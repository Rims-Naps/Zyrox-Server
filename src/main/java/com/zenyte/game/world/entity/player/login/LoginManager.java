package com.zenyte.game.world.entity.player.login;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenyte.Constants;
import com.zenyte.api.client.query.AccountInformationRequest;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.content.skills.farming.Farming;
import com.zenyte.game.content.skills.farming.FarmingSpot;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerInformation;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.entity.player.VarManager;
import com.zenyte.game.world.entity.player.container.impl.bank.Bank;
import com.zenyte.game.world.entity.player.dailychallenge.ChallengeAdapter;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.DailyChallenge;
import com.zenyte.game.world.entity.player.perk.Perk;
import com.zenyte.game.world.entity.player.perk.PerkAdapter;
import com.zenyte.plugins.PluginManager;
import com.zenyte.plugins.events.InitializationEvent;
import com.zenyte.plugins.events.PostInitializationEvent;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

/**
 * @author Kris | 25/02/2019 00:02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class LoginManager {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginManager.class);


    private enum Response {
        VOID, CANCELLED;
    }

    /**
     * A set of usernames of the characters who have been modified since the last backup occurred.
     */
    final Set<String> modifiedCharacters = ObjectSets.synchronize(new ObjectOpenHashSet<>(1000));
    /**
     * A map of cached loaded up players; the player is cached whenever it is loaded up externally outside of a login. The entry is cleared if either more than 10 minutes have
     * passed since its caching, or the user logs in.
     */
    private static final Map<String, CachedEntry> cachedPlayers = new ConcurrentHashMap<>();
    /**
     * The player save directory that all serialized files are placed.
     */
    static final Path PLAYER_SAVE_DIRECTORY = Path.of("data", "characters");
    /**
     * The file format extension we are serailizing information in.
     */
    private static final String EXTENSION = ".json";
    /**
     * The gson object we initiate upon calling our game loader.
     */
    private static final ThreadLocal<Gson> gson = ThreadLocal.withInitial(
            () -> new GsonBuilder()
                    .registerTypeAdapter(Perk.class, new PerkAdapter())
                    .registerTypeAdapter(FarmingSpot.class, Farming.deserializer())
                    .registerTypeAdapter(VarManager.class, VarManager.deserializer())
                    .disableHtmlEscaping()
                    .registerTypeAdapter(DailyChallenge.class, new ChallengeAdapter())
                    .create());
    /**
     * The forkjoin pool that will be processing load and save requests of the characters.
     */
    private final ForkJoinPool pool = new ForkJoinPool();
    /**
     * A concurrent queue used for passing on requests to load the user; thread safety reasons.
     */
    private final Queue<Function<Void, Response>> loadRequests = new ConcurrentLinkedQueue<>();
    /**
     * A concurrent queue used for passing on requests to save the user; thread safety reasons.
     */
    private final Queue<Runnable> saveRequests = new ConcurrentLinkedQueue<>();
    /**
     * A map for user saving requests, wherein key is the username of the account which will also be the name of the
     * file we save, thus eliminating any sort of overwriting or other sort of problems. Additionally ensures there
     * are no duplicate requests.
     */
    private final Map<String, Callable<Void>> saveRequestMap = new Object2ObjectOpenHashMap<>();
    /**
     * A map for user load requests, wherein key is the username of the account which will also be the name of the
     * file we load, thus eliminating any sort of problems with file loading. Additionally ensures there are no
     * duplicate requests.
     */
    private final Map<String, Callable<Void>> loadRequestsMap = new Object2ObjectOpenHashMap<>();
    /**
     * The current stage of the login system, starting off in the {@link ShutdownStage#RUNNING} stage.
     */
    @NotNull
    private ShutdownStage shutdownStage = ShutdownStage.RUNNING;
    public static final Object writeLock = new Object();
    /**
     * The default thread sleep frequency - if there are no pending requests, the thread will sleep for the defined
     * duration(milliseconds).
     */
    private static final int THREAD_SLEEP_FREQUENCY = 20;
    /**
     * The maximum number of requests the server may process per a single tick.
     */
    private static final int MAXIMUM_ALLOWED_REQUESTS_PER_SECOND = (int) TimeUnit.SECONDS.toMillis(1) / THREAD_SLEEP_FREQUENCY;
    /**
     * The remaining allowed logins count at this moment.
     */
    private int loads = MAXIMUM_ALLOWED_REQUESTS_PER_SECOND;
    private MutableBoolean sleeping = new MutableBoolean(false);
    /**
     * The thread that executes the loading and saving of the accounts.
     */
    private Thread thread;
    private final Set<Player> awaitingSave = new ObjectOpenHashSet<>();
    MutableBoolean status = new MutableBoolean();

    static {
        if (Files.notExists(PLAYER_SAVE_DIRECTORY)) {
            try {
                Files.createDirectories(PLAYER_SAVE_DIRECTORY);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Launches the login processing.
     */
    public void launch() {
        thread = new Thread(new Runnable() {
            private long timer;
            private final int interval = 1000 / THREAD_SLEEP_FREQUENCY;
            @Override
            public void run() {
                while (true) {
                    try {
                        sleeping.setTrue();
                        while (loadRequests.isEmpty() && saveRequests.isEmpty()) {
                            Thread.sleep(THREAD_SLEEP_FREQUENCY);
                        }

                        sleeping.setFalse();
                        LoginManager.this.process();
                        if (LoginManager.this.isShutdown()) {
                            return;
                        }
                        if (timer++ % interval == 0) {
                            cachedPlayers.values().removeIf(cachedEntry -> cachedEntry.getTime() < (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10)) || cachedEntry.getCachedAccount().isNulled());
                        }
                    } catch (Exception e) {
                        log.error(Strings.EMPTY, e);
                    }
                }
            }
        });
        thread.start();
        CoresManager.getServiceProvider().scheduleRepeatingTask(this::increment, 1, 1);
    }

    /**
     * Increments the allowed logins counter if it is below the allowed maximum per second.
     */
    private void increment() {
        loads = MAXIMUM_ALLOWED_REQUESTS_PER_SECOND;
    }

    /**
     * Decrements the allowed logins counted per second if it is above 0.
     *
     * @return whether or not the logins counter was decremented.
     */
    private boolean decrementLoads() {
        if (isShutdown() || loads <= 0) {
            return false;
        }
        loads--;
        return true;
    }

    /**
     * Decrements the allowed logins counted per second if it is above 0.
     *
     * @return whether or not the logins counter was decremented.
     */
    private boolean decrementSaves() {
        if (shutdownStage == ShutdownStage.SHUTTING_DOWN) {
            return true;
        }
        return !isShutdown();
    }

    /**
     * Passes on a request to save the player. The request is then placed inside the save requests map which ensures
     * there are no duplicates - which will then be processed whenever {@link this#process()} is referenced.
     *
     * @param player the player whose character is being saved.
     */
    public void save(@NotNull final Player player) {
        if (player.isNulled()) {
            log.warn("Not saving player {} because the player is nulled.", player);
            return;
        }
        modifiedCharacters.add(player.getUsername());
        saveRequests.add(() -> saveRequestMap.put(player.getUsername(), () -> {
            try {
                savePlayer(player);
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            return null;
        }));
    }

    /**
     * Submits a save request to execute at the end of the tick.
     * @param player the player submitted.
     */
    public void submitSaveRequest(@NotNull final Player player) {
        awaitingSave.add(player);
    }

    /**
     * Passes on a request to load the player. The request is then placed inside the load requests map which ensures
     * there are no duplicates - which will then be processed whenever {@link this#process()} is referenced.
     *
     * @param playerInformation the user details of the character being loaded.
     * @param consumer          the consumer that will use the loaded player object.
     */
    public void load(final long time, @NotNull final PlayerInformation playerInformation, @NotNull final Consumer<Player> consumer) {
        final java.lang.String username = Utils.formatUsername(playerInformation.getUsername());
        loadRequests.add(voidObject -> {
            if (World.getPlayers().size() >= 2000 || System.currentTimeMillis() - time >= java.util.concurrent.TimeUnit.SECONDS.toMillis(30)) {
                return Response.CANCELLED;
            }
            if (!playerInformation.getSession().getChannel().isOpen()) {
                return Response.CANCELLED;
            }
            loadRequestsMap.put(username, () -> {
                try {
                    if (World.getPlayers().size() >= 2000 || System.currentTimeMillis() - time >= java.util.concurrent.TimeUnit.SECONDS.toMillis(30)) {
                        return null;
                    }
                    if (!playerInformation.getSession().getChannel().isOpen()) {
                        return null;
                    }
                    final com.zenyte.game.world.entity.player.Player existingPlayer = getPlayer(username);
                    final com.zenyte.game.world.entity.player.Player player = new Player(playerInformation, existingPlayer == null ? null : existingPlayer.getAuthenticator());
                    final com.zenyte.api.client.query.AccountInformationRequestResults forumAccountInfo = Constants.WORLD_PROFILE.getApi().isEnabled() ? new AccountInformationRequest(username).execute() : null;
                    if (existingPlayer == null) {
                        if (Constants.isOwner(player)) {
                            player.setPrivilege(Privilege.SPAWN_ADMINISTRATOR);
                        }
                        if (forumAccountInfo != null) {
                            player.addAttribute("set registration date", 1);
                            player.addAttribute("forum registration date", forumAccountInfo.getJoinDate());
                            player.addAttribute("unread message count", forumAccountInfo.getUnreadMessageCount());
                            player.addTemporaryAttribute("hashed password", forumAccountInfo.getHashedPassword());
                            player.addTemporaryAttribute("two factor authentication", forumAccountInfo.isTwoFactorAuthentication());
                            playerInformation.setUserIdentifier(forumAccountInfo.getMemberId());
                        }
                        player.getPlayerInformation().setPlayerInformation(playerInformation);
                        setDefaults(player);
                    } else {
                        if (Constants.isOwner(player)) {
                            player.setPrivilege(Privilege.SPAWN_ADMINISTRATOR);
                        }
                        setFields(player, existingPlayer);
                        if (forumAccountInfo != null) {
                            if (player.getNumericAttribute("set registration date").intValue() == 0) {
                                player.addAttribute("set registration date", 1);
                                player.addAttribute("forum registration date", forumAccountInfo.getJoinDate());
                            }
                            if (player.getPlayerInformation().getUserIdentifier() == -1) {
                                player.getPlayerInformation().setUserIdentifier(forumAccountInfo.getMemberId());
                            }
                            player.addAttribute("unread message count", forumAccountInfo.getUnreadMessageCount());
                            player.addTemporaryAttribute("hashed password", forumAccountInfo.getHashedPassword());
                            player.addTemporaryAttribute("two factor authentication", forumAccountInfo.isTwoFactorAuthentication());
                        }
                    }
                    cachedPlayers.remove(username);
                    consumer.accept(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    consumer.accept(null);
                }
                return null;
            });
            return Response.VOID;
        });
    }

    /**
     * Passes on a request to load the player. The request is then placed inside the load requests map which ensures
     * there are no duplicates - which will then be processed whenever {@link this#process()} is referenced.
     * The difference between this method and {@link this#load(long, PlayerInformation, Consumer)} is that this one simply
     * loads the file through json and doesn't do anything with it, unlike the latter which will construct a new
     * player object, set the fields correctly as well as sets the defaults if the account is new.
     * @param requestedUsername the username of the player requested.
     * @param sync              whether or not the consumer function should be synchronized through a world task, or
     *                          executed directly from the {@link this#pool}
     * @param consumer          the optional consumer that will use the loaded player object.
     */
    public void load(@NotNull final String requestedUsername, final boolean sync, @NotNull final Consumer<Optional<Player>> consumer) {
        final java.lang.String username = Utils.formatUsername(requestedUsername);
        final com.zenyte.game.world.entity.player.login.CachedEntry cached = cachedPlayers.get(username);
        if (cached != null && !cached.getCachedAccount().isNulled()) {
            consumer.accept(Optional.ofNullable(cached.getCachedAccount()));
            return;
        }
        loadRequests.add(voidObject -> {
            loadRequestsMap.put(username, () -> {
                try {
                    final com.zenyte.game.world.entity.player.Player player = getPlayer(username);
                    if (player != null) {
                        cachedPlayers.put(username, new CachedEntry(System.currentTimeMillis(), player));
                    }
                    WorldTasksManager.scheduleOrExecute(() -> consumer.accept(Optional.ofNullable(player)), sync ? 0 : -1);
                } catch (Exception e) {
                    System.err.println(requestedUsername + " FAILED REQUEST");
                    e.printStackTrace();
                }
                return null;
            });
            return Response.VOID;
        });
    }

    /**
     * Sets the default fields for the newly-created account.
     *
     * @param player the player that was created.
     */
    private void setDefaults(final Player player) {
        final com.zenyte.game.world.entity.player.PlayerInformation info = player.getPlayerInformation();
        final java.lang.String addr = info.getSession().getChannel().remoteAddress().toString();
        info.setIp(addr.substring(1, addr.indexOf(":")));
        player.setDefaultSettings();
    }

    /**
     * Processes all of the requests pushed, which maps them accordingly to either save or load requests. Continues
     * on to process the save requests if there are any - blocking the thread until all of the requests have actually
     * finished executing. Finally, continues off to process all of the load requests.
     */
    public void process() {
        synchronized (writeLock) {
            status.setTrue();
            Runnable request;
            while (decrementSaves() && (request = saveRequests.poll()) != null) {
                request.run();
            }
            Function<Void, Response> function;
            while ((function = loadRequests.poll()) != null) {
                final com.zenyte.game.world.entity.player.login.LoginManager.Response returnCode = function.apply(null);
                if (returnCode == Response.CANCELLED) {
                    continue;
                }
                if (!decrementLoads()) {
                    break;
                }
            }
            if (!saveRequestMap.isEmpty()) {
                pool.invokeAll(saveRequestMap.values());
                saveRequestMap.clear();
            }
            if (!loadRequestsMap.isEmpty()) {
                pool.invokeAll(loadRequestsMap.values());
                loadRequestsMap.clear();
            }
            if (shutdownStage == ShutdownStage.SHUTTING_DOWN && loadRequests.isEmpty()) {
                shutdownStage = ShutdownStage.SHUT_DOWN;
            }
            status.setFalse();
        }
    }

    public void waitForShutdown() {
        if (shutdownStage.equals(ShutdownStage.RUNNING)) {
            throw new IllegalStateException("Cannot request for shutdown waiting until the shutdown has commenced.");
        }
        while (!shutdownStage.equals(ShutdownStage.SHUT_DOWN)) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                log.error(Strings.EMPTY, e);
            }
        }
    }

    /**
     * Sets the stage to shutting down, indicating the services should no longer be accepting new load requests.
     */
    public void shutdown() {
        if (shutdownStage == ShutdownStage.RUNNING) {
            shutdownStage = ShutdownStage.SHUTTING_DOWN;
        }
    }

    /**
     * Checks to see if the services have finally shut down.
     *
     * @return whether or not the services have shut down.
     */
    private boolean isShutdown() {
        return shutdownStage == ShutdownStage.SHUT_DOWN;
    }

    /**
     * Loads up the player object based on the input username. Returns null if the player doesn't exist.
     *
     * @param username the username of the player.
     * @return the player object.
     */
    private Player getPlayer(@NotNull final String username) {
        final Gson gson = LoginManager.gson.get();

        final String formattedUsername = Utils.formatUsername(username);
        final String fileName = formattedUsername + EXTENSION;
        final Path filePath = PLAYER_SAVE_DIRECTORY.resolve(fileName);
        if (Files.notExists(filePath)) {
            return null;
        }
        try (final BufferedReader reader = Files.newBufferedReader(filePath)) {
            return gson.fromJson(reader, Player.class);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Writes the player object in json to the output folder.
     *
     * @param player the player object being written.
     */
    public void savePlayer(final Player player) {
        if (player.isNulled()) {
            return;
        }

        final String json = gson.get().toJson(player);

        final String username = player.getUsername();
        final String fileName = username + EXTENSION;
        try {
            final Path tempFile = Files.createTempFile(PLAYER_SAVE_DIRECTORY, fileName, ".tmp");
            Files.writeString(tempFile, json, StandardCharsets.UTF_8);

            final Path finalFile = PLAYER_SAVE_DIRECTORY.resolve(fileName);
            Files.move(tempFile, finalFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }

        final Runnable runnable = player.getPostSaveFunction();
        if (runnable != null) {
            CoresManager.getServiceProvider().executeWithDelay(runnable, 300);
        }
    }


    /**
     * An enum containing the shutdown stages used for the forkjoinpool to determine whether or not the system has
     * fully shut down.
     */
    private enum ShutdownStage {
        RUNNING, SHUTTING_DOWN, SHUT_DOWN;
    }

    /**
     * Sets the serialized fields of a player based on the deserialized fields from the loaded account.
     *
     * @param player the constructed played that will be logging in.
     * @param parser the loaded deserialized account.
     */
    private static void setFields(final Player player, final Player parser) {
        player.setLastLocation(parser.getLocation());
        player.getPlayerInformation().setPlayerInformation(parser.getPlayerInformation());
        player.setBank(new Bank(player, parser.getBank()));
        player.forceLocation(new Location(parser.getLocation()));
        player.setFarming(parser.getFarming());
        player.getToxins().initialize(parser.getToxins());
        player.setPetId(parser.getPetId());
        player.getSkills().setSkills(parser.getSkills());
        player.getCombatDefinitions().setSpellbook(parser.getCombatDefinitions().getSpellbook(), false);
        player.getInventory().setInventory(parser.getInventory());
        player.getEquipment().setEquipment(parser.getEquipment());
        player.getSlayer().initialize(player, parser);
        player.getAchievementDiaries().initialize(player, parser);
        player.getVariables().set(parser.getVariables());
        player.getPrayerManager().setPrayer(parser.getPrayerManager());
        player.init(parser);
        player.getInterfaceHandler().initialize(parser.getInterfaceHandler());
        player.getBossTimer().setBossTimers(parser.getBossTimer());
        player.getMusic().getUnlockedTracks().putAll(parser.getMusic().getUnlockedTracks());
        if (parser.getTolerancePositionQueue() != null) {
            player.getTolerancePositionQueue().addAll(parser.getTolerancePositionQueue());
        }
        player.getSettings().initialize(parser.getSettings());
        player.getAppearance().initialize(parser.getAppearance());
        player.getSocialManager().initalize(parser.getSocialManager());
        player.getControllerManager().initalize(parser.getControllerManager());
        player.getCombatDefinitions().initialize(parser.getCombatDefinitions());
        player.getRunePouch().initialize(parser.getRunePouch());
        player.getSeedBox().initialize(parser.getSeedBox());
        player.getLootingBag().initialize(parser.getLootingBag());
        player.getHerbSack().initialize(parser.getHerbSack());
        player.getGemBag().initialize(parser.getGemBag());
        player.getGrandExchange().initialize(parser.getGrandExchange());
        player.getTeleportManager().initialize(parser.getTeleportManager());
        player.getPetInsurance().initialize(parser.getPetInsurance());
        player.getPerkManager().initialize(parser.getPerkManager());
        player.getAttributes().putAll(parser.getAttributes());
        if (player.getGrotesqueGuardiansInstance() == null) {
            player.setGrotesqueGuardiansInstance(Optional.empty());
        }
        PluginManager.post(new InitializationEvent(player, parser));
        PluginManager.post(new PostInitializationEvent(player));
    }

    /**
     * The thread that executes the loading and saving of the accounts.
     */
    public Thread getThread() {
        return this.thread;
    }

    public Set<Player> getAwaitingSave() {
        return this.awaitingSave;
    }
}