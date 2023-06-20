package com.zenyte.game.content.boss.skotizo.npc;

import com.amazonaws.services.dynamodbv2.xspec.M;
import com.zenyte.game.content.boss.skotizo.instance.SkotizoInstance;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.ForceTalk;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tommeh | 05/03/2020 | 21:09
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class Skotizo extends NPC implements CombatScript {

    private static final Animation meleeAnimation = new Animation(64);
    private static final Animation magicAnimation = new Animation(69);

    private static final Projectile magicProjectile = new Projectile(1242, 90, 20, 30, 8, 30, 0, 4);

    private static final ForceTalk minionSpawningForceTalk = new ForceTalk("Gar mulno ful taglo!");

    private transient SkotizoInstance instance;
    private static final Graphics graphics = new Graphics(86, 0, 90);

    @Getter
    private List<NPC> minions;

    @Setter
    private long demonSpawnDelay, ankouSpawnDelay;
    @Getter @Setter private boolean disableAltarRespawning;

    public Skotizo(final SkotizoInstance instance) {
        super(NpcId.SKOTIZO, instance.getBossLocation(), Direction.SOUTH, 128);
        this.instance = instance;
        minions = new ArrayList<>(3);
        this.attackDistance = 10;
        this.maxDistance = this.aggressionDistance = 64 << 1;
        this.forceAggressive = true;
        this.randomWalkDelay = Integer.MAX_VALUE >> 1;
        this.spawned = true;
        //Wait a little bit before sending a strike at the player.
        combat.setCombatDelay(6);
        resetAnkouSpawnDelay();
        this.temporaryAttributes.remove("no demon bane");
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    private final int countOf(@NotNull final Class<? extends NPC> clazz) {
        val mutableInt = new MutableInt();
        for (val minion : minions) {
            if (clazz.isAssignableFrom(minion.getClass())) {
                mutableInt.increment();
            }
        }
        return mutableInt.intValue();
    }

    private final void resetAnkouSpawnDelay() {
        this.ankouSpawnDelay = Utils.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Utils.random(75, 100));
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        if (source != null) {
            if (source instanceof Player) {
                val player = (Player) source;
                if (instance.getAwakenedAltars() == 0 && !player.getBooleanAttribute("medium-combat-achievement23")) {
                    player.putBooleanAttribute("medium-combat-achievement23", true);
                    MediumTasks.sendMediumCompletion(player, 23);
                }
                if (player.getEquipment().getItem(EquipmentSlot.WEAPON) != null) {
                    if ((player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("silverlight")
                            || player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("darklight")
                            || player.getEquipment().getItem(EquipmentSlot.WEAPON).getName().toLowerCase().contains("arclight"))
                            && !player.getBooleanAttribute("medium-combat-achievement32")) {
                        player.putBooleanAttribute("medium-combat-achievement32", true);
                        MediumTasks.sendMediumCompletion(player, 32);
                    }
                }
                if (!player.getBooleanAttribute("has_taken_damage_from_skotizo")
                        && !player.getBooleanAttribute("elite-combat-achievement50")) {
                    player.putBooleanAttribute("elite-combat-achievement50", true);
                    EliteTasks.sendEliteCompletion(player, 50);
                }
                if (!this.getTemporaryAttributes().containsKey("no demon bane") && !player.getBooleanAttribute("elite-combat-achievement61")) {
                    player.putBooleanAttribute("elite-combat-achievement61", true);
                    EliteTasks.sendEliteCompletion(player, 61);
                }
            }
        }
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if (isDead() || isFinished()) {
            return;
        }
        if (ankouSpawnDelay <= Utils.currentTimeMillis()) {
            resetAnkouSpawnDelay();
            val ankouCount = countOf(DarkAnkou.class);
            if (ankouCount <= 0) {
                val tile = instance.getLocation(new Location(Utils.random(SkotizoInstance.southWesternTile.getX(), SkotizoInstance.northEasternTile.getX()),
                        Utils.random(SkotizoInstance.southWesternTile.getY(), SkotizoInstance.northEasternTile.getY()), getPlane()));
                minions.add(new DarkAnkou(tile, this).spawn());
            }
        } else if (hitpoints <= getMaxHitpoints() / 2 && demonSpawnDelay <= Utils.currentTimeMillis()) {
            setForceTalk(minionSpawningForceTalk);
            demonSpawnDelay = Utils.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
            val reanimatedDemonCount = countOf(ReanimatedDemon.class);
            if (reanimatedDemonCount >= 3) {
                return;
            }
            val tile = instance.getLocation(new Location(Utils.random(SkotizoInstance.southWesternTile.getX(), SkotizoInstance.northEasternTile.getX() - 10),
                    Utils.random(SkotizoInstance.southWesternTile.getY(), SkotizoInstance.northEasternTile.getY() - 10), getPlane()));
            for (int i = reanimatedDemonCount; i < 3; i++) {
                val location = tile.transform(Utils.random(10), Utils.random(10), 0);
                minions.add(new ReanimatedDemon(location, this).spawn());
                World.sendGraphics(graphics, location);
            }
        }
    }

    @Override
    public float getXpModifier(final Hit hit) {
        val awakenedAltars = instance.getAwakenedAltars();
        //Modify damage here and reflect on experience early on.
        hit.setDamage((int) (hit.getDamage() * (1F - (0.2F * awakenedAltars))));
        return 1;
    }

    @Override
    public int attack(final Entity target) {
        if (isWithinMeleeDistance(this, target)) {
            if (Utils.random(2) <= 1) {
                return meleeAttack(target);
            } else {
                return magicAttack(target);
            }
        }
        return magicAttack(target);
    }

    private int meleeAttack(final Entity target) {
        setAnimation(meleeAnimation);
        delayHit(this, 0, target, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), SLASH, target), HitType.MELEE));
        return combatDefinitions.getAttackSpeed();
    }

    private int magicAttack(final Entity target) {
        setAnimation(magicAnimation);
        delayHit(this, World.sendProjectile(this, target, magicProjectile), target, new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MAGIC, target), HitType.MAGIC));
        return combatDefinitions.getAttackSpeed();
    }

    @Override
    protected void onFinish(Entity source) {
        super.onFinish(source);
        instance.refreshOverlay();
        WorldTasksManager.schedule(() -> {
            //Avoid concurrent exception.
            val minions = new ObjectArrayList<>(this.minions);
            minions.forEach(NPC::sendDeath);

            val altars = new ObjectArrayList<>(instance.getAltars());
            altars.forEach(AwakenedAltar::sendDeath);
        });
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        val player = (Player) hit.getSource();
        if (!player.getBooleanAttribute("elite-combat-achievement61")) {
            val weapon = player.getWeapon().getName().toLowerCase();
            if (weapon.contains("silverlight") || weapon.contains("darklight") || weapon.contains("arclight")) {
                this.getTemporaryAttributes().put("no demon bane", false);
            }
        }
        if (!player.getBooleanAttribute("master-combat-achievement38")) {
            if (hit.getAttributes().getOrDefault("notMainFocusedTarget", false).equals(true)) {
                if (hitpoints - hit.getDamage() <= 0) {
                    player.putBooleanAttribute("master-combat-achievement38", true);
                    MasterTasks.sendMasterCompletion(player, 38);
                }
            }
        }
        super.handleIngoingHit(hit);
    }
}
