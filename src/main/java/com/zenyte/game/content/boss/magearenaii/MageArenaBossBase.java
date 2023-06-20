package com.zenyte.game.content.boss.magearenaii;

import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.magic.CombatSpell;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 21/06/2019 17:02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class MageArenaBossBase extends NPC implements CombatScript {

    private static final Projectile teleblockProjectile = new Projectile(1299, 43, 25, 40, 0, 28, 64, 5);
    private static final Projectile barrageProjectile = new Projectile(368, 43, 0, 51, 23, 70, 64, 5);

    MageArenaBossBase(final int id, @NotNull final Player owner, @NotNull final Location tile) {
        super(id, tile, Direction.SOUTH, 5);
        this.owner = owner;
        this.maxDistance = 50;
        this.attackDistance = 10;
        spawned = true;
        val death = combatDefinitions.getSpawnDefinitions().getDeathAnimation();
        if (death != null) {
            deathDelay = Math.max(Math.min((int) Math.ceil(death.getDuration() / 600F), 10), 1);
        }
    }

    @Override
    public void sendDeath() {
        val source = getMostDamagePlayerCheckIronman();
        onDeath(source);
        WorldTasksManager.schedule(new TickTask() {
            @Override
            public void run() {
                if (ticks == 0) {
                    val spawnDefinitions = combatDefinitions.getSpawnDefinitions();
                    setAnimation(spawnDefinitions.getDeathAnimation());
                    val sound = spawnDefinitions.getDeathSound();
                    if (sound != null && source != null) {
                        source.sendSound(sound);
                    }
                } else if (ticks == deathDelay) {
                    onFinish(source);
                    stop();
                    return;
                }
                ticks++;
            }
        }, 0, 0);
    }

    public NPC spawn() {
        final NPC npc = super.spawn();
        final Animation animation = combatDefinitions.getSpawnDefinitions().getSpawnAnimation();
        final int delay = animation.getCeiledDuration() / 600;
        lock(delay);
        setAnimation(animation);
        WorldTasksManager.schedule(() -> getCombat().forceTarget(owner), delay);
        return npc;
    }

    private int counter;

    @Override
    public void processNPC() {
        super.processNPC();
        if ((!isUnderCombat() && ++counter > 200) || owner.isFinished() || owner.isDead() || owner.getLocation().getDistance(this.getLocation()) > 25 || owner.getPlane() != getPlane()) {
            if (isLocked()) {
                return;
            }
            //Clear received damage so no one gets the drop and then properly slay the monster.
            getReceivedDamage().clear();
            sendDeath();
            lock();
        }
    }

    @Override
    public boolean isAttackable(final Entity e) {
        if (e != owner) {
            if (e instanceof Player) {
                ((Player) e).sendMessage("You cannot attack this creature.");
            }
            return false;
        }
        return true;
    }

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
        return entity == owner;
    }

    @Override
    public void onFinish(final Entity source) {
        super.onFinish(source);
        owner.getTemporaryAttributes().remove("MAII NPC");
    }

    abstract Animation getMagicAnimation();
    abstract Animation getMeleeAnimation();
    abstract Animation getSpecialAnimation();

    abstract CombatSpell validSpell();

    Graphics hitGraphics() {
        return validSpell().getHitGfx();
    }

    @Override
    public void applyHit(final Hit hit) {
        super.applyHit(hit);
        val type = hit.getHitType();
        if (type != HitType.MELEE && type != HitType.MAGIC && type != HitType.RANGED) {
            return;
        }
        if (hit.getWeapon() != validSpell()) {
            hit.setDamage(0);
        }
    }

    @Override
    public float getXpModifier(final Hit hit) {
        val type = hit.getHitType();
        if (type != HitType.MELEE && type != HitType.MAGIC && type != HitType.RANGED) {
            return 1;
        }
        if (hit.getWeapon() != validSpell()) {
            return 0;
        }
        return 1;
    }

    @Override
    public double getMagicPrayerMultiplier() {
        return 0.5;
    }

    @Override
    public double getMeleePrayerMultiplier() {
        return 0.5;
    }

    abstract int meleeFrequency();

    abstract int meleeSpeed();

    abstract int meleeMax();

    abstract AttackType meleeAttackType();

    abstract boolean canUseSpecial(final Entity target);

    @Override
    public boolean isTickEdible() {
        return false;
    }

    @Override
    public boolean isEntityClipped() {
        return false;
    }

    @Override
    public boolean isTolerable() {
        return false;
    }

    @Override
    public boolean isForceAttackable() {
        return true;
    }

    protected final Player owner;

    @Override
    public int attack(final Entity target) {
        val specialsList = new IntArrayList();
        if (target instanceof Player && ((Player) target).getVariables().getTime(TickVariable.TELEBLOCK) <= 0) {
            specialsList.add(0);
        }
        if (!target.isFrozen()) {
            specialsList.add(1);
        }
        if (canUseSpecial(target)) {
            specialsList.add(2);
        }
        val special = !specialsList.isEmpty() && Utils.random(4) == 0;
        if (!special) {
            val melee = isWithinMeleeDistance(this, target);
            if (melee && Utils.random(meleeFrequency()) == 0) {
                setAnimation(getMeleeAnimation());
                delayHit(-1, target, new Hit(this, getRandomMaxHit(this, meleeMax(), meleeAttackType(), target), HitType.MELEE));
                return meleeSpeed();
            } else {
                setAnimation(getMagicAnimation());
                val spell = validSpell();
                val delay = 0;
                val clientDelay = 30;
                val sound = spell.getHitSound();
                val gfx = hitGraphics();
                val hit = magic(target, 43);
                val splash = hit.getDamage() <= ((target instanceof Player && ((Player) target).getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC) ? 1 : 0));
                World.sendSoundEffect(target.getLocation(), splash ? new SoundEffect(227, 10, clientDelay) : new SoundEffect(sound.getId(), sound.getRadius(), clientDelay));
                target.setGraphics(splash ? new Graphics(85, clientDelay, 124) : new Graphics(gfx.getId(), clientDelay, gfx.getHeight()));
                delayHit(delay, target, hit);
            }
            return 6;
        }
        val specialType = specialsList.getInt(Utils.random(specialsList.size() - 1));
        if (specialType == 0) {
            setAnimation(getSpecialAnimation());
            val spell = CombatSpell.TELE_BLOCK;
            val clientDelay = teleblockProjectile.getProjectileDuration(getLocation(), target.getLocation());
            val sound = spell.getHitSound();
            val gfx = spell.getHitGfx();
            val hit = magic(target, 43);
            World.sendProjectile(this, target, spell.getProjectile());
            val splash = hit.getDamage() <= ((target instanceof Player && ((Player) target).getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC) ? 1 : 0));
            World.sendSoundEffect(target.getLocation(), splash ? new SoundEffect(227, 10, clientDelay) : new SoundEffect(sound.getId(), sound.getRadius(), clientDelay));
            target.setGraphics(splash ? new Graphics(85, clientDelay, 124) : new Graphics(gfx.getId(), clientDelay, gfx.getHeight()));
            if (!splash) {
                if (target instanceof Player) {
                    val p = (Player) target;
                    val halved = p.getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC);
                    p.getVariables().schedule(halved ? 100 : 200, TickVariable.TELEBLOCK);
                    p.getVariables().schedule(halved ? 200 : 300, TickVariable.TELEBLOCK_IMMUNITY);
                    p.sendMessage("<col=4f006f>A teleblock spell has been cast on you. It will expire in "
                            + (halved ? "1 minute, 0 seconds." : "2 minutes, 0 seconds.") + "</col>");
                }
            }
        } else if (specialType == 1) {
            setAnimation(getMagicAnimation());
            val spell = CombatSpell.ICE_BARRAGE;
            val projectile = barrageProjectile;
            val sound = spell.getHitSound();
            val gfx = spell.getHitGfx();
            val hit = magic(target, 43);
            val splash = hit.getDamage() <= ((target instanceof Player && ((Player) target).getPrayerManager().isActive(Prayer.PROTECT_FROM_MAGIC) ? 1 : 0));
            World.sendProjectile(this, target.getLocation(), projectile);
            val position = target.getLocation().getPositionHash();
            WorldTasksManager.schedule(() -> {
                if (position != target.getPosition().getPositionHash() || target.getLocation().getDistance(getLocation()) > 15) {
                    return;
                }
                World.sendSoundEffect(target.getLocation(), splash ? new SoundEffect(227, 10, 0) : new SoundEffect(sound.getId(), sound.getRadius(), 0));
                target.setGraphics(splash ? new Graphics(85, 0, 124) : new Graphics(gfx.getId(), 0, gfx.getHeight()));
                delayHit(-1, target, hit);
                if (!splash) {
                    spell.getEffect().spellEffect(this, target, 0);
                }
            }, projectile.getTime(this, target));
        } else {
            return special(target);
        }
        return 6;
    }

    abstract int special(final Entity target);

}
