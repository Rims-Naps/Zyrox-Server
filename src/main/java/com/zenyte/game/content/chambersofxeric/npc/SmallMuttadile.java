package com.zenyte.game.content.chambersofxeric.npc;

import com.zenyte.game.content.chambersofxeric.room.MuttadileRoom;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import lombok.NonNull;
import lombok.val;

/**
 * @author Kris | 12. jaan 2018 : 19:30.31
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class SmallMuttadile extends RaidNPC<MuttadileRoom> implements CombatScript {

	private static final Animation ANIM = new Animation(7420);
	
	public SmallMuttadile(final MuttadileRoom room, final Location tile) {
		super(room.getRaid(), room, NpcId.MUTTADILE_7562, tile);
		this.room = room;
		combat = new SmallMuttadileCombatHandler(this);
		setForceMultiArea(true);
		setForceAggressive(true);
		setAggressionDistance(20);
		setAttackDistance(6);
		radius = 32;
        hasEaten = false;
	}

	private final MuttadileRoom room;
	private boolean feeding;
	private int count = 3, feedCount = 3;
	private int bites = -1;
	private int ticks = 5;
    private int safespotTicks;
    private int safespotDelay;
    private static boolean hasEaten;

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        if (isFrozen()) {
            return false;
        }
        return super.addWalkStep(nextX, nextY, lastX, lastY, check);
    }

    @Override
    public void applyHit(final Hit hit) {
        super.applyHit(hit);
        if (feeding) {
            hit.setDamage(0);
        }
    }

    @Override
    public float getXpModifier(final Hit hit) {
        return feeding ? 0 : 1;
    }

	@Override
	public void processNPC() {
        safespotDelay--;
		if (feedCount > 0 && !feeding && !room.getTree().isDead() && !room.getTree().isFinished() && count > 0 && getHitpoints() < getMaxHitpoints() / 2 && Utils.random(2) == 0) {
			feeding = true;
			combat.removeTarget();
            setFaceEntity(room.getTree());
            if (!isFrozen()) {
                calcFollow(room.getTree(), -1, true, true, true);
            }
			count--;
		} else if (feeding) {
		    if (!isFrozen()) {
                calcFollow(room.getTree(), -1, true, true, true);
            }
            if (!Utils.isOnRange(getX(), getY(), getSize(), room.getTree().getX(), room.getTree().getY(), room.getTree().getSize(),
                    0)) {
                if (!hasWalkSteps() && safespotDelay <= 0) {
                    feeding = false;
                    return;
                }
                return;
            }
            if (bites == 0) {
                bites = -1;
                stopFeeding();
                safespotDelay = 0;
                return;
            }
            if (ticks-- > 0) {
                return;
            }
            if (!hasWalkSteps()) {
                if (bites == -1) {
                    feedCount--;
                    bites = getHitpoints() < (getMaxHitpoints() * 0.4F) ? 3 : 2;
                }
                bites--;
                setAnimation(ANIM);
                ticks = 3;
                val amount = (int) (getMaxHitpoints() * 0.1F);
                heal(amount);
                safespotDelay = ticks;
                hasEaten = true;
            }
		} else {
			super.processNPC();
            if (safespotTicks++ % 3 == 0) {
                val target = combat.getTarget();
                if (target == null || hasWalkSteps() || !isProjectileClipped(target, false)) {
                    return;
                }
                resetWalkSteps();
                val dest = getFaceLocation(target, 15, 1024);
                calcFollow(dest, 5, true, false, false);
                safespotDelay = randomWalkDelay = 5;
                combat.removeTarget();
                WorldTasksManager.schedule(() -> {
                    if (combat.getTarget() == null) {
                        combat.setTarget(target);
                    }
                }, safespotDelay - 1);
            }
		}
	}

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
        return safespotDelay <= 0;
    }

    private final void stopFeeding() {
		feeding = false;
		setFaceEntity(null);
		checkAggressivity();
	}
	
	@Override
	public void sendDeath() {
        val source = getMostDamagePlayerCheckIronman();
        onDeath(source);
        resetWalkSteps();
        combat.removeTarget();
        setAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			private int loop;
			@Override
			public void run() {
				if (loop == 0) {
                    val spawnDefinitions = combatDefinitions.getSpawnDefinitions();
                    setAnimation(spawnDefinitions.getDeathAnimation());
                    val sound = spawnDefinitions.getDeathSound();
                    if (sound != null && source != null) {
                        source.sendSound(new SoundEffect(sound.getId(), 10, 0));
                    }
				} else if (loop == 1) {
					drop(getMiddleLocation());
					room.unleashLargeMuttadile();
					reset();
					finish();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
    public void onDrop(@NonNull final Player killer) {
        super.onDrop(killer);
        room.getRaid().sendGlobalMessage("As the Muttadile is slain, supplies are dropped for " + killer.getName() + ".");
    }

    @Override
    public void autoRetaliate(final Entity source) {
        if (safespotDelay > 0) {
            return;
        }
        super.autoRetaliate(source);
    }

    private static final Projectile proj = new Projectile(1291, 23, 25, 15, 5, 28, 0, 5);
    private static final Animation attackAnim = new Animation(7421);
    private static final Animation meleeAnim = new Animation(7420);
    private static final SoundEffect rangedSound = new SoundEffect(385, 5, -1);
    private static final SoundEffect meleeSound = new SoundEffect(2548, 5, 0);

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.33;
    }

    @Override
    public int attack(final Entity target) {
        val meleeDist = isWithinMeleeDistance(this, target);
        if (meleeDist && Utils.random(7) >= 2) {
            setAnimation(meleeAnim);
            World.sendSoundEffect(getMiddleLocation(), meleeSound);
            delayHit(this, 0, target, melee(target, (int) (47 * (room.getRaid().isChallengeMode() ? 1.5F : 1F))));
            return combatDefinitions.getAttackSpeed();
        }
        if (!meleeDist && Utils.random(5) > 3) {
            WorldTasksManager.schedule(() -> calcFollow(target, Utils.random(1, 3), true, false, true), 1);
        }

        setAnimation(attackAnim);
        World.sendSoundEffect(target.getLocation(), new SoundEffect(rangedSound.getId(), rangedSound.getRadius(), proj.getProjectileDuration(this.getLocation(), target.getLocation())));
        delayHit(this, World.sendProjectile(this, target, proj), target, ranged(target, (int) (30 * (room.getRaid().isChallengeMode() ? 1.5F : 1F))));
        return combatDefinitions.getAttackSpeed();
    }

    public static boolean hasEaten() {
        return hasEaten;
    }

    private static final class SmallMuttadileCombatHandler extends NPCCombat {

		SmallMuttadileCombatHandler(final SmallMuttadile npc) {
			super(npc);
		}
		
		@Override
		public boolean checkAll() {
			if (((SmallMuttadile) npc).feeding) {
				return false;
			}
			return super.checkAll();
		}
	}
	
}
