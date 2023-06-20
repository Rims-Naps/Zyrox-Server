package com.zenyte.game.content.boss.kraken;

import com.google.common.collect.ImmutableList;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.HardTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MasterTasks;
import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.EntityHitBar;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.PlayerCombat;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 21 mei 2018 | 19:11:12
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class Kraken extends NPC implements CombatScript, Spawnable {

	private static final Animation AWAKE_ANIM = new Animation(7135);
	private static final Animation AWAKE_ANIM_TENTACLE = new Animation(3860);
	private static final ImmutableList<Byte[]> POSITIONS = ImmutableList.of(new Byte[] { -3, 4 }, new Byte[] { 6, 4 }, new Byte[] { -3, 0 },
			new Byte[] { 6, 0 });
	private static final Projectile PROJECTILE = new Projectile(156, 80, 28, 35, 20, 18, 64, 5);
	private static final Graphics SPLASH_GRAPHICS = new Graphics(85, 0, 124);

	@Getter
	private final EnormousTentacle[] tentacles = new EnormousTentacle[4];
	private int ticks;

	@Override
	public int getRespawnDelay() {
		return 14;
	}

	@Override
	public boolean isTolerable() {
		return false;
	}


    @Override
    public float getXpModifier(final Hit hit) {
		if (!attackable()) {
			return 0F;
		}
        return hit.getHitType() == HitType.RANGED ? 0.5F : 1F;
    }

    @Override
    public void handleIngoingHit(final Hit hit) {
        if (!(hit.getSource() instanceof Player)) {
            return;
        }
        val target = (Player) hit.getSource();
        if (!attackable()) {
            target.sendMessage("There was no response.");
            combat.setTarget(null);
			target.cancelCombat();
            return;
        }
        if (hit.getHitType() == HitType.RANGED) {
            hit.setDamage(hit.getDamage() / 2);
        }
        super.handleIngoingHit(hit);
        if (id == 496) {
            WorldTasksManager.schedule(new WorldTask() {
                int ticks;

                @Override
                public void run() {
                    switch (ticks++) {
                        case 0:
                            setTransformation(494);
                            setAnimation(AWAKE_ANIM);
                            faceEntity(target);
                            PlayerCombat.attackEntity(target, Kraken.this, null);
                            break;
                        case 3:
                            getCombat().setTarget(target);
                            stop();
                            break;
                    }
                }
            }, 0, 1);
        }
    }

	public static void startKraken(Player target, Kraken npc) {
		for (val tentacle : npc.getTentacles()) {
			int id = tentacle.getId();
			if (id == 5534) {
				WorldTasksManager.schedule(new WorldTask() {
					int ticks;

					@Override
					public void run() {
						switch (ticks++) {
							case 0:
								tentacle.setTransformation(5535);
								tentacle.setAnimation(AWAKE_ANIM_TENTACLE);
								tentacle.faceEntity(target);
								break;
							case 3:
								tentacle.getCombat().setTarget(target);
								stop();
								break;
						}
					}
				}, 0, 1);
			}
		}

		WorldTasksManager.schedule(new WorldTask() {
			int ticks;

			@Override
			public void run() {
				switch (ticks++) {
					case 0:
						npc.setTransformation(494);
						npc.setAnimation(AWAKE_ANIM);
						npc.faceEntity(target);
						PlayerCombat.attackEntity(target, npc, null);
						break;
					case 3:
						npc.getCombat().setTarget(target);
						stop();
						break;
				}
			}
		}, 0, 1);
	}


    public Kraken(final int id, final Location tile, final Direction direction, final int radius) {
		super(id, tile, direction, radius);
		if (isAbstractNPC()) {
			return;
		}
		setForceMultiArea(true);
		setAggressionDistance(20);
		this.attackDistance = 50;
		this.hitBar = new EntityHitBar(this) {
		    @Override
            protected int getSize() {
                return 4;
            }
        };
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (id == 494) {
			if (getCombat().underCombat()) {
				ticks = 0;
			}
			if (++ticks >= 50) {
				setTransformation(496);
				cancelCombat();
				heal(getMaxHitpoints());
				for (val tentacle : tentacles) {
					if (tentacle == null) {
						continue;
					}
					tentacle.setTransformation(5534);
					tentacle.heal(tentacle.getMaxHitpoints());
				}
			}
		}
	}

	@Override
	public NPC spawn() {
		spawnTentacles();
		return super.spawn();
	}

	@Override
    public void dropItem(final Player killer, final Item item, final Location tile, boolean guaranteedDrop) {
	    //Kraken always drops loot underneath the player.
	    tile.setLocation(killer.getLocation());
	    super.dropItem(killer, item, tile, guaranteedDrop);
    }

	@Override
	public void drop(final Location location) {
		val player = getDropRecipient();
		if (player == null) {
			return;
		}
		if (player.isLocked()) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (player.isLocked()) {
						return;
					}
					drop(player.getLocation());
					stop();
				}
			}, 0, 0);
			return;
		}
		super.drop(player.getLocation());
	}

	private void spawnTentacles() {
		for (int index = 0; index < POSITIONS.size(); index++) {
			val position = POSITIONS.get(index);
			tentacles[index] = new EnormousTentacle(NpcId.WHIRLPOOL_5534, getLocation().transform(position[0], position[1], 0), Direction.SOUTH, 0);
			tentacles[index].spawn();
		}
	}

	private boolean attackable() {
		for (val tentacle : tentacles) {
			if (tentacle == null) {
				continue;
			}
			if (!tentacle.isFinished() && tentacle.getId() == 5534) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void processHit(final Hit hit) {
		if (!(hit.getSource() instanceof Player)) {
			return;
		}
		if (!attackable()) {
			combat.setTarget(null);
			return;
		}
		super.processHit(hit);
	}

	@Override
	protected void invalidateItemCharges(@NotNull final Item item) {

	}

	@Override
	public void onFinish(final Entity source) {
		drop(getMiddleLocation());
		reset();
		finish();
		if (!spawned) {
			setRespawnTask();
		}
		if (source != null) {
			if (source instanceof Player) {
				val player = (Player) source;
				sendNotifications(player);
			}
		}
		setId(496);
	}

	@Override
	public void onDeath(final Entity source) {
		super.onDeath(source);
		if (source instanceof Player) {
			val player = (Player) source;
			int deathCount = 0;
			for (int i = 0; i < 4; i++) {
				val tentacle = tentacles[i];
				if (tentacle.isDead() || tentacle.isFinished() || tentacle.getHitpoints() == 0) {
					deathCount++;
				}
			}
			if (deathCount >= 4 && !player.getBooleanAttribute("hard-combat-achievement38")) {
				player.putBooleanAttribute("hard-combat-achievement38", true);
				HardTasks.sendHardCompletion(player, 38);
			}
			if (player.getNumericAttribute("kraken_kc_on_instance_creation").intValue() + 24 <= player.getNotificationSettings().getKillcount("kraken")
					&& !player.getBooleanAttribute("hard-combat-achievement47")
					&& player.getArea() instanceof KrakenInstance) {
				player.putBooleanAttribute("hard-combat-achievement47", true);
				HardTasks.sendHardCompletion(player, 47);
			}
			if (player.getNumericAttribute("kraken_kc_on_instance_creation").intValue() + 49 <= player.getNotificationSettings().getKillcount("kraken")
					&& !player.getBooleanAttribute("elite-combat-achievement70")
					&& player.getArea() instanceof KrakenInstance) {
				player.putBooleanAttribute("elite-combat-achievement70", true);
				EliteTasks.sendEliteCompletion(player, 70);
			}
			if (player.getNumericAttribute("kraken_kc_on_instance_creation").intValue() + 74 <= player.getNotificationSettings().getKillcount("kraken")
					&& !player.getBooleanAttribute("master-combat-achievement51")
					&& player.getArea() instanceof KrakenInstance) {
				player.putBooleanAttribute("master-combat-achievement51", true);
				MasterTasks.sendMasterCompletion(player, 51);
			}
		}
		WorldTasksManager.schedule(() -> {
			for (int index = 0; index < tentacles.length; index++) {
				if (tentacles[index] == null) {
					continue;
				}
				tentacles[index].sendDeath();
				tentacles[index] = null;
			}
		}, 1);
	}

	@Override
	public boolean canAttack(final Player source) {
		return true;
	}

	@Override
	public boolean isFreezeImmune() {
		return true;
	}

	@Override
    public int attack(final Entity target) {
        setUnprioritizedAnimation(getCombatDefinitions().getAttackDefinitions().getAnimation());
        delayHit(this, World.sendProjectile(this, target, PROJECTILE), target,
                new Hit(this, getRandomMaxHit(this, getCombatDefinitions().getMaxHit(), MAGIC, target), HitType.MAGIC).onLand(hit -> {
                    if (hit.getDamage() <= 0) {
                        target.setGraphics(SPLASH_GRAPHICS);
                    }
                }));
        return getCombatDefinitions().getAttackSpeed();
    }

	@Override
	public boolean validate(final int id, final String name) {
		return id == 494 || id == 496;
	}

}
