package com.zenyte.game.content.godwars.instance;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.godwars.GodwarsInstanceManager;
import com.zenyte.game.content.godwars.npcs.GodwarsBossMinion;
import com.zenyte.game.content.godwars.npcs.KillcountNPC;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.godwars.GodwarsDungeonArea;
import com.zenyte.game.world.region.area.plugins.*;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.game.world.region.dynamicregion.OutOfBoundaryException;
import com.zenyte.plugins.events.ClanLeaveEvent;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.zenyte.game.world.entity.player.Player.DEATH_ANIMATION;

/**
 * @author Kris | 14/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public abstract class GodwarsInstance extends DynamicArea implements FullMovementPlugin, TeleportMovementPlugin, DeathPlugin, DropPlugin, LogoutPlugin, CannonRestrictionPlugin, LootBroadcastPlugin {
    final String clan;
    @Getter final KillcountNPC.GodType god;

    static final int STATUS_VAR = 3625;

    RSPolygon polygon;
    GodwarsBossMinion[] minions;
    final List<NPC> killcountNPCS = new ObjectArrayList<>();
    abstract IntList possibleKillcountMonsters();
    abstract List<Location> possibleSpawnTiles();

    GodwarsInstance(final String clan, final KillcountNPC.GodType god, AllocatedArea allocatedArea, int copiedChunkX, int copiedChunkY) {
        super(allocatedArea, copiedChunkX, copiedChunkY);
        this.god = god;
        this.clan = clan;
    }

    @Subscribe
    public static final void onClanLeave(final ClanLeaveEvent event) {
        val player = event.getPlayer();
        if (!(player.getArea() instanceof GodwarsInstance)) {
            return;
        }
        player.sendMessage(Colour.RED.wrap("As you are no longer part of the clan, you will be removed from the instance within the next 3-10 seconds."));
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (++ticks >= 100 || player.isNulled() || player.isFinished()) {
                    stop();
                    return;
                }
                if (player.isDead() || player.isLocked()) {
                    return;
                }
                val area = player.getArea();
                if (!(area instanceof GodwarsInstance)) {
                    stop();
                    return;
                }
                player.lock(1);
                player.stopAll();
                player.sendMessage(Colour.RED.wrap("You have been removed from the instance."));
                player.blockIncomingHits();
                player.setLocation(new Location(((GodwarsInstance) area).onLoginLocation()));
                stop();
            }
        }, Utils.random(5, 15), 0);
    }

    public abstract Location onLoginLocation();
    abstract void destroyed();

    @Override
    public void constructed() {
        val possibleSpawnTiles = possibleSpawnTiles();
        val possibleKillcountMonsters = possibleKillcountMonsters();
        val tiles = new ObjectArrayList<Location>(possibleSpawnTiles.size());
        for (val tile : possibleSpawnTiles) {
            tiles.add(getLocation(tile));
        }
        for (val id : possibleKillcountMonsters) {
            val npc = World.spawnNPC(id, tiles.remove(Utils.random(tiles.size() - 1)), Direction.SOUTH, 5);
            if (npc == null) {
                continue;
            }
            killcountNPCS.add(npc);
        }
    }

    @Override
    public final void constructRegion() {
        if (isConstructed()) {
            return;
        }
        setConstructed(true);
        //Build the instance, if this fails, an exception is thrown and the area gets destroyed, and this method stops executing.
        build();
        GodwarsInstanceManager.getManager().addInstance(clan, this);
        GlobalAreaManager.add(this);
        constructed();
    }

    @Override
    public final void destroyRegion() {
        if (isDestroyed() || !players.isEmpty()) {
            return;
        }
        setDestroyed(true);
        try {
            GodwarsInstanceManager.getManager().removeInstance(clan, this);
            destroyed();
            GlobalAreaManager.remove(this);
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
        destroy();
    }

    /**
     * Build the map. If this process fails, destroy whatever remnants are actually built, and throw another exception
     * to interrupt the flow of the code which would register the area officially.
     */
    protected void build() {
        try {
            MapBuilder.copyAllPlanesMap(area, staticChunkX, staticChunkY, chunkX, chunkY, sizeX, sizeY);
        } catch (OutOfBoundaryException e) {
            destroyRegion();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void enter(final Player player) {
        player.setViewDistance(Player.SCENE_DIAMETER);
        GodwarsDungeonArea.enterArea(player);
        updateStatus(player);
    }

    protected void updateStatus(@NotNull final Player player) {
        player.getVarManager().sendVar(STATUS_VAR, 1);
    }

    @Override
    public void leave(final Player player, boolean logout) {
        player.resetViewDistance();
        GodwarsDungeonArea.leaveArea(player);
        player.getVarManager().sendVar(STATUS_VAR, 0);
    }

    /**
     * Attempt to destroy the area, log the exceptions if any occur.
     */
    private final void destroy() {
        try {
            MapBuilder.destroy(area);
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public RSPolygon chamberPolygon() {
        return polygon;
    }

    @Override
    public boolean processMovement(final Player player, final int x, final int y) {
        val polygon = chamberPolygon();
        if (!polygon.contains(player.getX(), player.getY())) {
            if (polygon.contains(x, y)) {
                GodwarsDungeonArea.enterArea(player);
            }
        } else {
            if (!polygon.contains(x, y)) {
                GodwarsDungeonArea.leaveArea(player);
            }
        }
        return true;
    }

    @Override
    public void processMovement(final Player player, final Location destination) {
        processMovement(player, destination.getX(), destination.getY());
    }

    @Override
    public boolean sendDeath(Player player, Entity source) {
        player.setAnimation(Animation.STOP);
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
                if (ticks == 0) {
                    player.setAnimation(DEATH_ANIMATION);
                } else if (ticks == 2) {
                    player.getDeathMechanics().service(ItemRetrievalService.RetrievalServiceType.GODWARS, source);
                    player.sendMessage("Oh dear, you have died.");
                    player.reset();
                    player.setAnimation(Animation.STOP);
                    player.sendMessage("The dying knight has retrieved some of your items. You can collect them from him in any of the godwars lobby areas.");
                    ItemRetrievalService.updateVarps(player);
                    if (player.getVariables().isSkulled()) {
                        player.getVariables().setSkull(false);
                    }
                    player.setLocation(player.getRespawnPoint().getLocation());
                } else if (ticks == 3) {
                    player.unlock();
                    player.getAppearance().resetRenderAnimation();
                    player.setAnimation(Animation.STOP);
                    stop();
                }
                ticks++;
            }
        }, 0, 1);
        return true;
    }
    @Override
    public boolean isSafe() {
        return false;
    }

    @Override
    public String getDeathInformation() {
        return null;
    }

    @Override
    public Location getRespawnLocation() {
        return null;
    }

    @Override
    public boolean manualLogout(final Player player) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                options("Are you sure you wish to log out?<br>" +
                        "If there are no players left, " + Colour.RED.wrap("the instance will be destroyed") + ".",
                        new DialogueOption("Yes", () -> player.logout(false)), new DialogueOption("No."));
            }
        });
        return false;
    }

}
