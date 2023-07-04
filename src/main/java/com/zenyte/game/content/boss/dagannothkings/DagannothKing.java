package com.zenyte.game.content.boss.dagannothkings;

import com.zenyte.game.RuneDate;
import com.zenyte.game.content.achievementdiary.diaries.FremennikDiary;
import com.zenyte.game.content.boss.BossRespawnTimer;
import com.zenyte.game.content.combatachievements.combattasktiers.EliteTasks;
import com.zenyte.game.content.combatachievements.combattasktiers.MediumTasks;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.Projectile;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.entity.npc.combat.CombatScript;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;


/**
 * @author Tommeh | 19 mrt. 2018 : 20:39:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public final class DagannothKing extends NPC implements Spawnable, CombatScript {

	public DagannothKing(final int id, final Location tile, final Direction facing, final int radius) {
		super(id, tile, facing, radius);
		this.maxDistance = 64;
	}
	
	@Override
	public int getRespawnDelay() {
		return BossRespawnTimer.DAGANNOTH_KINGS.getTimer().intValue();
	}

	@Override
	public void onDeath(Entity source) {
		super.onDeath(source);
		if (source instanceof Player) {
			val player = (Player) source;
			val flag = id == NpcId.DAGANNOTH_SUPREME ? 0x1 : id == NpcId.DAGANNOTH_PRIME ? 0x2 : 0x4;
			player.getAchievementDiaries().update(FremennikDiary.KILL_DAGANNOTH_KINGS, flag);
			if (id == NpcId.DAGANNOTH_REX) {
				player.getAttributes().put("time_rex_kill", RuneDate.currentTimeMillis());
			} else if (id == NpcId.DAGANNOTH_PRIME) {
				player.getAttributes().put("time_other_dks_kill", RuneDate.currentTimeMillis());
				player.getAttributes().put("time_prime_kill", RuneDate.currentTimeMillis());
			} else if (id == NpcId.DAGANNOTH_SUPREME) {
				player.getAttributes().put("time_other_dks_kill", RuneDate.currentTimeMillis());
				player.getAttributes().put("time_supreme_kill", RuneDate.currentTimeMillis());
			}
			if (!player.getBooleanAttribute("medium-combat-achievement20") && id == NpcId.DAGANNOTH_REX && (super.isStunned() || super.isFrozen() || super.isMovementRestricted())) {
				player.putBooleanAttribute("medium-combat-achievement20", true);
				MediumTasks.sendMediumCompletion(player, 20);
			}
			if (!player.getBooleanAttribute("elite-combat-achievement28") && id == NpcId.DAGANNOTH_PRIME) {
				long timeOfShotRuneThrownAxe = 0;
				if (player.getAttributes().get("time_when_rune_thrown_axe_was_shot_at_prime_via_rex") instanceof Long) {
					timeOfShotRuneThrownAxe = (long) player.getAttributes().get("time_when_rune_thrown_axe_was_shot_at_prime_via_rex");
				}
				if (player.getAttributes().containsKey("time_when_rune_thrown_axe_was_shot_at_prime_via_rex")
						&& timeOfShotRuneThrownAxe + 1000L <= RuneDate.currentTimeMillis()) {
					player.putBooleanAttribute("elite-combat-achievement28", true);
					EliteTasks.sendEliteCompletion(player, 28);
				}
				player.getAttributes().remove("time_when_rune_thrown_axe_was_shot_at_prime_via_rex");
			}
			if (!player.getBooleanAttribute("elite-combat-achievement29") && id == NpcId.DAGANNOTH_PRIME) {
				val attackers = player.getPossibleTargets(EntityType.NPC);
				int count = 0;
				for (val e : attackers) {
					val npc = (NPC) e;
					if (npc.getId() == 2267 || npc.getId() == 2265) {
						if (npc.getTargetType() == EntityType.PLAYER) {
							for (val p : npc.getPossibleTargets(EntityType.PLAYER)) {
								val play = (Player) p;
								if (play.getPlayerInformation().getUserIdentifier() == player.getPlayerInformation().getUserIdentifier()) {
									count++;
									break;
								}
							}
						}
					}
				}
				if (count == 2) {
					player.putBooleanAttribute("elite-combat-achievement29", true);
					EliteTasks.sendEliteCompletion(player, 29);
				}
			}
			if (!player.getBooleanAttribute("elite-combat-achievement30") && (id == NpcId.DAGANNOTH_REX || (id == NpcId.DAGANNOTH_PRIME || id == NpcId.DAGANNOTH_SUPREME))) {
				if (player.getAttributes().get("time_rex_kill") instanceof Long && player.getAttributes().get("time_other_dks_kill") instanceof Long) {
					if ((long) player.getAttributes().get("time_rex_kill") + 500L >= (long) player.getAttributes().get("time_other_dks_kill")
							|| (long) player.getAttributes().get("time_rex_kill") - 500L <= (long) player.getAttributes().get("time_other_dks_kill")) {
						player.putBooleanAttribute("elite-combat-achievement30", true);
						EliteTasks.sendEliteCompletion(player, 30);
					}
				}
			}
			if (!player.getBooleanAttribute("elite-combat-achievement31") && id == NpcId.DAGANNOTH_REX) {
				val attackers = player.getPossibleTargets(EntityType.NPC);
				int count = 0;
				for (val e : attackers) {
					val npc = (NPC) e;
					if (npc.getId() == 2266 || npc.getId() == 2265) {
						if (npc.getTargetType() == EntityType.PLAYER) {
							for (val p : npc.getPossibleTargets(EntityType.PLAYER)) {
								val play = (Player) p;
								if (play.getPlayerInformation().getUserIdentifier() == player.getPlayerInformation().getUserIdentifier()) {
									count++;
									break;
								}
							}
						}
					}
				}
				if (count == 2) {
					player.putBooleanAttribute("elite-combat-achievement31", true);
					EliteTasks.sendEliteCompletion(player, 31);
				}
			}
			if (!player.getBooleanAttribute("elite-combat-achievement32") && (id == NpcId.DAGANNOTH_REX || id == NpcId.DAGANNOTH_PRIME || id == NpcId.DAGANNOTH_SUPREME)) {
				if (player.getAttributes().get("time_rex_kill") instanceof Long
						&& player.getAttributes().get("time_prime_kill") instanceof Long
						&& player.getAttributes().get("time_supreme_kill") instanceof Long) {
					if (Math.min(Math.min((long) player.getAttributes().get("time_rex_kill"), (long) player.getAttributes().get("time_prime_kill")), (long) player.getAttributes().get("time_supreme_kill")) + 9000L >= RuneDate.currentTimeMillis()) {
						player.putBooleanAttribute("elite-combat-achievement32", true);
						EliteTasks.sendEliteCompletion(player, 32);
					}
				}
			}
			if (!player.getBooleanAttribute("elite-combat-achievement33") && id == NpcId.DAGANNOTH_SUPREME) {
				val attackers = player.getPossibleTargets(EntityType.NPC);
				int count = 0;
				for (val e : attackers) {
					val npc = (NPC) e;
					if (npc.getId() == 2266 || npc.getId() == 2267) {
						if (npc.getTargetType() == EntityType.PLAYER) {
							for (val p : npc.getPossibleTargets(EntityType.PLAYER)) {
								val play = (Player) p;
								if (play.getPlayerInformation().getUserIdentifier() == player.getPlayerInformation().getUserIdentifier()) {
									count++;
									break;
								}
							}
						}
					}
				}
				if (count == 2) {
					player.putBooleanAttribute("elite-combat-achievement33", true);
					EliteTasks.sendEliteCompletion(player, 33);
				}
			}
		}
	}
	
	@Override
	public boolean isTolerable() {
		return false;
	}
	
	@Override
	public boolean isEntityClipped() {
		return false;
	}

	@Override
	public boolean validate(final int id, final String name) {
		return id >= NpcId.DAGANNOTH_SUPREME && id <= NpcId.DAGANNOTH_REX;
	}

    private static final Projectile magicProj = new Projectile(162, 63, 25, 27, 15, 33, 64, 5);
    private static final Projectile rangedProj = new Projectile(475, 50, 30, 25, 30, 28, 5, 5);

    @Override
    public int attack(final Entity target) {
        val npc = this;
        if (getId() == 2266) {
            npc.setAnimation(npc.getCombatDefinitions().getAttackAnim());
            delayHit(npc, World.sendProjectile(npc, target, magicProj), target, new Hit(npc, getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), MAGIC, target), HitType.MAGIC));
        } else if (getId() == 2267) {
            npc.setAnimation(npc.getCombatDefinitions().getAttackAnim());
            delayHit(npc, 0, target, new Hit(npc, getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), MELEE, target), HitType.MELEE));
        } else {
            npc.setAnimation(npc.getCombatDefinitions().getAttackAnim());
            delayHit(npc, World.sendProjectile(npc, target, rangedProj), target, new Hit(npc, getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit(), RANGED, target), HitType.RANGED));
        }
        return npc.getCombatDefinitions().getAttackSpeed();
    }
}
