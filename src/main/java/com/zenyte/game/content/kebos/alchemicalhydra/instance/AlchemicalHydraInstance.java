package com.zenyte.game.content.kebos.alchemicalhydra.instance;

import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.kebos.alchemicalhydra.HydraPhase;
import com.zenyte.game.content.kebos.alchemicalhydra.model.AlchemicalVent;
import com.zenyte.game.content.kebos.alchemicalhydra.model.FireWall;
import com.zenyte.game.content.kebos.alchemicalhydra.model.FireWallBlock;
import com.zenyte.game.content.kebos.alchemicalhydra.model.VentObject;
import com.zenyte.game.content.kebos.alchemicalhydra.npc.AlchemicalHydra;
import com.zenyte.game.content.kebos.konar.map.KaruulmSlayerDungeon;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.area.plugins.*;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.zenyte.game.world.entity.player.Player.DEATH_ANIMATION;

/**
 * @author Tommeh | 02/11/2019 | 16:40
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class AlchemicalHydraInstance extends DynamicArea implements CycleProcessPlugin, LogoutPlugin, DeathPlugin, CannonRestrictionPlugin, LootBroadcastPlugin {

    public static final Location outsideLocation = new Location(1352, 10250, 0);
    public static final Location hydraCenterLocation = new Location(1364, 10265, 0);
    private static final Location greenVentLocation = new Location(1371, 10272, 0);
    private static final Location blueVentLocation = new Location(1362, 10272, 0);
    private static final Location redVentLocation = new Location(1371, 10263, 0);

    private static final Animation ventSprayAnim = new Animation(8279);
    private static final Animation ventResetAnim = new Animation(8280);
    private static final Animation climbAnim = new Animation(839);

    @Getter
    private transient Player player;
    @Getter private AlchemicalHydra hydra;

    @Setter @Getter
    private boolean entered;
    @Getter
    private VentObject redVent, greenVent, blueVent;
    @Getter
    private VentObject[] vents;
    @Getter
    private WorldObject[] doors, graphicalDoors;
    @Getter
    private Rectangle innerCenter, outerCenter;
    @Getter
    private List<Location> centerBorder;
    @Setter
    private int ventDelay = 10;
    @Getter
    @Setter
    private static boolean wasEmpowered;

    public AlchemicalHydraInstance(final Player player, final AllocatedArea area) {
        super(area, 165, 1278);
        this.player = player;
        this.redVent = new VentObject(AlchemicalVent.RED, this);
        this.greenVent = new VentObject(AlchemicalVent.GREEN, this);
        this.blueVent = new VentObject(AlchemicalVent.BLUE, this);
        this.vents = new VentObject[]{redVent, greenVent, blueVent};
        this.doors = new WorldObject[] {
                new WorldObject(34553, 0, 2, getLocation(new Location(1355, 10259, 0))),
                new WorldObject(34554, 0, 2, getLocation(new Location(1355, 10258, 0))),
        };
        this.graphicalDoors = new WorldObject[] {
                new WorldObject(34555, 0, 1, getLocation(new Location(1356, 10259, 0))),
                new WorldObject(34556, 0, 3, getLocation(new Location(1356, 10258, 0))),
        };
        this.innerCenter = World.getRectangle(getX(1364), getX(1369), getY(10265), getY(10270));
        this.outerCenter = World.getRectangle(getX(1363), getX(1370), getY(10264), getY(10271));
        this.centerBorder = new ArrayList<>(28);
        for (int x = (int) outerCenter.getMinX(); x <= (int) outerCenter.getMaxX(); x++) {
            centerBorder.add(new Location(x, (int) outerCenter.getMinY(), player.getPlane()));
            centerBorder.add(new Location(x, (int) outerCenter.getMaxY(), player.getPlane()));
        }
        for (int y = (int) outerCenter.getMinY() + 1; y < (int) outerCenter.getMaxY(); y++) {
            centerBorder.add(new Location((int) outerCenter.getMinX(), y, player.getPlane()));
            centerBorder.add(new Location((int) outerCenter.getMaxX(), y, player.getPlane()));
        }
    }

    public final List<FireWallBlock> buildWallTiles() {
        val tile = getStaticLocation(player.getLocation());
        val firewall = FireWall.select(tile);
        return firewall.getInnerPoints(this);
    }

    @Override
    public void constructed() {
        hydra = (AlchemicalHydra) new AlchemicalHydra(this, getLocation(hydraCenterLocation)).spawn();
        player.setLocation(getLocation(player.getLocation()));
        player.getAttributes().put("hydra_kc_on_instance_creation", player.getNotificationSettings().getKillcount("alchemical hydra"));
        val currentTile = getLocation(player.getLocation());
        val destination = currentTile.transform(0, 2, 0);
        val direction = Utils.getFaceDirection(destination.getX() - currentTile.getX(), destination.getY() - currentTile.getY());
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (ticks == 0) {
                    player.setAnimation(climbAnim);
                    player.autoForceMovement(destination, 0, 60, direction);
                } else if (ticks == 1) {
                    player.lock(2);
                    player.sendFilteredMessage("You climb over the rocks.");
                    stop();
                }
                ticks++;
            }
        }, 0, 0);
    }

    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player, boolean logout) {
        if (logout) {
            player.forceLocation(outsideLocation);
        }
        player.getAttributes().remove("hydra_kc_on_instance_creation");
    }

    @Override
    public void onLogout(final Player player) {
        player.setLocation(getLocation(outsideLocation));
    }

    @Override
    public Location onLoginLocation() {
        return outsideLocation;
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
    public String name() {
        return player.getName() + "'s Alchemical Hydra Instance";
    }

    @Override
    public void process() {
        for (val player : players) {
            if (player == null || KaruulmSlayerDungeon.isProtectedAgainstHeat(player)) {
                continue;
            }
            player.applyHit(new Hit(player, 4, HitType.REGULAR).setExecuteIfLocked());
        }
        if (entered) {
            if (ventDelay == 5) {
                for (val vent : vents) {
                    World.sendObjectAnimation(vent, ventSprayAnim);
                }
            } else if (ventDelay == 3) {
                if (!hydra.getPhase().equals(HydraPhase.ENRAGED)) {
                    for (val vent : vents) {
                        if (player.getLocation().matches(vent)) {
                            player.applyHit(new Hit(hydra, Utils.random(10, 20), HitType.REGULAR));
                            player.sendFilteredMessage("The chemical burns you as it cascades over you.");
                        }
                        if (!vent.isOnVent(hydra) || hydra.isDead() || hydra.isFinished()) {
                            continue;
                        }
                        if (vent.isCorrectVent(hydra)) {
                            if (hydra.isShielded()) {
                                hydra.setShielded(false);
                                hydra.setForceTalk(AlchemicalHydra.roar);
                                player.sendFilteredMessage("The chemicals neutralise the Alchemical Hydra's defence!");
                            }
                        } else {
                            hydra.increaseStrength();
                            player.sendFilteredMessage("The chemicals are absorbed by the Alchemical Hydra; empowering it further!");
                            setWasEmpowered(true);
                        }
                        break;
                    }
                }
            } else if (ventDelay == 0) {
                for (val vent : vents) {
                    World.sendObjectAnimation(vent, ventResetAnim);
                }
                ventDelay = 8;
            }
            ventDelay--;
        }
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
                    player.getDeathMechanics().service(ItemRetrievalService.RetrievalServiceType.ORRVOR_QUO_MATEN, source);
                    player.sendMessage("Oh dear, you have died.");
                    player.reset();
                    player.setAnimation(Animation.STOP);
                    player.sendMessage("Some of your items were sent to Orrvor Quo Maten. You can collect them from him in the Karuulm Slayer Dungeon.");
                    ItemRetrievalService.updateVarps(player);
                    if (player.getVariables().isSkulled()) {
                        player.getVariables().setSkull(false);
                    }
                    player.blockIncomingHits();
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
}
