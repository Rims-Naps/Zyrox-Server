package com.zenyte.game.content.boss.dagannothkings;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.content.ItemRetrievalService;
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
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.spawns.NPCSpawn;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.*;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import com.zenyte.plugins.events.ClanLeaveEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import static com.zenyte.game.world.entity.player.Player.DEATH_ANIMATION;
import static com.zenyte.game.world.entity.npc.NpcId.*;
/**
 * @author Kris | 18/06/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class DagannothKingInstance extends DynamicArea implements CannonRestrictionPlugin, DeathPlugin, LogoutPlugin, DropPlugin, LootBroadcastPlugin {

    protected DagannothKingInstance(final String username, AllocatedArea allocatedArea, int copiedChunkX, int copiedChunkY) {
        super(allocatedArea, copiedChunkX, copiedChunkY);
        this.username = username;
    }

    private final String username;

    private static final NPCSpawn[] spawns = new NPCSpawn[]{
            new NPCSpawn(SUSPICIOUS_WATER_5948, 2903, 4435, 0, Direction.SOUTH, 2), new NPCSpawn(5948, 2903, 4435, 0, Direction.SOUTH, 4),
            new NPCSpawn(SUSPICIOUS_WATER_5962, 2898, 4439, 0, Direction.SOUTH, 3), new NPCSpawn(5962, 2898, 4446, 0, Direction.SOUTH, 4),
            new NPCSpawn(SUSPICIOUS_WATER_5948, 2897, 4446, 0, Direction.SOUTH, 2), new NPCSpawn(5948, 2897, 4455, 0, Direction.SOUTH, 2),
            new NPCSpawn(SPINOLYP_5961, 2898, 4455, 0, Direction.SOUTH, 2), new NPCSpawn(5963, 2901, 4458, 0, Direction.SOUTH, 4),
            new NPCSpawn(SPINOLYP, 2901, 4459, 0, Direction.SOUTH, 2), new NPCSpawn(5946, 2903, 4464, 0, Direction.SOUTH, 4),
            new NPCSpawn(SPINOLYP_5961, 2910, 4434, 0, Direction.SOUTH, 2), new NPCSpawn(2265, 2905, 4443, 0, Direction.SOUTH, 3),
            new NPCSpawn(SPINOLYP, 2910, 4463, 0, Direction.SOUTH, 5), new NPCSpawn(5962, 2909, 4464, 0, Direction.SOUTH, 5),
            new NPCSpawn(SUSPICIOUS_WATER_5948, 2917, 4433, 0, Direction.SOUTH, 3), new NPCSpawn(5962, 2919, 4436, 0, Direction.SOUTH, 8),
            new NPCSpawn(DAGANNOTH_PRIME, 2913, 4456, 0, Direction.SOUTH, 3), new NPCSpawn(2267, 2920, 4445, 0, Direction.SOUTH, 3),
            new NPCSpawn(SUSPICIOUS_WATER_5962, 2922, 4461, 0, Direction.SOUTH, 4), new NPCSpawn(5946, 2927, 4457, 0, Direction.SOUTH, 2),
            new NPCSpawn(SPINOLYP, 2918, 4463, 0, Direction.SOUTH, 2), new NPCSpawn(5946, 2923, 4464, 0, Direction.SOUTH, 2),
            new NPCSpawn(SUSPICIOUS_WATER_5948, 2920, 4464, 0, Direction.SOUTH, 2), new NPCSpawn(5962, 2928, 4439, 0, Direction.SOUTH, 2),
            new NPCSpawn(SUSPICIOUS_WATER_5962, 2930, 4441, 0, Direction.SOUTH, 2), new NPCSpawn(5961, 2930, 4445, 0, Direction.SOUTH, 5),
            new NPCSpawn(SUSPICIOUS_WATER, 2928, 4453, 0, Direction.SOUTH, 2), new NPCSpawn(5963, 2930, 4450, 0, Direction.SOUTH, 3)
    };

    @Override
    public void constructed() {
        val poly = Spinolyp.polyBounds;
        val array = new int[poly.length][];
        for (int i = 0; i < poly.length; i++) {
            array[i] = new int[] { getX(poly[i][0]), getY(poly[i][1]) };
        }
        val polygon = new RSPolygon(array);
        for (val spawn : spawns) {
            val id = spawn.getId();
            if (id == NpcId.SPINOLYP || id == NpcId.SPINOLYP_5961 || id == NpcId.SPINOLYP_5963) {
                new InstanceSpinolyp(polygon, id, getLocation(spawn.getX(), spawn.getY(), spawn.getZ()), spawn.getDirection(), spawn.getRadius()).spawn();
                continue;
            }
            World.spawnNPC(spawn, spawn.getId(), getLocation(spawn.getX(), spawn.getY(), spawn.getZ()), spawn.getDirection(), spawn.getRadius());
        }
        DagannothKingsInstanceManager.getManager().addInstance(username, this);
    }

    @Override
    public void destroyRegion() {
        if (isDestroyed() || !players.isEmpty()) {
            return;
        }
        try {
            DagannothKingsInstanceManager.getManager().removeInstance(username, this);
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
        setDestroyed(true);
        GlobalAreaManager.remove(this);
        MapBuilder.destroy(area);
    }

    @Override
    public Location onLoginLocation() {
        return new Location(1912, 4367, 0);
    }

    @Override
    public void enter(Player player) {
        player.setViewDistance(Player.SCENE_DIAMETER);
    }

    @Override
    public void leave(Player player, boolean logout) {
        player.resetViewDistance();
    }

    @Override
    public String name() {
        return username + "'s clan Dagannoth Kings instance";
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
                    player.getDeathMechanics().service(ItemRetrievalService.RetrievalServiceType.HAGAVIK, source);
                    player.sendMessage("Oh dear, you have died.");
                    player.reset();
                    player.setAnimation(Animation.STOP);
                    player.sendMessage("Hagavik has retrieved some of your items. You can collect them from him in the Waterbirth island dungeon.");
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

    @Subscribe
    public static final void onClanLeave(final ClanLeaveEvent event) {
        val player = event.getPlayer();
        if (!(player.getArea() instanceof DagannothKingInstance)) {
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
                if (!(area instanceof DagannothKingInstance)) {
                    stop();
                    return;
                }
                player.lock(1);
                player.stopAll();
                player.sendMessage(Colour.RED.wrap("You have been removed from the instance."));
                player.blockIncomingHits();
                player.setLocation(new Location(((DagannothKingInstance) area).onLoginLocation()));
                stop();
            }
        }, Utils.random(5, 15), 0);
    }
}
