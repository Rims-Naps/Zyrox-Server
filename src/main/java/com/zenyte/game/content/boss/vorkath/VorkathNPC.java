package com.zenyte.game.content.boss.vorkath;

import com.zenyte.game.content.achievementdiary.diaries.WesternProvincesDiary;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.GrandmasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.godwars.instance.SaradominInstance;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.Toxins.ToxinType;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessorLoader;
import com.zenyte.game.world.entity.npc.drop.matrix.NPCDrops;
import com.zenyte.game.world.entity.npc.impl.slayer.dragons.Dragonfire;
import com.zenyte.game.world.entity.npc.impl.slayer.dragons.DragonfireProtection;
import com.zenyte.game.world.entity.npc.impl.slayer.dragons.DragonfireType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kris | 28. jaan 2018 : 18:03.33
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@Setter
public final class VorkathNPC extends NPC implements CombatScript {

    private static final Projectile acidProjectile = new Projectile(1470, 30, 30, 27, 15, 40, 0, 5);
    private static final Graphics acidProjectileSplashGfx = new Graphics(1472, 0, 92);
    private static final Projectile afterPoisonProjectile = new Projectile(1482, 25, 25, 0, 15, 0, 0, 3);
    private static final Graphics afterPoisonProjectileGfx = new Graphics(131, 0, 92);
    private static final Animation basicAttackAnimation = new Animation(7952);
    private static final Projectile dragonfireProjectile = new Projectile(393, 30, 25, 27, 15, 40, 0, 5);
    private static final Graphics dragonfireSplashGfx = new Graphics(157, 0, 92);
    private static final Animation highAttackAnimation = new Animation(7960);
    private static final Projectile highDamageProjectile = new Projectile(1481, 40, 10, 35, 70, 80, 0, 5);
    private static final Graphics iceBarrageGfx = new Graphics(369);
    private static final Projectile iceDragonfireProjectile = new Projectile(395, 30, 25, 27, 15, 40, 0, 5);
    private static final Projectile magicProjectile = new Projectile(1479, 30, 25, 27, 15, 40, 0, 5);
    private static final Graphics magicProjectileSplashGfx = new Graphics(1480, 0, 92);
    private static final Animation meleeAnimation = new Animation(7951);
    private static final Projectile pinkDragonfireProjectile = new Projectile(1471, 30, 25, 27, 15, 40, 0, 5);
    private static final Graphics pinkDragonfireProjectileSplashGfx = new Graphics(1473, 0, 92);
    private static final Animation poisonPoolAnimation = new Animation(7957);
    private static final Projectile poisonPoolProjectile = new Projectile(1483, 25, 0, 35, 70, -1, 64, 5);
    private static final Projectile rangedProjectile = new Projectile(1477, 30, 25, 27, 15, 40, 0, 5);
    private static final Graphics rangedProjectileSplashGfx = new Graphics(1478, 0, 92);
    private static final Projectile zombifiedSpawnProjectile = new Projectile(1484, 60, 25, 35, 60, 85, 0, 0);
    private static final SoundEffect bigFireballSound = new SoundEffect(1519);
    private static final SoundEffect bigFireballHittingSound = new SoundEffect(163, 10);
    private static final SoundEffect vorkathIceSound = new SoundEffect(586);
    private static final SoundEffect vorkathIceSound2 = new SoundEffect(3750);
    private static final SoundEffect vorkathIceLandSound = new SoundEffect(168);
    private static final SoundEffect vorkathShootingSpawnSound = new SoundEffect(1784);
    private static final SoundEffect vorkathSpawnHittingGroundSound = new SoundEffect(3308, 10);
    private static final SoundEffect vorkathMagicAttackSound = new SoundEffect(408);
    private static final SoundEffect vorkathMagicAttackSoundp2 = new SoundEffect(222);
    private static final SoundEffect vorkathMagicLandSound = new SoundEffect(223);
    private static final SoundEffect meleeAttackSound = new SoundEffect(1521);
    private static final SoundEffect poisonPoolsSound = new SoundEffect(736);
    private static final SoundEffect poisonSplatHittingGroundSound = new SoundEffect(3308);
    private static final SoundEffect vorkathShootingFireSound = new SoundEffect(3749);
    private static final SoundEffect fireLandSound = new SoundEffect(158);
    private static final SoundEffect vorkathRangedAttackSound = new SoundEffect(408);
    private static final SoundEffect vorkathRangedAttackSoundp2 = new SoundEffect(1965);
    private static final SoundEffect vorkathRangedLandSound = new SoundEffect(3403);
    private static final SoundEffect vorkathPrayerSound = new SoundEffect(284);
    private static final SoundEffect vorkathPrayerSound2 = new SoundEffect(3750);
    private static final SoundEffect vorkathPrayerLandSound = new SoundEffect(135);
    private static final SoundEffect vorkathDragonfireSound = new SoundEffect(585);
    private static final SoundEffect vorkathDragonfireSound2 = new SoundEffect(3750);
    private static final SoundEffect vorkathDragonfireLandSound = new SoundEffect(156);
    private static final SoundEffect vorkathAcidSound = new SoundEffect(587);
    private static final SoundEffect vorkathAcidSound2 = new SoundEffect(3750);
    private static final SoundEffect vorkathAcidLandSound = new SoundEffect(129);
    private final List<AcidPool> acidPools = new ObjectArrayList<>();
    private final VorkathInstance instance;
    private int attacksPerformed, attackType = Utils.random(1);
    private float damageModifier = 1;
    private int delay;
    private int attacksDuringPoison;
    private boolean performingPoisonAttack;
    private boolean hitByRapidFire;
    private boolean hitByPoisonPool;
    private boolean wasWithFists;
    private static final Dragonfire.MessageBuilder builder = (protections, percentage, builder) -> {
        if (protections.isEmpty()) {
            builder.append("You are horribly burnt by the %s!");
        } else {
            builder.append("Your ");
            builder.append(protections.get(0).getProtectionName());
            builder.append(" helps to protect you from the %s!");
        }
    };

    VorkathNPC(final Location tile, final VorkathInstance instance) {
        super(NpcId.VORKATH_8059, tile, Direction.SOUTH, 0);
        this.instance = instance;
        setSpawned(true);
        setForceMultiArea(true);
        setDeathDelay(4);
    }

    @Override
    public float getXpModifier(Hit hit) {
        return damageModifier;
    }

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        return false;
    }

    @Override
    public void applyHit(final Hit hit) {
        super.applyHit(hit);
        if (damageModifier != 1 && hit.getHitType() != HitType.HEALED) {
            hit.setDamage((int) (hit.getDamage() * damageModifier));
        }
    }

    @Override
    public int attack(final Entity target) {
        if (delay > 0 || !(target instanceof Player)) {
            --delay;
            return 0;
        }
        val player = (Player) target;
        setAttacksPerformed(getAttacksPerformed() + 1);
        if (getAttacksPerformed() % 7 == 0) {
            setAttackType(getAttackType() + 1);
            if (getAttackType() % 2 == 0) {
                return sendIceDragonfireAttack(player);
            }
            return sendPoisonPools(player);
        }
        final boolean withinMeleeDistance = isWithinMeleeDistance(this, player);
        int style = Utils.random(withinMeleeDistance ? 6 : 5);
        if (style == 5 && player.isFrozen()) {
            style = Utils.random(4);
        }
        if (style == 0) {
            return sendMagicAttack(player);
        } else if (style == 1) {
            return sendRangedAttack(player);
        } else if (style == 2) {
            return sendRegularDragonfireAttack(player);
        } else if (style == 3) {
            return sendVenomousDragonfireAttack(player);
        } else if (style == 4) {
            return sendPrayerDragonfireAttack(player);
        } else if (style == 5) {
            return sendHighDamageDragonfireAttack(player);
        }
        return sendMeleeAttack(target);
    }

    @Override
    public boolean checkProjectileClip(final Player player) {
        return false;
    }

    private boolean containsAcidPool(final Location tile) {
        final int hash = tile.getPositionHash();
        for (final AcidPool pool : acidPools) {
            if (pool == null) {
                continue;
            }
            if (pool.getPositionHash() == hash) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

    @Override
    public void dropItem(final Player killer, final Item item, final Location tile, boolean guaranteedDrop) {
        tile.setLocation(new Location(getX() - 1, getY() - 1, getPlane()));
        int x = getX() + 3;
        int y = getY() + 3;
        if (killer.getX() < x) {
            x -= 4;
        } else if (killer.getX() > x) {
            x += 4;
        }
        if (killer.getY() < y) {
            y -= 4;
        } else if (killer.getY() > y) {
            y += 4;
        }
        tile.setLocation(x, y, tile.getPlane());
        super.dropItem(killer, item, tile, guaranteedDrop);
    }

    @Override
    protected void drop(final Location tile) {
        instance.getPlayer().getBossTimer().finishTracking("Vorkath");
        if (instance.getPlayer().getBossTimer().getBossTimer("Vorkath") < 75 && !instance.getPlayer().getBooleanAttribute("master-combat-achievement47")) {
            instance.getPlayer().putBooleanAttribute("master-combat-achievement47", true);
            MasterTasks.sendMasterCompletion(instance.getPlayer(), 47);
        }
        if (instance.getPlayer().getBossTimer().getBossTimer("Vorkath") < 60 && !instance.getPlayer().getBooleanAttribute("grandmaster-combat-achievement30")) {
            instance.getPlayer().putBooleanAttribute("grandmaster-combat-achievement30", true);
            GrandmasterTasks.sendGrandmasterCompletion(instance.getPlayer(), 30);
        }
        instance.setVorkath((VorkathNPC) new VorkathNPC(instance.getLocation(2269, 4062, 0), instance).spawn());
        attacksPerformed = 0;
        attackType = Utils.random(1);
        val killer = instance.getPlayer();
        tile.setLocation(new Location(getX() - 1, getY() - 1, getPlane()));
        onDrop(killer);

        val drops = NPCDrops.getTable(getId());
        if (drops == null) {
            return;
        }

        val processors = DropProcessorLoader.get(id);
        if (processors != null) {
            for (val processor : processors) {
                processor.onDeath(this, killer);
            }
        }
        for (int i = 0; i < 2; i++) {
            val first = i == 0;
            NPCDrops.forEach(drops, drop -> {
                if (!first && drop.isAlways())
                    return;
                dropItem(killer, drop, tile);
            });
        }
    }

    @Override
    protected int invisibleDropTicks() {
        return 2800;
    }

    @Override
    protected int visibleDropTicks() {
        return 200;
    }

    @Override
    public boolean lockUponInteraction() {
        return false;
    }

    private int sendHighDamageDragonfireAttack(final Player player) {
        setAnimation(highAttackAnimation);
        final Location tile = new Location(player.getLocation());
        val startPosition = getFaceLocation(player, 5);
        val delay = World.sendProjectile(startPosition, tile, highDamageProjectile);
        bigFireballSound.sendLocal(player);
        bigFireballHittingSound.sendLocal(player, highDamageProjectile, startPosition, tile);
        dragonfireSplashGfx.sendDelayed(highDamageProjectile, startPosition, tile);
        WorldTasksManager.schedule(() -> {
            if (player.getLocation().withinDistance(tile, 1)) {
                val damage = player.getLocation().matches(tile) ? Utils.random(110, 121) : Utils.random(50, 60);
                delayHit(this, -1, player, new Hit(this, damage, HitType.REGULAR));
                player.sendMessage("You are horribly burnt by the dragon's fiery bomb!");
            }
        }, delay);
        return delay + 3;
    }

    private int sendIceDragonfireAttack(final Entity target) {
        setAnimation(basicAttackAnimation);
        vorkathIceSound.sendLocal(target);
        vorkathIceSound2.sendLocal(target);
        val startTile = getFaceLocation(target, 6);
        vorkathIceLandSound.sendLocal(target, acidProjectile, startTile, target);
        WorldTasksManager.schedule(new WorldTask() {
            private int ticks = -1;
            private Location tile;

            @Override
            public void run() {
                if (ticks == -1) {
                    target.setGraphics(iceBarrageGfx);
                    VorkathNPC.this.setAnimation(highAttackAnimation);
                    int trycount = 100;
                    while (--trycount >= 0) {
                        if (trycount == 0) {
                            tile = new Location(VorkathNPC.this.getLocation());
                            break;
                        }
                        tile = new Location(VorkathNPC.this.getInstance().getLocation(Utils.random(2261, 2283), Utils.random(4054, 4076), 0));
                        if (World.getMask(tile.getPlane(), tile.getX(), tile.getY()) != 0) {
                            continue;
                        }
                        final double distance = tile.getDistance(target.getLocation());
                        if (distance >= 6 && distance <= 9) {
                            break;
                        }
                    }
                    World.sendProjectile(getFaceLocation(target, 6), tile, zombifiedSpawnProjectile);
                    vorkathShootingSpawnSound.sendLocal(target);
                    vorkathSpawnHittingGroundSound.sendGlobal(zombifiedSpawnProjectile, VorkathNPC.this, tile);
                    target.freezeWithNotification(50);
                    ticks = zombifiedSpawnProjectile.getTime(VorkathNPC.this, tile) + 1;
                    return;
                }
                if (--ticks <= 0) {
                    if (target.getEntityType() == EntityType.PLAYER) {
                        new ZombifiedSpawnNPC(tile, VorkathNPC.this, (Player) target).spawn();
                        VorkathNPC.this.setDamageModifier(0);
                        VorkathNPC.this.delay = Integer.MAX_VALUE;
                    }
                    stop();
                }
            }
        }, World.sendProjectile(startTile, target, iceDragonfireProjectile), 0);
        return 8;
    }

    private int sendMagicAttack(final Player player) {
        setAnimation(basicAttackAnimation);
        val startTile = getFaceLocation(player, 6);
        vorkathMagicAttackSound.sendLocal(player);
        vorkathMagicAttackSoundp2.sendLocal(player);
        vorkathMagicLandSound.sendLocal(player, magicProjectile, startTile, player);
        player.setGraphics(new Graphics(magicProjectileSplashGfx.getId(), magicProjectile.getProjectileDuration(startTile, player.getLocation()), magicProjectileSplashGfx.getHeight()));
        delayHit(this, World.sendProjectile(startTile, player, magicProjectile), player, new Hit(this, getRandomMaxHit(this, 30, MAGIC, player), HitType.MAGIC));
        return 5;
    }

    private int sendMeleeAttack(final Entity target) {
        setAnimation(meleeAnimation);
        meleeAttackSound.sendLocal(target);
        delayHit(this, 0, target, new Hit(this, getRandomMaxHit(this, 32, MELEE, target), HitType.MELEE));
        return 5;
    }

    private int sendPoisonPools(final Player player) {
        setAnimation(poisonPoolAnimation);
        poisonPoolsSound.sendLocal(player);
        final Location center = getInstance().getLocation(2272, 4065, 0);
        final List<AcidPool> pools = new ArrayList<>(75);
        Location tile = new Location(player.getLocation());
        pools.add(new AcidPool(tile));
        World.sendProjectile(this, tile, poisonPoolProjectile, poisonPoolProjectile.getProjectileDurationByDistance(1));
        int count = 500;
        while (pools.size() < 75 && --count > 0) {
            final int x = center.getX() + (Utils.random(11) * (Utils.random(1) == 0 ? (-1) : 1));
            final int y = center.getY() + (Utils.random(11) * (Utils.random(1) == 0 ? (-1) : 1));
            tile = new Location(x, y, center.getPlane());
            if (World.getMask(tile.getPlane(), tile.getX(), tile.getY()) != 0 || containsAcidPool(tile)) {
                continue;
            }
            pools.add(new AcidPool(tile));
            World.sendProjectile(this, tile, poisonPoolProjectile, poisonPoolProjectile.getProjectileDurationByDistance(3));
        }
        WorldTasksManager.schedule(new WorldTask() {
            private int ticks = -2;
            private Location tile;

            @Override
            public void run() {
                if (isDead() || isFinished() || !player.getLocation().withinDistance(getLocation(), 30) || player.isDead() || player.isFinished()) {
                    pools.forEach(World::removeObject);
                    getAcidPools().clear();
                    setDamageModifier(1);
                    setAnimation(Animation.STOP);
                    stop();
                    performingPoisonAttack = true;
                    hitByRapidFire = false;
                    hitByPoisonPool = false;
                    return;
                }
                if (ticks == -1) {
                    poisonSplatHittingGroundSound.sendLocal(player);
                    pools.forEach(World::spawnObject);
                    getAcidPools().addAll(pools);
                    setDamageModifier(0.5f);
                    attacksDuringPoison = 0;
                    performingPoisonAttack = true;
                    hitByRapidFire = false;
                    hitByPoisonPool = false;
                } else if (ticks >= 0) {
                    if (ticks == 26) {
                        pools.forEach(World::removeObject);
                        getAcidPools().clear();
                        setDamageModifier(1);
                        stop();
                        performingPoisonAttack = false;
                        return;
                    }
                    if (tile != null) {
                        World.sendGraphics(afterPoisonProjectileGfx, new Location(tile));
                        fireLandSound.sendLocal(player);
                        if (player.getLocation().getPositionHash() == tile.getPositionHash()) {
                            hitByRapidFire = true;
                            val damage = Utils.random(30, 40);
                            val perk = player.getPerkManager().isValid(PerkWrapper.BACKFIRE);
                            val modifier = Math.max(0, Utils.randomDouble() - 0.25F);
                            val deflected = !perk ? 0 : ((int) Math.floor(damage * modifier));
                            delayHit(VorkathNPC.this, -1, player, new Hit(VorkathNPC.this, damage - deflected, HitType.REGULAR));
                            if (perk) {
                                WorldTasksManager.scheduleOrExecute(() -> VorkathNPC.this.applyHit(new Hit(player, deflected, HitType.REGULAR)), 0);
                            }
                        }
                    }
                    if (ticks < 25) {
                        vorkathShootingFireSound.sendLocal(player);
                        World.sendProjectile(VorkathNPC.this, tile = new Location(player.getLocation()), afterPoisonProjectile);
                    }
                }
                ticks++;
            }
        }, 1, 0);
        return 34;
    }

    private int sendRangedAttack(final Player player) {
        setAnimation(basicAttackAnimation);
        val startTile = getFaceLocation(player, 6);
        vorkathRangedAttackSound.sendLocal(player);
        vorkathRangedAttackSoundp2.sendLocal(player);
        vorkathRangedLandSound.sendLocal(player, rangedProjectile, startTile, player);
        player.setGraphics(new Graphics(rangedProjectileSplashGfx.getId(), rangedProjectileSplashGfx.getHeight(), rangedProjectile.getProjectileDuration(startTile, player.getLocation())));
        delayHit(this, World.sendProjectile(startTile, player, rangedProjectile), player, new Hit(this, getRandomMaxHit(this, 32, RANGED, player), HitType.RANGED));
        return 5;
    }

    private int sendPrayerDragonfireAttack(final Player player) {
        setAnimation(basicAttackAnimation);
        val startTile = getFaceLocation(player, 6);
        player.setGraphics(new Graphics(pinkDragonfireProjectileSplashGfx.getId(), pinkDragonfireProjectileSplashGfx.getHeight(), pinkDragonfireProjectile.getProjectileDuration(startTile, player.getLocation())));
        val dragonfire = getBuilder(player);
        vorkathPrayerSound.sendLocal(player);
        vorkathPrayerSound2.sendLocal(player);
        vorkathPrayerLandSound.sendLocal(player, acidProjectile, startTile, player);
        delayHit(this, World.sendProjectile(startTile, player, pinkDragonfireProjectile), player,
                new Hit(this, Utils.random(Math.max(0, dragonfire.getDamage())), HitType.REGULAR).onLand(hit -> {
                    PlayerCombat.appendDragonfireShieldCharges(player);
                    player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's corrupting breath"));
                    player.sendMessage("<col=ff0000>Your prayers have been disabled!");
                    player.getPrayerManager().deactivateActivePrayers();
                }));
        return 5;
    }

    private int sendRegularDragonfireAttack(final Player player) {
        setAnimation(basicAttackAnimation);
        val startTile = getFaceLocation(player, 6);
        val dragonfire = new Dragonfire.DragonfireBuilder(DragonfireType.STRONG_DRAGONFIRE, 70, DragonfireProtection.getProtection(player), builder) {
            @Override
            public int getDamage() {
                val tier = getAccumulativeTier();
                return tier == 0F ? 70 : tier == 0.25F ? 60 : tier == 0.5F ? 40 : tier == 0.75F ? 20 : tier == 1F ? 15 : tier == 1.25F ? 10 : tier == 1.5F ? 5 : 0;
            }
        };
        vorkathDragonfireSound.sendLocal(player);
        vorkathDragonfireSound2.sendLocal(player);
        vorkathDragonfireLandSound.sendLocal(player, dragonfireProjectile, startTile, player);
        player.setGraphics(new Graphics(dragonfireSplashGfx.getId(), dragonfireSplashGfx.getHeight(), dragonfireProjectile.getProjectileDuration(startTile, player.getLocation())));
        delayHit(this, World.sendProjectile(startTile, player, dragonfireProjectile), player,
                new Hit(this, Utils.random(Math.max(0, dragonfire.getDamage())), HitType.REGULAR).onLand(hit -> {
                    PlayerCombat.appendDragonfireShieldCharges(player);
                    player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's fiery breath"));
                }));
        return 5;
    }

    private int sendVenomousDragonfireAttack(final Player player) {
        setAnimation(basicAttackAnimation);
        val dragonfire = getBuilder(player);
        val startTile = getFaceLocation(player, 6);
        vorkathAcidSound.sendLocal(player);
        vorkathAcidSound2.sendLocal(player);
        vorkathAcidLandSound.sendLocal(player, acidProjectile, startTile, player);
        player.setGraphics(new Graphics(acidProjectileSplashGfx.getId(), acidProjectileSplashGfx.getHeight(), acidProjectile.getProjectileDuration(startTile, player.getLocation())));
        delayHit(this, World.sendProjectile(startTile, player, acidProjectile), player,
                new Hit(this, Utils.random(Math.max(0, dragonfire.getDamage())), HitType.REGULAR).onLand(hit -> {
                    PlayerCombat.appendDragonfireShieldCharges(player);
                    player.sendFilteredMessage(String.format(dragonfire.getMessage(), "dragon's venomous breath"));
                    if (Utils.random(1) == 0) player.getToxins().applyToxin(ToxinType.VENOM, 6, false);
                }));
        return 5;
    }

    private Dragonfire.DragonfireBuilder getBuilder(@NotNull final Player player) {
        return new Dragonfire.DragonfireBuilder(DragonfireType.STRONG_DRAGONFIRE, 72, DragonfireProtection.getProtection(player), builder) {
            @Override
            public int getDamage() {
                val tier = getAccumulativeTier();
                return tier == 0F ? 72 : tier == 0.25F ? 55 : tier == 0.5F ? 35 : tier == 0.75F ? 15 : tier == 1F ? 10 : 0;
            }
        };
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        if (hit != null && hit.getSource() instanceof Player) {
            if (hitpoints == getMaxHitpoints()) {
                wasWithFists = true;
            }
            val player = (Player) hit.getSource();
            if (!player.getBooleanAttribute("elite-combat-achievement63")) {
                if (hit.getHitType() != HitType.MELEE) {
                    player.getTemporaryAttributes().put("melee only", false);
                }
            }
            if (!player.getBooleanAttribute("master-combat-achievement23")) {
                if (performingPoisonAttack) {
                    attacksDuringPoison++;
                    if (attacksDuringPoison >= 12 && !hitByRapidFire && !hitByPoisonPool) {
                        if (!player.getBooleanAttribute("master-combat-achievement23")) {
                            player.putBooleanAttribute("master-combat-achievement23", true);
                            MasterTasks.sendMasterCompletion(player, 23);
                        }
                    }
                }
            }
            val weapon = player.getWeapon();
            if (weapon != null) {
                wasWithFists = false;
            }
        }
        super.handleIngoingHit(hit);
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source instanceof Player) {
            val player = (Player) source;
            int noSpecialDamageKills = 0;
            if (!player.getTemporaryAttributes().containsKey("melee only") && !player.getBooleanAttribute("elite-combat-achievement63")) {
                player.putBooleanAttribute("elite-combat-achievement63", true);
                EliteTasks.sendEliteCompletion(player, 63);
            }
            if (!hitByPoisonPool
                    && !hitByRapidFire
                    && !player.getBooleanAttribute("hitByZombifiedSpawn")
                    && !player.getBooleanAttribute("master-combat-achievement35")) {
                noSpecialDamageKills = player.getNumericAttribute("noSpecialDamageVorkathKills").intValue() + 1;
                player.getAttributes().put("noSpecialDamageVorkathKills", noSpecialDamageKills);
            } else  {
                player.getAttributes().put("noSpecialDamageVorkathKills", 0);
            }
            if (noSpecialDamageKills >= 5 && !player.getBooleanAttribute("master-combat-achievement35")) {
                player.putBooleanAttribute("master-combat-achievement35", true);
                MasterTasks.sendMasterCompletion(player, 35);
            }
            if (player.getNumericAttribute("vorkath_kc_on_instance_creation").intValue() + 9 <= player.getNotificationSettings().getKillcount("vorkath")
                    && !player.getBooleanAttribute("master-combat-achievement52")
                    && player.getArea() instanceof VorkathInstance) {
                player.putBooleanAttribute("master-combat-achievement52", true);
                MasterTasks.sendMasterCompletion(player, 52);
            }
            if (wasWithFists && !player.getBooleanAttribute("grandmaster-combat-achievement20")) {
                player.putBooleanAttribute("grandmaster-combat-achievement20", true);
                GrandmasterTasks.sendGrandmasterCompletion(player, 20);
            }
            if (!player.getBooleanAttribute("drainedPrayerDuringVorkath") && !player.getBooleanAttribute("grandmaster-combat-achievement21")) {
                player.putBooleanAttribute("grandmaster-combat-achievement21", true);
                GrandmasterTasks.sendGrandmasterCompletion(player, 21);
            }
        }
    }
}
