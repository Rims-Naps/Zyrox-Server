package com.zenyte.game.content.skills.farming.hespori;

import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.Toxins;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Kris | 24/02/2019 13:45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Hespori extends NPC implements CombatScript {

    /**
     * The ranged attack animation, projectiles and sound effects.
     */
    private static final Animation rangedAttackAnimation = new Animation(8224);
    private static final Projectile firstRangedProj = new Projectile(1639, 24, 14, 30, 11, 25, 64, 5);
    private static final Projectile secondRangedProj = new Projectile(1639, 24, 14, 55, 11, 25, 64, 5);
    private static final SoundEffect rangedAttackSound = new SoundEffect(4061, 0, -1);

    /**
     * The magic attack animation, projectiles, graphics and sound effects.
     */
    private static final Animation magicAttackAnimation = new Animation(8223);
    private static final Projectile magicProjectile = new Projectile(1640, 85, 39, 90, 11, 25, 64, 5);
    private static final Graphics magicHitGraphics = new Graphics(1641, -1, 124);
    private static final SoundEffect magicCastSound = new SoundEffect(4056);
    private static final SoundEffect magicHitSound = new SoundEffect(4063, 0, -1);

    /**
     * The snare attack projectile, graphics and sound effects.
     */
    private static final Projectile snareProjectile = new Projectile(1642, 85, 39, 90, 11, 25, 64, 5);
    private static final SoundEffect snareCastSound = new SoundEffect(4062);
    private static final Graphics snareHitGraphics = new Graphics(1643, 0, 124);
    private static final SoundEffect snareHitSound = new SoundEffect(4065);

    /**
     * The entangement counter key used to determine how strong the vines entanglement still is.
     */
    static final String entanglementAttrKey = "Hespori entanglement";

    /**
     * The entanglement lock verification, used to ensure that the world task executing the hit is actually the same
     * one that was submitted by the attack itself, in other words - prevents dealing damage if the player has
     * already broken free of the vines and Hespori has fired another entanglement attack.
     */
    static final String entanglementVerificationAttrKey = "Hespori entanglement lock";

    /**
     * The package-level constructor for hespori, to be instantiated through {@link HesporiInstance}
     *
     * @param instance the instanced area for the Hespori.
     */
    Hespori(final HesporiInstance instance) {
        super(8583, instance.getLocation(1246, 10086, 0), Direction.SOUTH, 0);
        this.instance = instance;
        this.spawned = true;
    }

    /**
     * The instanced area for the Hespori.
     */
    private final HesporiInstance instance;

    /**
     * Applies the hits pending for the NPC - overridden to allow the immunity effect on Hespori whilst flower buds
     * are alive.
     *
     * @param hit the hit being processed.
     */
    @Override
    public void applyHit(final Hit hit) {
        super.applyHit(hit);
        if (instance.isImmune()) {
            hit.setDamage(0);
            instance.owner.sendMessage("The Hespori is currently immune to your attacks.");
        }
    }

    /**
     * Gets a list of the possible targets for the Hespori boss. Only adds the owner of the instance to the list,
     * never anyone else.
     *
     * @param type the type of the target.
     * @return a list of the possible targets.
     */
    @Override
    public List<Entity> getPossibleTargets(final EntityType type) {
        if (!possibleTargets.isEmpty()) {
            possibleTargets.clear();
        }
        if (predicate.test(instance.owner)) {
            possibleTargets.add(instance.owner);
        }
        return possibleTargets;
    }

    /**
     * The drop method for the NPC. We override it since the Hespori doesn't directly drop the items, as you receive
     * the loot through clearing the patch.
     *
     * @param tile the tile where the drop should be dropped.
     */
    @Override
    public void drop(final Location tile) {

    }

    /**
     * Gets the xp modifier that is used to multiply the combat xp when attacking the monster.
     *
     * @return the xp modifier, 0-1.
     * @param hit the hit dealt.
     */
    @Override
    public float getXpModifier(final Hit hit) {
        return instance.isImmune() ? 0 : 1;
    }

    /**
     * The Hespori will need to face south once it dies, to properly align itself with the actual patch object.
     *
     * @param source the killer entity.
     */
    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        this.setFaceLocation(new Location(getX(), 0, 0));
        if (source != null) {
            if (source instanceof Player) {
                val player = (Player) source;
                if (player.getBooleanAttribute("used_special") && !player.getBooleanAttribute("hard-combat-achievement35")) {
                    player.putBooleanAttribute("hard-combat-achievement35", true);
                    HardTasks.sendHardCompletion(player, 35);
                }
            }
        }
    }

    /**
     * The instance needs to spawn the actual Hespori patch back into the game, as well as update its status to allow
     * harvesting it.
     *
     * @param source the killer entity.
     */
    @Override
    public void onFinish(final Entity source) {
        super.onFinish(source);
        instance.finish();
    }

    /**
     * THe Hespori stands on-top of a clipped farming patch, meaning we must override the projectile clip method to
     * not check for clipping whilst attacking it.
     *
     * @param player the player attacking Hespori.
     * @return whether or not to check for projectile clipping underneath the NPC.
     */
    @Override
    public boolean checkProjectileClip(final Player player) {
        return false;
    }

    /**
     * Checks whether or not the NPC can actually ever walk.
     *
     * @return whether the NPC can ever walk.
     */
    @Override
    public boolean isMovableEntity() {
        return false;
    }

    /**
     * Removes the hitpoints from the player based on the damage output of the hit. Overridden to allow the effect of
     * flower buds re-blooming when the Hespori reaches 66.6% & 33.3% health respectively.
     *
     * @param hit the hit being processed.
     */
    @Override
    protected void removeHitpoints(final Hit hit) {
        val health = this.hitpoints;
        super.removeHitpoints(hit);
        if (health > 200 && hitpoints <= 200 || health > 100 && hitpoints <= 100) {
            if (hitpoints > 0) {
                instance.setBlooming();
            }
        }
    }

    /**
     * The Hespori's magic attacks will deal damage even through prayer - up to half of the normal damage.
     *
     * @return the modifier for the damage whilst praying from magic.
     */
    @Override
    public double getMagicPrayerMultiplier() {
        return 0.5;
    }

    /**
     * The Hespori's ranged attacks will deal damage even through prayer - up to half of the normal damage.
     *
     * @return the modifier for the damage whilst praying from ranged.
     */
    @Override
    public double getRangedPrayerMultiplier() {
        return 0.5;
    }

    /**
     * Runs the attack method of Hespori. Launches either a snare attack which has a 1/18 chance of occurring - which
     * will snare the player to the ground, not allowing them to move. They will need to spam-click to free
     * themselves. After the 6th attempt of freeing themselves, they will be set free. if they do not free themselves
     * in time, they will be punished with a deadly attack that deals anywhere from 20 to 40 damage.
     * If the attack doesn't roll as a snare attack, there will be a 50/50 chance of either a ranged or a magic
     * attack being dealt. The ranged attack will fire two swarms towards the player - each dealing up to 7 damage
     * which can be negated by half through protection prayers. The magic attack will send one strong attack, dealing
     * up to 14 damage on impact - the attack can only be negated by half as well.
     * Besides the normal three attack styles, the Hespori may also poison the target.
     *
     * @param target the target the Hespori is attacking.
     * @return the delay until the next attack.
     */
    @Override
    public int attack(Entity target) {
        if (!(target instanceof Player)) {
            return 0;
        }
        val player = (Player) target;
        val attr = player.getTemporaryAttributes();
        if (Utils.random(2) == 0) {
            player.getToxins().applyToxin(Toxins.ToxinType.POISON, 4);
        }
        if (Utils.random(17) == 0 && !attr.containsKey(entanglementAttrKey)) {
            setAnimation(magicAttackAnimation);
            player.sendSound(snareCastSound);
            World.scheduleProjectile(this, target, snareProjectile).schedule(() -> {
                if (vanished(player)) {
                    return;
                }
                player.sendSound(snareHitSound);
                player.setGraphics(snareHitGraphics);
                player.resetWalkSteps();
                player.sendMessage(Colour.RED + "The Hespori entangles you in some vines!");
                attr.put(entanglementAttrKey, 5);
                val $lock = new Object();
                attr.put(entanglementVerificationAttrKey, $lock);
                WorldTasksManager.schedule(() -> {
                    if (vanished(player)) {
                        return;
                    }
                    if (attr.get(entanglementVerificationAttrKey) == $lock && attr.remove(entanglementAttrKey) != null) {
                        player.applyHit(new Hit(Utils.random(20, 40), HitType.DEFAULT));
                        player.sendMessage(Colour.RED + "The vines suddenly break, damaging you in the process.");
                    }
                }, 13);
            });
        } else {
            if (Utils.random(1) == 0) {
                setAnimation(rangedAttackAnimation);
                delayHit(World.sendProjectile(this, target, firstRangedProj), target, ranged(target, 7));
                delayHit(World.sendProjectile(this, target, secondRangedProj), target, ranged(target, 7));
                player.sendSound(new SoundEffect(rangedAttackSound.getId(), 0,
                        firstRangedProj.getProjectileDuration(this.getMiddleLocation(), target)));
                player.sendSound(new SoundEffect(rangedAttackSound.getId(), 0,
                        secondRangedProj.getProjectileDuration(this.getMiddleLocation(), target)));
            } else {
                setAnimation(magicAttackAnimation);
                delayHit(World.sendProjectile(this, target, magicProjectile), target, magic(target, 14));
                player.sendSound(magicCastSound);
                val delay = magicProjectile.getProjectileDuration(this.getMiddleLocation(), target);
                player.setGraphics(new Graphics(magicHitGraphics.getId(), delay, magicHitGraphics.getHeight()));
                player.sendSound(new SoundEffect(magicHitSound.getId(), 0, delay));
            }
        }
        return combatDefinitions.getAttackSpeed();
    }

    /**
     * Checks to ensure that the player is still within the given instance, and has not yet died.
     *
     * @param player the player being attacked by Hespori.
     * @return whether or not the player is either dead, or has left.
     */
    private boolean vanished(@NotNull final Player player) {
        return !player.inArea(HesporiInstance.name) || player.isDead() || isDead() || isFinished();
    }
}
