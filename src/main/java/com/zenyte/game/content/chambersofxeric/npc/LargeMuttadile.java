package com.zenyte.game.content.chambersofxeric.npc;

import com.zenyte.game.content.chambersofxeric.room.MuttadileRoom;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTask;
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
import com.zenyte.game.world.entity.npc.NPCCombat;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.CharacterLoop;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author Kris | 12. jaan 2018 : 22:23.37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class LargeMuttadile extends RaidNPC<MuttadileRoom> implements CombatScript {

	private static final Projectile proj = new Projectile(393, 20, 25, 10, 15, 8, 0, 5);
	private static final Graphics gfx = new Graphics(157, 0, 96);
	private static final Animation anim = new Animation(7420);

	public LargeMuttadile(final MuttadileRoom room, final Direction direction, final Location tile) {
		super(room.getRaid(), room, NpcId.MUTTADILE, tile);
		setSpawned(true);
		setDirection(direction.getDirection());
		this.direction = direction;
		this.room = room;
		combat = new LargeMuttadileCombatHandler(this);
		setForceMultiArea(true);
		setForceAggressive(true);
		setAggressionDistance(20);
		setAttackDistance(6);
		this.radius = 32;
		hasEaten = false;
	}

	@Override
	public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
		if (isFrozen()) {
			return false;
		}
		return super.addWalkStep(nextX, nextY, lastX, lastY, check);
	}

    @Override
    public double getRangedPrayerMultiplier() {
        return 0.33;
    }

	private final MuttadileRoom room;
	private final Direction direction;
	@Setter
	private boolean unleashed;
	@Setter
	private int ticks;
	private boolean feeding;
	private int count = 3, feedCount = 3;
	private int bites = -1;
	private int safespotTicks;
	private int safespotDelay;
	private boolean hasEaten;

    @Override
    public void applyHit(final Hit hit) {
    	if (!unleashed) {
    		return;
		}
        super.applyHit(hit);
        if (feeding) {
            hit.setDamage(0);
        }
    }

    @Override
    public void autoRetaliate(final Entity source) {
        if (safespotDelay > 0) {
            return;
        }
        super.autoRetaliate(source);
    }

    @Override
    public float getXpModifier(final Hit hit) {
        return feeding ? 0 : 1;
    }

    @Override
    public double getMagicPrayerMultiplier() {
        return 0.33;
    }

	public Direction getMovementDirection() {
		return direction;
	}

	@Override
	public void processNPC() {
		if (isDead()) {
			return;
		}
		if (isCantInteract()) {
			return;
		}
		if (!unleashed) {
			if (!room.isLaunched()) {
				return;
			}
			if (ticks-- <= 0) {
				ticks = 5;
				if (Utils.random(3) != 0) {
				    return;
                }
				final List<Entity> possibleTargets = getPossibleTargets(EntityType.PLAYER);
				if (possibleTargets.isEmpty()) {
					return;
				}
				val min = Math.min(8, (int) Math.ceil(possibleTargets.size() / 8F));
				val max = Math.min(8, Math.max(1, possibleTargets.size() - 1));
				int count = Utils.random(min, max);
				while (count > 0) {
					count--;
					final Entity target = possibleTargets.remove(Utils.random(possibleTargets.size() - 1));
					if (target == null) {
						continue;
					}
					if (target.getPlane() != getPlane()) {
						continue;
					}
					val prediseDelay = proj.getProjectileDuration(this.getLocation(), target.getLocation());
                    World.sendSoundEffect(target.getLocation(), new SoundEffect(magicHitSound.getId(), magicHitSound.getRadius(), prediseDelay));
                    target.setGraphics(new Graphics(gfx.getId(), gfx.getHeight(), prediseDelay));
                    val delay = World.sendProjectile(this, target, proj);
                    iteratePath(target, delay);
                    CombatUtilities.delayHit(LargeMuttadile.this, delay, target,
                            new Hit(CombatUtilities.getRandomMaxHit(LargeMuttadile.this,
                                            (int) (25 * (room.getRaid().isChallengeMode() ? 1.5F : 1F)), CombatScript.MAGIC, target), HitType.MAGIC));
				}
			}
		} else {
            safespotDelay--;
			if (feedCount > 0 && !feeding && !room.getTree().isDead() && !room.getTree().isFinished() && count > 0 && getHitpoints() < getMaxHitpoints() / 2
					&& Utils.random(2) == 0) {
				feeding = true;
				combat.removeTarget();
				setFaceEntity(room.getTree());
				if (!isFrozen()) {
					calcFollow(room.getTree(), -1, true, true, true);
				}
				count--;
				safespotDelay = 5;
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
					setAnimation(anim);
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
	}

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
        return safespotDelay <= 0;
    }

	private void iteratePath(@NotNull final Entity target, final int delay) {
        WorldTasksManager.schedule(new TickTask() {

            private final Location current = new Location(getMiddleLocation());
            private final Set<Entity> hitEntitiesSet = new ObjectOpenHashSet<>();
            private final int tilesPerTick = (int) target.getLocation().getDistance(current) / Math.max(1, delay);
            @Override
            public void run() {
                if (ticks++ >= delay) {
                    stop();
                    return;
                }
                val path = Utils.calculateLine(current.getX(), current.getY(), target.getLocation().getX(), target.getLocation().getY(), current.getPlane());
                val length = ticks == delay ? Integer.MAX_VALUE : tilesPerTick;

                for (int i = 0; i < length; i++) {
                    if (i >= path.size()) {
                        break;
                    }
                    val tile = path.get(i);
                    CharacterLoop.forEach(tile, 2, Player.class, t -> {
                        if (t == target || !hitEntitiesSet.add(t)) {
                            return;
                        }
                        CombatUtilities.delayHit(LargeMuttadile.this, -1, t,
                                new Hit(LargeMuttadile.this, CombatUtilities.getRandomMaxHit(LargeMuttadile.this,
                                        (int) (25 * (room.getRaid().isChallengeMode() ? 1.5F : 1F)), CombatScript.MAGIC, target), HitType.MAGIC));
                    });
                }
                if (path.size() > tilesPerTick) {
                    current.setLocation(path.get(tilesPerTick));
                }
            }
        }, 0, 0);
    }

	@Override
	public void sendDeath() {
        val source = getMostDamagePlayerCheckIronman();
        onDeath(source);
		if (!room.getPlayers().isEmpty() && !SmallMuttadile.hasEaten() && !this.hasEaten()) {
			val players = room.getPlayers();
			for (val player : players) {
				if (!player.getBooleanAttribute("elite-combat-achievement25")) {
					player.putBooleanAttribute("elite-combat-achievement25", true);
					EliteTasks.sendEliteCompletion(player, 25);
				}
			}
		}
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
					World.spawnObject(new WorldObject(30031, 10, 0, new Location(getMiddleLocation())));
					room.clearPath();
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

	private final void stopFeeding() {
		feeding = false;
		setFaceEntity(null);
		checkAggressivity();
	}

    private static final Projectile rangedProj = new Projectile(1291, 25, 25, 15, 15, 28, 0, 5);
    private static final Projectile magicProj = new Projectile(393, 25, 25, 40, 15, 28, 0, 5);
    private static final Animation rangedAnim = new Animation(7421);
    private static final Animation meleeAnim = new Animation(7424);
    private static final Animation magicAnim = new Animation(7422);
    private static final SoundEffect rangedSound = new SoundEffect(385, 5, -1);
    private static final SoundEffect meleeSound = new SoundEffect(163, 5, 30);
    private static final SoundEffect magicSound = new SoundEffect(162, 5, 0);
    private static final SoundEffect magicHitSound = new SoundEffect(163, 5, -1);

    @Override
    public int attack(final Entity target) {
    	val meleeDistance = isWithinMeleeDistance(this, target);
        if (meleeDistance && Utils.random(1) == 0) {
            setAnimation(meleeAnim);
            World.sendSoundEffect(getMiddleLocation(), meleeSound);
            for (val player : room.getRaid().getPlayers()) {
                if (isWithinMeleeDistance(this, player)) {
                    delayHit(this, 0, target, new Hit(this, Utils.random(116), HitType.DEFAULT));
                }
            }
            return combatDefinitions.getAttackSpeed();
        }
        if (Utils.random(5) > 3) {
            WorldTasksManager.schedule(() -> calcFollow(target, Utils.random(1, 3), true, false, true), 1);
        }
        if (Utils.random(5) >= 1) {
            setAnimation(rangedAnim);
            World.sendSoundEffect(target.getLocation(), new SoundEffect(rangedSound.getId(), rangedSound.getRadius(), proj.getProjectileDuration(this.getLocation(), target.getLocation())));
            delayHit(this, World.sendProjectile(this, target, rangedProj), target, ranged(target, (int) (30 * (room.getRaid().isChallengeMode() ? 1.5F : 1F))));
        } else {
            setAnimation(magicAnim);
            World.sendSoundEffect(getMiddleLocation(), magicSound);
            World.sendSoundEffect(target.getLocation(), new SoundEffect(magicHitSound.getId(), magicHitSound.getRadius(), magicProj.getProjectileDuration(this.getLocation(),
                    target.getLocation())));
            val delay = World.sendProjectile(this, target, magicProj);
            iteratePath(target, delay);
            delayHit(this, delay, target, magic(target, (int) (25 * (room.getRaid().isChallengeMode() ? 1.5F : 1F))));
        }
        return combatDefinitions.getAttackSpeed();
    }

	public boolean hasEaten() {
		return hasEaten;
	}


    private static final class LargeMuttadileCombatHandler extends NPCCombat {

		LargeMuttadileCombatHandler(final LargeMuttadile npc) {
			super(npc);
		}

		@Override
		public boolean checkAll() {
			if (((LargeMuttadile) npc).feeding) {
				return false;
			}
			return super.checkAll();
		}
	}

}
