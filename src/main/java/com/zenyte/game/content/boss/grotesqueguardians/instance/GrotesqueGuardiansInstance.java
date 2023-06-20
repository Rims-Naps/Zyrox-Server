package com.zenyte.game.content.boss.grotesqueguardians.instance;

import com.zenyte.game.content.ItemRetrievalService;
import com.zenyte.game.content.boss.grotesqueguardians.EnergySphere;
import com.zenyte.game.content.boss.grotesqueguardians.FightPhase;
import com.zenyte.game.content.boss.grotesqueguardians.boss.Dawn;
import com.zenyte.game.content.boss.grotesqueguardians.boss.Dusk;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.*;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.var;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static com.zenyte.game.world.entity.player.Player.DEATH_ANIMATION;

/**
 * @author Tommeh | 21/07/2019 | 21:17
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class GrotesqueGuardiansInstance extends DynamicArea implements LogoutPlugin, CycleProcessPlugin, DeathPlugin, PartialMovementPlugin, FullMovementPlugin, CannonRestrictionPlugin, LootBroadcastPlugin {

    public static final Location OUTSIDE_LOCATION = new Location(3428, 3541, 2);
    public static final Animation BELL_ANIMATION = new Animation(7748);
    private static final Location INSIDE_LOCATION = new Location(1697, 4567, 0);
    private static final Location BELL_LOCATION = new Location(1695, 4583, 0);
    private static final Location DUSK_SPAWN_LOCATION = new Location(1689, 4572, 0);
    private static final Location DAWN_SPAWN_LOCATION = new Location(1701, 4573, 0);
    private static final Location DUSK_STATUE_LOCATION = new Location(1685, 4573, 0);
    private static final Location DAWN_STATUE_LOCATION = new Location(1705, 4573, 0);
    private static final Location BOSS_ROOM_CENTER_LOCATION = new Location(1697, 4575, 0);
    private static final Animation DUSK_ENTRY_ANIMATION = new Animation(7778);
    private static final Animation DAWN_ENTRY_ANIMATION = new Animation(7766);

    private static final Animation DUSK_JUMP_ANIMATION = new Animation(7789);
    private static final Animation DAWN_JUMP_ANIMATION = new Animation(7767);

    private static final Animation DUSK_AOE_ANIMATION = new Animation(7792);
    private static final Animation DAWN_AOE_ANIMATION = new Animation(7772);

    private static final Animation DUSK_TRANSFORM_ANIMATION = new Animation(7790);
    private static final Animation DUSK_REVERT_ANIMATION = new Animation(7793);

    private static final Animation DAWN_FLY_AWAY_ANIMATION = new Animation(7773);
    private static final Animation DAWN_FLY_BACK_ANIMATION = new Animation(7774);

    private static final Projectile DEBRIS_PROJECTILE = new Projectile(1435, 255, 0, 0, 0, 100, 127, 7);
    private static final Graphics DEBRIS_SHADOW_GFX = new Graphics(1446);
    private static final Graphics DEBRIS_IMPACT_GFX = new Graphics(1436);
    private static final Graphics DEBRIS_STUN_GFX = new Graphics(80);


    @Getter
    private transient Player player;
    @Getter
    private WorldObject bell;
    @Getter
    private Dawn dawn;
    @Getter
    private Dusk dusk;
    @Getter
    @Setter
    private boolean started;
    @Getter
    @Setter
    private FightPhase phase = FightPhase.PHASE_ONE;
    private RSPolygon bossRoom;
    @Getter
    private RSPolygon walkingBoundary, prisonBoundary;
    private Map<Location, Integer> lightnings;
    private long debrisDelay;
    @Getter
    private boolean performingLightningAttack;
    @Getter
    @Setter
    private boolean debrisAllowed;
    @Getter
    private Int2ObjectOpenHashMap<EnergySphere> energySpheres;
    @Getter
    private Location roomCenter;

    public GrotesqueGuardiansInstance(final Player player, final AllocatedArea area) {
        super(area, 208, 568);
        this.player = player;
        bell = new WorldObject(31684, 10, 3, getLocation(BELL_LOCATION));
        roomCenter = getLocation(BOSS_ROOM_CENTER_LOCATION);
        bossRoom = new RSPolygon(new int[][] {
                { getX(1689), getY(4583) },
                { getX(1689), getY(4567) },
                { getX(1705), getY(4567) },
                { getX(1705), getY(4583) }
        });
        walkingBoundary = new RSPolygon(new int[][] {
                { getX(1690), getY(4582) },
                { getX(1690), getY(4568) },
                { getX(1704), getY(4568) },
                { getX(1704), getY(4582) }
        });
        prisonBoundary = new RSPolygon(new int[][] {
                { getX(1691), getY(4581) },
                { getX(1691), getY(4569) },
                { getX(1703), getY(4569) },
                { getX(1703), getY(4581) }
        });
        lightnings = new Object2ObjectOpenHashMap<>();
        energySpheres = new Int2ObjectOpenHashMap<>(3);
    }

    @Override
    public void constructed() {
        player.setGrotesqueGuardiansInstance(Optional.of(this));
        player.getInterfaceHandler().closeInterfaces();
        player.setLocation(getLocation(INSIDE_LOCATION));
        player.getAttributes().put("grotesque_guardians_kc_on_instance_creation", player.getNotificationSettings().getKillcount("grotesque guardians"));
        /*for (int x = 0; x < 50000; x++) {
            for (int y = 0; y < 50000; y++) {
                if (prisonBoundary.contains(x, y)) {
                    World.spawnObject(new WorldObject(1, 10, 3, new Location(x, y, 0)));
                }
            }
        }*/
    }

    @Override
    public void enter(Player player) {
        player.getVarManager().sendBit(3318, 0); //dusk statue
        player.getVarManager().sendBit(4600, 0); //dawn statue
    }

    public void start(final boolean cutscene) {
        player.getInterfaceHandler().sendInterface(InterfacePosition.OVERLAY, 257);
        player.getVarManager().sendBit(3318, 1); //dusk statue
        player.getVarManager().sendBit(4600, 1); //dawn statue
        player.putBooleanAttribute("was_hit_by_lightning_grot_guar", false);
        player.putBooleanAttribute("dawn_healed", false);
        player.putBooleanAttribute("was_hit_by_rockfall_grot_guar", false);
        player.putBooleanAttribute("was_hit_by_prison_grot_guar", false);
        player.putBooleanAttribute("was_hit_by_blinding_grot_guar", false);
        if (!player.getAttributes().containsKey("perfectguardians")) {
            player.getAttributes().put("perfectguardians", 0);
        }
        if (cutscene) {
            dawn = new Dawn(getLocation(DAWN_STATUE_LOCATION), this);
            dawn.setTransformation(Dawn.NON_ATTACKABLE_NPC_ID);
            dawn.lock();
            dawn.setAnimation(DAWN_ENTRY_ANIMATION);

            dusk = new Dusk(getLocation(DUSK_STATUE_LOCATION), this);
            dusk.setTransformation(Dusk.NON_ATTACKABLE_NPC_ID);
            dusk.lock();
            dusk.setAnimation(DUSK_ENTRY_ANIMATION);

            WorldTasksManager.schedule(new WorldTask() {
                int ticks;

                @Override
                public void run() {
                    if (ticks == 8) {
                        dawn.addWalkSteps(getX(DAWN_STATUE_LOCATION.getX() - 5), getY(DAWN_STATUE_LOCATION.getY()), 6, false);
                        dawn.setAnimation(DAWN_JUMP_ANIMATION);

                        dusk.addWalkSteps(getX(DUSK_STATUE_LOCATION.getX() + 5), getY(DUSK_STATUE_LOCATION.getY()), 6, false);
                        dusk.setAnimation(DUSK_JUMP_ANIMATION);
                    } else if (ticks == 9) {
                        dawn.unlock();
                        dusk.unlock();
                    } else if (ticks == 13) {
                        dawn.lock();
                        dusk.lock();
                    } else if (ticks == 4) {
                        player.getInterfaceHandler().closeInterfaces();
                    } else if (ticks == 16) {
                        dawn.unlock();
                        dawn.setTransformation(Dawn.ATTACKABLE_NPC_ID);
                        dawn.getCombat().setTarget(player);
                        dusk.unlock();
                        dusk.setTransformation(Dusk.ATTACKABLE_NPC_ID);
                        dusk.getCombat().setTarget(player);
                        player.getBossTimer().startTracking("Grotesque Guardians");
                        stop();
                        return;
                    }
                    ticks++;
                }
            }, 0, 0);

        } else {
            dawn = new Dawn(getLocation(DAWN_SPAWN_LOCATION), this);
            dusk = new Dusk(getLocation(DUSK_SPAWN_LOCATION), this);
        }
        dawn.setDusk(dusk);
        dusk.setDawn(dawn);
        dawn.spawn();
        dusk.spawn();
        if (!cutscene) {
            dawn.getCombat().setTarget(player);
            dusk.getCombat().setTarget(player);
            player.getBossTimer().startTracking("Grotesque Guardians");
        }
    }

    @Override
    public void leave(Player player, boolean logout) {
        if (logout) {
            player.forceLocation(OUTSIDE_LOCATION);
        }
        player.getAttributes().remove("grotesque_guardians_kc_on_instance_creation");
    }

    @Override
    public void onLogout(final Player player) {
        player.setLocation(getLocation(OUTSIDE_LOCATION));
    }

    @Override
    public Location onLoginLocation() {
        return OUTSIDE_LOCATION;
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
                    player.getDeathMechanics().service(ItemRetrievalService.RetrievalServiceType.MAGICAL_CHEST, source);
                    player.sendMessage("Oh dear, you have died.");
                    player.reset();
                    player.setAnimation(Animation.STOP);
                    player.sendMessage("Some of your items were sent to the magical chest. You can collect them from it in the Slayer Tower.");
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
    public void process() {
        if (!lightnings.isEmpty()) {
            for (val lightning : lightnings.keySet()) {
                if (player.getLocation().withinDistance(lightning, 1) && lightnings.get(lightning) == 2) {
                    player.applyHit(new Hit(dusk, Utils.random(5, 10), HitType.REGULAR));
                    player.putBooleanAttribute("was_hit_by_lightning_grot_guar", true);
                }
                if (lightnings.get(lightning) < 2) {
                    lightnings.replace(lightning, lightnings.get(lightning), lightnings.get(lightning) + 1);
                }
            }
        }
        switch (phase) {
            case PHASE_ONE:
                if (dawn != null && dawn.getHitpoints() <= 225) {
                    lightningAttack();
                }
                break;
            case PHASE_TWO:
                if (!dawn.isDying() && !dawn.isDead() && debrisAllowed && debrisDelay <= Utils.currentTimeMillis()) {
                    debrisAttack();
                }
                if (dusk != null && dusk.getHitpoints() <= 225) {
                    lightningAttack();
                }
                break;
            case PHASE_THREE:
                if (!energySpheres.isEmpty()) {
                    for (val entry : energySpheres.int2ObjectEntrySet()) {
                        val sphere = entry.getValue();
                        sphere.process();
                    }
                }
                break;
            case PHASE_FOUR:
                if (dusk.isDying() || dusk.isDead()) {
                    return;
                }
                if (!dusk.isPerformingPrisonAttack() && dusk.getPrisonAttackDelay() <= Utils.currentTimeMillis()) {
                    dusk.prisonAttack(false);
                }
                break;
        }
    }

    private void lightningAttack() {
        if (performingLightningAttack) {
            return;
        }

        dawn.setCantInteract(true);
        dawn.getCombat().setTarget(null);
        dawn.setInteractingWith(null);
        dawn.resetWalkSteps();
        dawn.setTransformation(Dawn.NON_ATTACKABLE_NPC_ID);

        dusk.setCantInteract(true);
        dusk.getCombat().setTarget(null);
        dusk.setInteractingWith(null);
        dusk.resetWalkSteps();
        dusk.setTransformation(Dusk.NON_ATTACKABLE_NPC_ID);
        switch (phase) {
            case PHASE_ONE:
                dawn.addWalkSteps(getX(1699), getY(4573), 30, false);
                dusk.addWalkSteps(getX(1691), getY(4573), 30, false);
                lightnings.clear();
                performingLightningAttack = true;
                WorldTasksManager.schedule(new WorldTask() {
                    int ticks;

                    @Override
                    public void run() {
                        if (!dawn.hasWalkSteps() && !dusk.hasWalkSteps()) {
                            switch (ticks++) {
                                case 0:
                                    dawn.lock();
                                    dusk.lock();
                                    break;
                                case 2:
                                    dawn.faceEntity(dusk);
                                    dusk.faceEntity(dawn);
                                    break;
                                case 3:
                                    dawn.setTransformation(Dawn.NON_ATTACKABLE_NPC_ID);

                                    dusk.setAnimation(DUSK_TRANSFORM_ANIMATION);
                                    dusk.setTransformation(Dusk.LIGHTNING_ATTACK_NPC_ID);
                                    break;
                                case 4:
                                    dawn.setAnimation(DAWN_AOE_ANIMATION);
                                    dusk.setAnimation(DUSK_AOE_ANIMATION);
                                    val firstLightnings = getLightnings(4);
                                    for (val location : firstLightnings) {
                                        lightnings.put(location, 0);
                                        World.sendGraphics(Utils.random(1) == 0 ? new Graphics(1424) : new Graphics(1416), location);
                                    }
                                    break;
                                case 6:
                                case 8:
                                case 10:
                                    val newLightnings = getLightnings(4);
                                    for (val location : newLightnings) {
                                        lightnings.put(location, 0);
                                        World.sendGraphics(Utils.random(1) == 0 ? new Graphics(1424 + (ticks - 5)) : new Graphics(1416 + (ticks - 5)), location);
                                    }
                                    break;
                                case 13:
                                    dawn.setTransformation(Dawn.ATTACKABLE_NPC_ID);
                                    dawn.setAnimation(DAWN_FLY_AWAY_ANIMATION);
                                    dusk.setAnimation(DUSK_REVERT_ANIMATION);
                                    lightnings.clear();
                                    break;
                                case 15:
                                    dawn.finish();
                                    dusk.setTransformation(Dusk.ATTACKABLE_NPC_ID);
                                    dusk.setCantInteract(false);
                                    dusk.unlock();
                                    dusk.getCombat().setTarget(player);
                                    dusk.setExplosiveAttack(true);
                                    performingLightningAttack = false;
                                    phase = FightPhase.PHASE_TWO;
                                    stop();
                                    break;
                            }
                        }
                    }

                }, 0, 0);
                break;
            case PHASE_TWO:
                lightnings.clear();
                dawn.unlock();

                dawn.setLocation(new Location(getX(1699), getY(4573)));
                World.addNPC(dawn);
                dawn.setFinished(false);
                dawn.updateLocation();

                //dawn.setHitpoints(225); //225
                dawn.setAnimation(DAWN_FLY_BACK_ANIMATION);
                dawn.addWalkSteps(getX(1699), getY(4573), 30, false);
                dusk.addWalkSteps(getX(1691), getY(4573), 30, false);
                performingLightningAttack = true;
                WorldTasksManager.schedule(new WorldTask() {
                    int ticks;

                    @Override
                    public void run() {
                        if (!dawn.hasWalkSteps() && !dusk.hasWalkSteps()) {
                            switch (ticks++) {
                                case 0:
                                    dawn.lock();
                                    dusk.lock();
                                    break;
                                case 2:
                                    dawn.faceEntity(dusk);
                                    dusk.faceEntity(dawn);
                                    break;
                                case 3:
                                    dusk.setAnimation(DUSK_TRANSFORM_ANIMATION);
                                    dusk.setTransformation(Dusk.LIGHTNING_ATTACK_NPC_ID);
                                    break;
                                case 4:
                                    dawn.setAnimation(DAWN_AOE_ANIMATION);
                                    dusk.setAnimation(DUSK_AOE_ANIMATION);
                                    val firstLightnings = getLightnings(4);
                                    for (val location : firstLightnings) {
                                        lightnings.put(location, 0);
                                        World.sendGraphics(Utils.random(1) == 0 ? new Graphics(1424) : new Graphics(1416), location);
                                    }
                                    break;
                                case 6:
                                case 8:
                                case 10:
                                    val newLightnings = getLightnings(4);
                                    for (val location : newLightnings) {
                                        lightnings.put(location, 0);
                                        World.sendGraphics(Utils.random(1) == 0 ? new Graphics(1424 + (ticks - 5)) : new Graphics(1416 + (ticks - 5)), location);
                                    }
                                    break;
                                case 13:
                                    lightnings.clear();
                                    dusk.setAnimation(DUSK_REVERT_ANIMATION);
                                    break;
                                case 15:
                                    dawn.setTransformation(Dawn.ATTACKABLE_NPC_ID);
                                    dawn.setCantInteract(false);
                                    dawn.unlock();
                                    dawn.getCombat().setTarget(player);
                                    dawn.setSphereAttack(true);

                                    dusk.setTransformation(Dusk.ATTACKABLE_NPC_ID);
                                    dusk.setCantInteract(false);
                                    dusk.unlock();
                                    dusk.getCombat().setTarget(player);

                                    performingLightningAttack = false;
                                    phase = FightPhase.PHASE_THREE;

                                    stop();
                                    break;
                            }
                        }
                    }

                }, 0, 0);
                break;
        }
    }

    private void debrisAttack() {
        if (performingLightningAttack) {
            return;
        }
        debrisDelay = Utils.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;

            @Override
            public void run() {
                switch (ticks++) {
                    case 0:
                    case 1:
                    case 2:
                        for (int i = 0; i < Utils.random(2, 3); i++) {
                            val destination = getRandomPoint();
                            World.sendProjectile(destination.transform(-1, 0, 0), destination, DEBRIS_PROJECTILE);
                            World.sendGraphics(DEBRIS_SHADOW_GFX, destination);
                            WorldTasksManager.schedule(() -> {
                                if (player.getLocation().withinDistance(destination, 1)) {
                                    player.applyHit(new Hit(dawn, 15 + Utils.random(7), HitType.REGULAR));
                                    player.putBooleanAttribute("was_hit_by_rockfall_grot_guar", true);
                                    player.setGraphics(DEBRIS_STUN_GFX);
                                    player.stun(6);
                                    player.sendMessage("You have been knocked out!");
                                }
                                World.sendGraphics(DEBRIS_IMPACT_GFX, destination);
                            }, 2);
                        }
                        break;
                }
            }
        }, 0, 0);
    }

    private ArrayList<Location> getLightnings(final int amount) {
        val set = new ArrayList<Location>(18);
        var count = 1000;
        while (--count > 0 && set.size() != amount) {
            val location = getRandomPoint();

            if (set.size() < amount && !set.contains(location) && !lightnings.containsKey(location)) {
                set.add(location);
            }
        }
        return set;
    }

    public Location getRandomPoint() {
        val poly = bossRoom.getPolygon();
        val box = poly.getBounds2D();
        var count = 1000;
        Location location = new Location(0);
        do {
            if (--count <= 0) {
                throw new RuntimeException("Unable to find a valid point in polygon.");
            }
            location.setLocation((int) box.getMinX() + Utils.random((int) box.getWidth()), (int) box.getMinY() + Utils.random((int) box.getHeight()), player.getPlane());
        } while (!poly.contains(location.getX(), location.getY()));
        return location;
    }

    public EnergySphere getEnergySphere(final int id) {
        return energySpheres.get(id);
    }

    public void reset() {
        dawn = null;
        dusk = null;
        lightnings.clear();
        energySpheres.clear();
        phase = FightPhase.PHASE_ONE;
        started = false;
        performingLightningAttack = false;
        debrisAllowed = false;
        player.getVarManager().sendBit(3318, 0); //dusk statue
        player.getVarManager().sendBit(4600, 0); //dawn statue
    }

    @Override
    public void cleared() {
        if (players.isEmpty()) {
            destroyRegion();
            player.setGrotesqueGuardiansInstance(Optional.empty());
        }
    }

    @Override
    public String name() {
        return player.getName() + "'s Grotesque Guardians Instance";
    }

    @Override
    public boolean processMovement(Player player, int x, int y) {
        if (dusk != null && dusk.getMiddleLocation().getX() == x && dusk.getMiddleLocation().getY() == y) {
            player.applyHit(new Hit(dusk, Utils.random(30, 40), HitType.REGULAR));
            player.sendMessage("You get trampled under Dusk's massive body.");
        }
        return true;
    }
}
