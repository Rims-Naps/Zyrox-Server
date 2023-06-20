package com.zenyte.game.content.tournament.plugins;

import com.zenyte.game.content.skills.magic.spells.MagicSpell;
import com.zenyte.game.content.skills.magic.spells.lunar.SpellbookSwap;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.tournament.Tournament;
import com.zenyte.game.content.tournament.TournamentInstance;
import com.zenyte.game.content.tournament.preset.TournamentPreset;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.plugins.*;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import com.zenyte.utils.StaticInitializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Tommeh | 31/05/2019 | 20:26
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@StaticInitializer
@Slf4j
public class TournamentLobby extends DynamicArea implements FullMovementPlugin, TeleportMovementPlugin, LogoutPlugin, CycleProcessPlugin, TradePlugin, DropPlugin, TeleportPlugin,
        DeathPlugin, SpellPlugin {

    private static final Location LOBBY_CENTER = new Location(3807, 9252, 0);
    public static final Location RESPAWN_LOCATION = new Location(3811, 9236, 0);
    public static final Location SPECTATING_LOCATION = new Location(3806, 9244, 0);
    public static final Location WINNER_LOCATION = new Location(3807, 9242, 0);
    private static final Location GUARD_LOCATION = new Location(3805, 9244, 0);
    private static final Location enlistingsLocation = new Location(3091, 3502, 0);
    private static final int MIN_X = 3799;
    private static final int MAX_X = 3815;
    private static final int MIN_Y = 9245;
    private static final int MAX_Y = 9258;
    
    @Getter
    private final transient List<Player> lobbyPlayers;
    @Getter
    private final transient TournamentPreset preset;
    @Getter
    private Tournament tournament;
    @Getter
    private TournamentInstance fight;
    private int time;
    @Getter
    private Date date;
    
    public TournamentLobby(final AllocatedArea allocatedArea, final TournamentPreset preset) {
        super(allocatedArea, 472, 1152);
        this.preset = preset;
        lobbyPlayers = new ArrayList<>();
    }

    static {
        Area.onLoginAttachments.put("Tournament Zone", TournamentLobby::resetPlayerToPreviousState);
        Area.onLoginAttachments.put("Tournament Lobby", player -> {
            if (Objects.equals(player.getAttributes().get("was inside tournament lobby"), true)) {
                resetPlayerToPreviousState(player);
            }
        });
    }

    @Override
    public void constructed() {
        World.spawnNPC(10011, getLocation(GUARD_LOCATION), Direction.EAST, 0);
    }

    @Override
    public void enter(Player player) {
        checkLobby(player, player.getX(), player.getY());
        SpellbookSwap.checkSpellbook(player);
        refreshOverlay(player);
    }

    @Override
    protected void cleared() {
        if (tournament.isFinished()) {
            if (players.isEmpty()) {
                Tournament.tournaments.remove(tournament);
                destroyRegion();
                fight.destroyRegion();
            }
        }
    }

    /**
     * Execute this method when the timer reaches 0. Lock tournament down so players cannot enter it anymore(unless they're part of the tournament itself).
     */
    public final void createTournament(final int time, final Date date) {
        //assert players.size() >= 2 : "Need at least two players to start a tournament";
        this.time = (int) (time / 0.6);
        tournament = new Tournament(this);
        lobbyPlayers.clear();
        WorldTasksManager.schedule(this::beginFight, this.time);
        this.date = date;
    }

    public final void win(@NotNull final Player player, @NotNull final String message) {
        if (!player.inArea(name())) {
            player.setLocation(getRandomLobbyTile());
        }
        player.reset();
        player.getCombatDefinitions().setSpecialEnergy(100);
        player.lock(1);
        player.sendMessage(message);
        player.getInterfaceHandler().closeInterface(InterfacePosition.WILDERNESS_OVERLAY);
    }

    private final Location getRandomLobbyTile() {
        int count = 100;
        val center = getLocation(LOBBY_CENTER);
        while (--count > 0) {
            val tile = center.random(10);
            if (inLobby(tile.getX(), tile.getY()) && World.isFloorFree(tile, 1)) {
                return tile;
            }
        }
        return center.transform(0, -2, 0);
    }

    /**
     * Builds a new instance and teleports the first round players into it.
     */
    public final void beginFight() {
        if (tournament.getRound() <= 1 && lobbyPlayers.size() < 2) {
            tournament.setFinished(true);
            if (tournament.getRound() == 1 && lobbyPlayers.size() == 1) {
                //Declare winner.
                tournament.win(lobbyPlayers.get(0), null);
                return;
            }
            for (val player : players) {
                player.sendMessage(Colour.RS_RED.wrap("Tournament could not be started as there are not enough members in the lobby."));
                player.setLocation(enlistingsLocation.random(2));
            }
            return;
        }
        tournament.start(new ArrayList<>(lobbyPlayers));
        fight = TournamentInstance.build(tournament);
    }

    public final void teleportPlayer(@NotNull final Player player) {
        player.lock(1);
        player.setLocation(getLocation(RESPAWN_LOCATION));
    }

    @Override
    public void leave(Player player, boolean logout) {
        val nextArea = GlobalAreaManager.getArea(player.getLocation());
        if (nextArea != null && nextArea == fight) {
            return;
        }
        clear(player);
        player.getInterfaceHandler().closeInterface(InterfacePosition.MINIGAME_OVERLAY);
        player.getInterfaceHandler().closeInterfaces();
    }

    @Override
    public String name() {
        return "Tournament Lobby";
    }

    private void refreshPlayerCount() {
        for (val player : players) {
            player.getVarManager().sendVar(3611, lobbyPlayers.size());
        }
    }

    public void refreshRound() {
        for (val player : players) {
            player.getVarManager().sendVar(3612, tournament.getRound());
        }
    }

    @Override
    public boolean canTeleport(final Player player, final Location destination) {
        if (tournament.isFinished() || tournament.getRound() >= 1) {
            if (inLobby(destination.getX(), destination.getY()) && !lobbyPlayers.contains(player)) {
                player.sendMessage("Invalid teleport! You may not enter the tournament lobby illegally!");
                return false;
            }
        }
        return true;
    }
    
    private static final void resetPlayerToPreviousState(@NotNull final Player player) {
        player.getInterfaceHandler().closeInterface(InterfacePosition.WILDERNESS_OVERLAY);
        player.getInterfaceHandler().closeInterface(InterfacePosition.MINIGAME_OVERLAY);
        player.reset();
        player.getEquipment().clear();
        player.getInventory().clear();
        player.getCombatDefinitions().refresh();
        player.getRunePouch().getContainer().refresh(player);
        player.getAppearance().resetRenderAnimation();
//        player.getPresetManager().applyTournamentPreset();
    }
    
    private void refreshOverlay(final Player player) {
        if (tournament.isFinished()) {
            return;
        }
        player.getInterfaceHandler().sendInterface(InterfacePosition.MINIGAME_OVERLAY, 706);
        player.getPacketDispatcher().sendComponentText(706, 8, "Preset: " + preset);
        player.getVarManager().sendVar(3613, time - 1);
    }
    
    private void checkLobby(final Player player, final int x, final int y) {
        if (inLobby(x, y)) {
            if (!lobbyPlayers.contains(player)) {
                //Do not allow the user to use presets after the tournament has ended.
                if (tournament.isFinished()) {
                    return;
                }
                player.getAttributes().put("was inside tournament lobby", true);
//                player.getPresetManager().savePreset("Tournament", true);
                lobbyPlayers.add(player);
                refreshOverlay(player);
                try {
                    preset.apply(player);
                } catch (Exception e) {
                    log.error(Strings.EMPTY, e);
                }
            }
            refreshPlayerCount();
            refreshRound();
        } else {
            clear(player);
        }
        player.getInterfaceHandler().closeInterfaces();
    }

    public void clear(final Player player) {
        if (lobbyPlayers.remove(player)) {
            player.getAttributes().remove("was inside tournament lobby");
            resetPlayerToPreviousState(player);
            refreshPlayerCount();
            for (int x = getX(MIN_X); x <= getX(MAX_X); x += 8) {
                for (int y = getY(MIN_Y); y <= getY(MAX_Y); y += 8) {
                    val chunk = World.getChunk(x, y, 0);
                    val items = chunk.getFloorItems();
                    if (items == null) {
                        continue;
                    }
                    val itemList = new ArrayList<>(items);
                    for (val item : itemList) {
                        if (item.isVisibleTo(player) && inLobby(item.getLocation().getX(), item.getLocation().getY())) {
                            World.destroyFloorItem(item);
                        }
                    }
                }
            }
        }
    }

    public void schedule(final Pair<Player, Player> pair, int seconds) {
        seconds -= 1;
        pair.getLeft().getVarManager().sendVar(3613, (int) (seconds / 0.6));
        pair.getRight().getVarManager().sendVar(3613, (int) (seconds / 0.6));
    }

    public void schedule(int seconds, final Runnable runnable) {
        seconds -= 1;
        val ticks = (int) (seconds / 0.6);
        for (val player : lobbyPlayers) {
            player.getVarManager().sendVar(3613, ticks);
        }
        WorldTasksManager.schedule(runnable::run, ticks);
    }

    private boolean inLobby(final int x, final int y) {
        return x >= getX(MIN_X) && x <= getX(MAX_X) && y >= getY(MIN_Y) && y <= getY(MAX_Y);
    }

    @Override
    public Location onLoginLocation() {
        return enlistingsLocation.random(2);
    }

    @Override
    public boolean processMovement(Player player, int x, int y) {
        checkLobby(player, x, y);
        return true;
    }

    @Override
    public void processMovement(Player player, Location destination) {
        val nextArea = GlobalAreaManager.getArea(destination);
        if (nextArea != null && nextArea == fight) {
            return;
        }
        checkLobby(player, destination.getX(), destination.getY());
    }

    @Override
    public boolean canTrade(final Player player, final Player partner) {
        if (player.getAttributes().get("was inside tournament lobby") == null && partner.getAttributes().get("was inside tournament lobby") == null) {
            return true;
        }
        player.sendMessage("You cannot trade with someone inside a tournament!");
        return false;
    }

    @Override
    public boolean drop(final Player player, final Item item) {
        return true;
    }

    @Override
    public boolean dropOnGround(final Player player, final Item item) {
        return true;
    }

    @Override
    public int visibleTicks(final Player player, final Item item) {
        return !inLobby(player.getX(), player.getY()) ? 200 : -1;
    }

    @Override
    public int invisibleTicks(final Player player, final Item item) {
        return !inLobby(player.getX(), player.getY()) ? 100 : 300;
    }

    @Override
    public void process() {
        /*for (val player : players) {
            player.setForceTalk(new ForceTalk("" + (time * 0.6)));
        }*/
        if (time > 0) {
            time--;
        } /*else {
            if (tournament.getRound() == 0) {
                beginFight();
            }
        }*/
    }

    @Override
    public boolean canTeleport(final Player player, final Teleport teleport) {
        return !lobbyPlayers.contains(player);
    }

    @Override
    public boolean canCast(final Player player, final MagicSpell spell) {
        player.sendMessage("You cannot cast this spell in this area.");
        return false;
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    @Override
    public String getDeathInformation() {
        return "Items lost inside tournament-restricted areas are erased when the user leaves the tournament, which includes dying.";
    }

    @Override
    public Location getRespawnLocation() {
        return this.getLocation(RESPAWN_LOCATION);
    }
}
