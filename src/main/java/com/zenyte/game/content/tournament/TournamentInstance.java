package com.zenyte.game.content.tournament;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.content.tournament.plugins.TournamentLobby;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.plugins.*;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfBoundaryException;
import com.zenyte.game.world.region.dynamicregion.OutOfSpaceException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static com.zenyte.game.world.entity.player.Player.DEATH_ANIMATION;

/**
 * @author Kris | 26/05/2019 19:56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public final class TournamentInstance extends DynamicArea implements DeathPlugin, EntityAttackPlugin, TradePlugin, DropPlugin, PrayerPlugin, TeleportPlugin, TeleportMovementPlugin,
        ExperiencePlugin, LogoutPlugin {

    private static final ForceTalk FIGHT = new ForceTalk("FIGHT!");

    @NotNull @Getter
    private final Tournament tournament;
    @NotNull
    private final Queue<Pair<Player, Player>> pairs;
    @Getter private final Map<Pair<Player, Player>, Location> pairCastleMap = new Object2ObjectOpenHashMap<>();
    @Getter private final Map<Pair<Player, Player>, List<Player>> spectatorMap = new Object2ObjectOpenHashMap<>();
    private boolean countdown;

    private TournamentInstance(@NotNull final Tournament tournament, @NotNull final AllocatedArea allocatedArea, @NotNull final Queue<Pair<Player, Player>> pairs) {
        super(allocatedArea, 474, 1130);
        this.tournament = tournament;
        this.pairs = pairs;
    }

    @NotNull
    public static final TournamentInstance build(@NotNull final Tournament tournament) {
        try {
            val pairs = tournament.getPairs();
            val width = (int) Math.floor(Math.sqrt(pairs.size()));
            val height = (int) Math.ceil(Math.sqrt(pairs.size()));
            val allocatedArea = MapBuilder.findEmptyChunk(width * 4, height * 4);
            val instance = new TournamentInstance(tournament, allocatedArea, pairs);
            instance.constructRegion();
            return instance;
        } catch (OutOfSpaceException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void constructRegion() {
        if (constructed) {
            return;
        }
        GlobalAreaManager.add(this);
        try {
            val count = pairs.size();
            int currentIndex = 0;
            val length = (int) Math.ceil(Math.sqrt(pairs.size()));
            loop:
            for (int x = 0; x < length; x++) {
                for (int y = 0; y < length; y++) {
                    MapBuilder.copySquare(area, 4, this.staticChunkX, this.staticChunkY, 0, (x * 4) + area.getChunkX(), (y * 4) + area.getChunkY(), 0, 0);
                    MapBuilder.copySquare(area, 4, this.staticChunkX, this.staticChunkY, 1, (x * 4) + area.getChunkX(), (y * 4) + area.getChunkY(), 1, 0);
                    if (++currentIndex >= count) {
                        break loop;
                    }
                }
            }
        } catch (OutOfBoundaryException e) {
            log.error(Strings.EMPTY, e);
        }
        constructed = true;
        constructed();
    }

    @Override
    public void constructed() {
        val count = pairs.size();
        int currentIndex = 0;
        val pairsList = new ArrayList<>(pairs);
        val length = (int) Math.ceil(Math.sqrt(pairs.size()));
        countdown = true;
        loop:
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < length; y++) {
                val pair = pairsList.get(currentIndex);
                assert pair != null;
                val left = pair.getLeft();
                val right = pair.getRight();
                val dynamicCorner = new Location((((x * 4) + area.getChunkX()) << 3), (((y * 4) + area.getChunkY()) << 3), 0);
                val spectatorTile = dynamicCorner.transform(14, 16, 0);
                val leftLocation = dynamicCorner.transform(12, 16, 0);
                val rightLocation = dynamicCorner.transform(17, 16, 0);
                pairCastleMap.put(pair, spectatorTile);
                left.stop(Player.StopType.INTERFACES, Player.StopType.ROUTE_EVENT, Player.StopType.WALK, Player.StopType.ACTIONS, Player.StopType.ANIMATIONS,
                        Player.StopType.WORLD_MAP);
                right.stop(Player.StopType.INTERFACES, Player.StopType.ROUTE_EVENT, Player.StopType.WALK, Player.StopType.ACTIONS, Player.StopType.ANIMATIONS,
                        Player.StopType.WORLD_MAP);
                left.lock(1);
                right.lock(1);
                left.setLocation(leftLocation);
                right.setLocation(rightLocation);
                left.setFaceEntity(right);
                right.setFaceEntity(left);
                left.getVariables().resetScheduled();
                right.getVariables().resetScheduled();
                left.reset();
                right.reset();
                left.getPrayerManager().deactivateActivePrayers();
                right.getPrayerManager().deactivateActivePrayers();
                WorldTasksManager.schedule(() -> {
                    left.refreshDirection();
                    right.refreshDirection();
                    left.setFaceEntity(null);
                    right.setFaceEntity(null);
                });
                GameInterface.WILDERNESS_OVERLAY.open(left);
                GameInterface.WILDERNESS_OVERLAY.open(right);
                left.getInterfaceHandler().closeInterface(InterfacePosition.MINIGAME_OVERLAY);
                right.getInterfaceHandler().closeInterface(InterfacePosition.MINIGAME_OVERLAY);
                WorldTasksManager.schedule(new WorldTask() {
                    int ticks = 20;

                    @Override
                    public void run() {
                        if (ticks != 0 && ticks % 2 == 0) {
                            left.setForceTalk(new ForceTalk("" + (ticks / 2)));
                            right.setForceTalk(new ForceTalk("" + (ticks / 2)));
                        } else if (ticks == 0) {
                            left.setForceTalk(FIGHT);
                            right.setForceTalk(FIGHT);
                            countdown = false;
                            tournament.getLobby().schedule(pair, 60 * 10); // 10 minutes
                            stop();
                        }
                        ticks--;
                    }
                }, 0, 0);
                if (++currentIndex >= count) {
                    break loop;
                }
            }
        }
    }

    @Override
    public void enter(final Player player) {
        //Only allow the user to fight if they're not spectating.
        if (player.getTemporaryAttributes().get("tournament_spectating") == null) {
            player.setCanPvp(true);
        }
        player.findPlayerOption("Trade with").ifPresent(value -> player.setPlayerOption(value, null, false));
    }

    @Override
    public void leave(final Player player, final boolean logout) {
        player.setPlayerOption(4, "Trade with", false);
        player.setCanPvp(false);
        tournament.getLobby().clear(player);
        tournament.remove(player);
    }

    @Override
    public String name() {
        return "Tournament Zone";
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
    public Location onLoginLocation() {
        return new Location(3091, 3502, 0);//TODO
    }

    @Override
    public boolean attack(final Player player, final Entity entity) {
        if (countdown) {
            player.sendMessage("The fight has not started yet!");
            return false;
        }
        return true;
    }

    @Override
    public boolean sendDeath(final Player player, final Entity source) {
        val other = tournament.findPair(player);
        if (other != null) {
            val otherPlayer = other.getLeft() == player ? other.getRight() : other.getLeft();
            //Completely heal the opponent up.
            otherPlayer.blockIncomingHits();
            otherPlayer.reset();
            otherPlayer.setGraphics(new Graphics(1177));
        }

        player.setAnimation(null);
        player.lock();
        player.stopAll();
        if (player.getPrayerManager().isActive(Prayer.RETRIBUTION)) {
            player.getPrayerManager().applyRetributionEffect(source);
        }
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                if (player.isFinished() || player.isNulled()) {
                    stop();
                    return;
                }
                if (ticks == 1) {
                    player.setAnimation(DEATH_ANIMATION);
                } else if (ticks == 4) {
                    tournament.getLobby().clear(player);
                    player.reset();
                    tournament.remove(player);
                    player.sendMessage("Oh dear, you have died.");
                    player.sendMessage("You have been eliminated from the tournament in round " + tournament.getRound() + "; " +
                            "The tournament has " + tournament.getParticipants().size() + " participant" + (tournament.getParticipants().size() == 1 ? "" : "s") + " remaining.");
                    player.setAnimation(Animation.STOP);
                    player.getVariables().setSkull(false);
                    player.setLocation(tournament.getLobby().getLocation(TournamentLobby.RESPAWN_LOCATION));
                } else if (ticks == 5) {
                    player.unlock();
                    player.setAnimation(Animation.STOP);
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
        return true;
    }

    @Override
    public void onLogout(final Player player) {
        tournament.remove(player);
    }

    @Override
    public Location getRespawnLocation() {
        return tournament.getLobby().getLocation(TournamentLobby.RESPAWN_LOCATION);
    }

    @Override
    public boolean canTrade(final Player player, final Player partner) {
        return false;
    }

    @Override
    public boolean dropOnGround(final Player player, final Item item) {
        return true;
    }

    @Override
    public int visibleTicks(final Player player, final Item item) {
        return 0;
    }

    @Override
    public int invisibleTicks(final Player player, final Item item) {
        return 300;
    }

    @Override
    public boolean activatePrayer(final Player player, final Prayer prayer) {
        if (ArrayUtils.contains(this.tournament.getLobby().getPreset().getDisabledPrayers(), prayer)) {
            player.sendMessage("You may not use this prayer during this tournament.");
            return false;
        }
        return true;
    }

    @Override
    public boolean canTeleport(final Player player, final Teleport teleport) {
        return false;
    }

    @Override
    public boolean canTeleport(final Player player, final Location destination) {
        if (player.getTemporaryAttributes().get("tournament_spectating") != null || tournament.getParticipants().contains(player) || GlobalAreaManager.getArea(destination) != this) {
            return true;
        }
        player.sendMessage("Invalid teleport! You may not enter tournaments illegally!");
        return false;
    }

    @Override
    public void processMovement(final Player player, final Location destination) {

    }

    @Override
    public boolean enabled() {
        return false;
    }
}
