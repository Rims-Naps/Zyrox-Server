package com.zenyte.game.world.entity.player.action.combat;

import com.zenyte.cores.WorldThread;
import com.zenyte.game.RuneDate;
import com.zenyte.game.content.chambersofxeric.npc.Tekton;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.tasks.TickTask;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Entity.EntityType;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.Toxins.ToxinType;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.combatdefs.AttackType;
import com.zenyte.game.world.entity.npc.combatdefs.NPCCDLoader;
import com.zenyte.game.world.entity.npc.combatdefs.StatType;
import com.zenyte.game.world.entity.npc.race.Demon;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.entity.player.variables.TickVariable;
import lombok.Getter;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

import static com.zenyte.game.world.entity.player.action.combat.SpecialAttackScript.*;
import static com.zenyte.game.world.entity.player.action.combat.SpecialType.*;

/**
 * @author Kris | 1. jaan 2018 : 23:03.01
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public enum SpecialAttack {

	QUICK_SMASH(AttackType.CRUSH, new int[] { 4153, 12848, 20557 }, 0, MELEE, new Animation(1667), new Graphics(340, 0,
            96), (player, combat, target) -> {
		combat.delayHit(0, combat.getHit(player, target, 1, 1, 1, false));
		player.sendSound(QUICKSMASH_SOUND);
	}),
    CLEAVE(AttackType.SLASH, 1305, WEAPON_SPEED, MELEE, new Animation(1058), new Graphics(248, 0, 100), (player, combat, target) -> {
        combat.delayHit(0, combat.getHit(player, target, 1, 1.15, 1, false));
        player.sendSound(CLEAVE_SOUND);
    }),
    DESCENT_OF_DARKNESS(AttackType.RANGED, new int[]{11235, 12765, 12766, 12767, 12768, 20408}, 8, RANGED,
            new Animation(426), new Graphics(1112, 0, 95), (player, combat, target) -> {
        final int ammo = player.getEquipment().getId(EquipmentSlot.AMMUNITION.getSlot());
        final boolean dragons = ammo == 11212 || ammo == 11227 || ammo == 11228 || ammo == 11229 || ammo == 11237;
        final int firstDelay = DESCENT_OF_DARKNESS_FIRST_PROJ.getTime(player.getLocation(), target.getMiddleLocation());
        final int secondDelay = DESCENT_OF_DARKNESS_SECOND_PROJ.getTime(player.getLocation(), target.getMiddleLocation());
        if (combat instanceof RangedCombat) {
            val ranged = (RangedCombat) combat;
            ranged.dropAmmunition(firstDelay, false);
            ranged.dropAmmunition(secondDelay, false);
        }
        target.setGraphics(new Graphics(1100, 30 * firstDelay, 95));
        player.sendSound(DARK_BOW_DRAGON_LOCAL_FIRST_SOUND);
        player.sendSound(DARK_BOW_DRAGON_LOCAL_SECOND_SOUND);
        if (dragons) {
            World.sendProjectile(player, target, DESCENT_OF_DRAGONS_FIRST_PROJ);
            World.sendProjectile(player, target, DESCENT_OF_DRAGONS_SECOND_PROJ);
            val firstClientTicks = DESCENT_OF_DRAGONS_FIRST_PROJ.getProjectileDuration(player.getLocation(),
                    target.getMiddleLocation());
            val secondClientTicks = DESCENT_OF_DRAGONS_SECOND_PROJ.getProjectileDuration(player.getLocation(),
                    target.getMiddleLocation());
            World.sendSoundEffect(target, new SoundEffect(DESCENT_OF_DRAGONS_SOUND.getId(),
                    DESCENT_OF_DRAGONS_SOUND.getRadius(), firstClientTicks));
            World.sendSoundEffect(target, new SoundEffect(DESCENT_OF_DRAGONS_SOUND.getId(),
                    DESCENT_OF_DRAGONS_SOUND.getRadius(), secondClientTicks));
            final int max = Math.min(combat.getMaxHit(player, 1.5, 1, false), 48);
            val firstHit = Math.max(8, combat.getRandomHit(player, target, max, 1));
            val secondHit = Math.max(8, combat.getRandomHit(player, target, max, 1));
            combat.delayHit(firstDelay, new Hit(player, firstHit, HitType.RANGED));
            combat.delayHit(secondDelay, new Hit(player, secondHit, HitType.RANGED));
            return;
        }
        World.sendProjectile(player, target, DESCENT_OF_DARKNESS_FIRST_PROJ);
        World.sendProjectile(player, target, DESCENT_OF_DARKNESS_SECOND_PROJ);
        val firstClientTicks = DESCENT_OF_DARKNESS_FIRST_PROJ.getProjectileDuration(player.getLocation(),
                target.getMiddleLocation());
        val secondClientTicks = DESCENT_OF_DARKNESS_SECOND_PROJ.getProjectileDuration(player.getLocation(),
                target.getMiddleLocation());
        World.sendSoundEffect(target, new SoundEffect(DESCENT_OF_DRAGONS_SOUND.getId(),
                DESCENT_OF_DRAGONS_SOUND.getRadius(), firstClientTicks));
        World.sendSoundEffect(target, new SoundEffect(DESCENT_OF_DRAGONS_SOUND.getId(),
                DESCENT_OF_DRAGONS_SOUND.getRadius(), secondClientTicks));
        final int max = Math.min(combat.getMaxHit(player, 1.3, 1, false), 48);
        val firstHit = Math.max(5, combat.getRandomHit(player, target, max, 1));
        val secondHit = Math.max(5, combat.getRandomHit(player, target, max, 1));
        combat.delayHit(firstDelay, new Hit(player, firstHit, HitType.RANGED));
        combat.delayHit(secondDelay, new Hit(player, secondHit, HitType.RANGED));
    }),

    DUALITY(AttackType.RANGED, new int[] {22804, 22806, 22808, 22810, 22812, 22814}, WEAPON_SPEED, RANGED, null, null, (player, combat, target) -> {
        val weapon = player.getWeapon().getId();
        val poisonous = weapon == 22806 || weapon == 22808 || weapon == 22810;
        player.setAnimation(poisonous ? DUALITY_POISONOUS_ANIM : DUALITY_REGULAR_ANIM);
        player.sendSound(DUALITY_SOUND);
        val projectile = poisonous ? DUALITY_POISONOUS_PROJ : DUALITY_REGULAR_PROJ;
        val delay = World.sendProjectile(player, target, projectile);
        combat.delayHit(delay,
                combat.getHit(player, target, 1, 1, 1, false),
                combat.getHit(player, target, 1, 1, 1, false));

    }),

	PUNCTURE(AttackType.SLASH, new int[] { 1215, 1231, 5680, 5698, 20407 }, WEAPON_SPEED, MELEE, new Animation(1062),
            new Graphics(252, 0,
			100), (player, combat, target) -> {
	            player.sendSound(PUNCTURE_SOUND);
				if (target.getEntityType() == EntityType.PLAYER) {
					combat.delayHit(0, combat.getHit(player, target, 1.15, 1.15, 1, false),
                            combat.getHit(player, target, 1.15, 1.15, 1, false));
				} else {
					combat.delayHit(0, combat.getHit(player, target, 1.15, 1.15, 1, false));
					combat.delayHit(1, combat.getHit(player, target, 1.15, 1.15, 1, false));
				}
			}),
	SEVER(AttackType.SLASH, new int[] { 4587, 20000, 20406 }, WEAPON_SPEED, MELEE, new Animation(1872), new Graphics(347, 0, 100), (player, combat, target) -> {
		val hit = combat.getHit(player, target, 1, 1.25F, 1, false);
	    combat.delayHit(0, hit);
		player.sendSound(SEVER_SOUND);
		if (target.getEntityType() == EntityType.PLAYER && hit.getDamage() > 0) {
			val p2 = (Player) target;
			val prayers = p2.getPrayerManager();
			if (prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) {
			    prayers.deactivatePrayer(Prayer.PROTECT_FROM_MAGIC);
            }
			if (prayers.isActive(Prayer.PROTECT_FROM_MISSILES)) {
			    prayers.deactivatePrayer(Prayer.PROTECT_FROM_MISSILES);
            }
			if (prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
			    prayers.deactivatePrayer(Prayer.PROTECT_FROM_MELEE);
            }
			p2.getTemporaryAttributes().put("SeverEffect", Utils.currentTimeMillis() + 5000);
		}
	}),
    SHATTER(AttackType.SLASH, 1434, WEAPON_SPEED, MELEE, new Animation(1060), new Graphics(251, 0, 100), (player, combat, target) -> {
	    player.sendSound(SHATTER_SOUND);
        combat.delayHit(0, combat.getHit(player, target, 1.25, 1.5, 1, false));
    }),
	SMASH(AttackType.CRUSH, new int[] { 13576, 20785 }, WEAPON_SPEED, MELEE, new Animation(1378), new Graphics(1292), (player,
                                                                                                        combat, target) -> {
		final Hit hit = combat.getHit(player, target, 1.75, 1.5, 1, false);
		combat.delayHit(0, hit);
		final boolean tekton = target instanceof Tekton;
        if (!tekton && hit.getDamage() == 0) {
            return;
        }
		target.drainSkill(Skills.DEFENCE, hit.getDamage() == 0 ? 5d : 30d);
	}),
	SNAPSHOT(AttackType.RANGED, new int[] { 861, 20558, 12788 }, 2, RANGED, new Animation(1074), new Graphics(250, 25,
            95), (player, combat, target) -> {
		World.sendProjectile(player, target, SNAPSHOT_FIRST_PROJ);
		World.sendProjectile(player, target, SNAPSHOT_SECOND_PROJ);
		player.sendSound(SNAPSHOT_SOUND);
		combat.delayHit(SNAPSHOT_SECOND_PROJ.getTime(player.getLocation(), target.getLocation()), combat.getHit(player, target, 0.8, 1, 1, false),
				combat.getHit(player, target, 0.8, 1, 1, false));
	}),
	THE_JUDGEMENT(AttackType.SLASH, new int[] { 11802, 20593, 20368 }, WEAPON_SPEED, MELEE, null, new Graphics(1211), (player, combat, target) -> {
		final int weaponId = player.getWeapon() == null ? -1 : player.getWeapon().getId();
		player.setAnimation(weaponId == 20368 ? ORNAMENT_JUDGEMENT_ANIM : JUDGEMENT_ANIM);
		combat.delayHit(0, combat.getHit(player, target, 2, 1.1F, 1.25F, false));
		World.sendSoundEffect(player, THE_JUDGEMENT_SOUND);
	}),
	HEALING_BLADE(AttackType.SLASH, new int[] { 11806, 20372 }, WEAPON_SPEED, MELEE, null, new Graphics(1209), (player, combat, target) -> {
		final int weaponId = player.getWeapon() == null ? -1 : player.getWeapon().getId();
		player.setAnimation(weaponId == 11806 ? HEALING_BLADE_ANIM : ORNAMENT_HEALING_BLADE_ANIM);
		final Hit hit = combat.getHit(player, target, 2, 1.1, 1, false);
		World.sendSoundEffect(player, HEALING_BLADE_SOUND);
		combat.delayHit(0, hit);
		int prayer = (int) (hit.getDamage() * 0.25);
		int hitpoints = (int) (hit.getDamage() * 0.5);
		if (hit.getDamage() <= 22) {
			hitpoints = 10;
			prayer = 5;
		}
		player.heal(hitpoints);
		player.getPrayerManager().restorePrayerPoints(prayer);
		if (player.inArea("Fight caves")) {
			player.putBooleanAttribute("drainedPrayerInFightCaves", true);
		}
	}),
	PENANCE(AttackType.CRUSH, 13263, WEAPON_SPEED, MELEE, new Animation(7010), null, (player, combat, target) -> {
		final int max = player.getSkills().getLevelForXp(Skills.PRAYER);
		final int points = player.getPrayerManager().getPrayerPoints();
		final int missing = (points > max) ? 0 : max - points;
		final int maxHit = combat.getMaxHit(player, 1, 1, false);
		final float strengthModifier = (missing == 0 ? 1 : (1 + (missing * 0.005f)));
		final int extra = (int) ((strengthModifier * maxHit) - maxHit);
		World.sendSoundEffect(player, PENANCE_SWORD_SOUND);
		World.sendSoundEffect(player, PENANCE_SPECIAL_SOUND);
		World.sendGraphics(PENANCE_GFX, new Location(target.getLocation()));
		combat.delayHit(target, 0, new Hit(player, combat.getRandomHit(player, target, maxHit + extra, 1), HitType.MELEE));
	}),
	SLICE_AND_DICE(AttackType.SLASH, new int[] { 13652, 20784 }, WEAPON_SPEED, MELEE, new Animation(7514, 10), new Graphics(1171, 10, 0), (player, combat, target) -> {
	    val maxHit = combat.getMaxHit(player, 1, 1, false) - 1;
		int hit = 0;
		int probability = 0;
		for (int i = 0; i < 4 && hit == 0; i++) {
			val probableHit = combat.getRandomHit(player, target, maxHit, 1);
			if (probableHit > hit) {
				hit = probableHit;
				probability = i;
			}
        }
		World.sendSoundEffect(player, SLICE_AND_DICE_SOUND);
		val boost = Utils.random(1);
		if (hit == 0) {
			combat.delayHit(0, new Hit(player, 0, HitType.MISSED), new Hit(player, 0, HitType.MISSED));
			combat.delayHit(1, new Hit(player, boost, HitType.MELEE), new Hit(player, boost, HitType.MELEE));
			return;
		}
		int firstHit = 0;
		int secondHit = 0;
		int thirdHit = 0;
		int fourthHit;
		switch (probability) {
			case 0:
				firstHit = Math.max(Math.round(maxHit * 0.5F), combat.getRandomHit(player, target, maxHit - 1, 1));
				secondHit = firstHit / 2;
				thirdHit = secondHit / 2;

				combat.delayHit(0, new Hit(player, firstHit, HitType.MELEE), new Hit(player, secondHit, HitType.MELEE));
				combat.delayHit(1, new Hit(player, thirdHit, HitType.MELEE), new Hit(player, thirdHit + boost, HitType.MELEE));
				return;
			case 1:
				secondHit = Math.max(Math.round(maxHit * 0.375F), combat.getRandomHit(player, target, Math.round(maxHit * 0.875F), 1));
				thirdHit = secondHit / 2;

				combat.delayHit(0, new Hit(player, firstHit, HitType.MELEE), new Hit(player, secondHit, HitType.MELEE));
				combat.delayHit(1, new Hit(player, thirdHit, HitType.MELEE), new Hit(player, thirdHit + boost, HitType.MELEE));
				return;
			case 2:
				thirdHit = Math.max(Math.round(maxHit * 0.25F), combat.getRandomHit(player, target, Math.round(maxHit * 0.75F), 1));

				combat.delayHit(0, new Hit(player, firstHit, HitType.MELEE), new Hit(player, secondHit, HitType.MELEE));
				combat.delayHit(1, new Hit(player, thirdHit, HitType.MELEE), new Hit(player, thirdHit + boost, HitType.MELEE));
				return;
			case 3:
				fourthHit = Math.max(Math.round(maxHit * 0.25F), combat.getRandomHit(player, target, Math.round(maxHit * 1.25F), 1));

				combat.delayHit(0, new Hit(player, firstHit, HitType.MELEE), new Hit(player, secondHit, HitType.MELEE));
				combat.delayHit(1, new Hit(player, thirdHit, HitType.MELEE), new Hit(player, fourthHit, HitType.MELEE));
				return;
			default:
				return;
        }
	}),
	RAMPAGE(AttackType.SLASH, 1377, WEAPON_SPEED, MELEE, new Animation(1056), new Graphics(246), (player, combat, target) -> {
		final int attack = player.getSkills().drainSkill(Skills.ATTACK, 10d, 0);
		final int defence = player.getSkills().drainSkill(Skills.DEFENCE, 10d, 0);
		final int magic = player.getSkills().drainSkill(Skills.MAGIC, 10d, 0);
		final int ranged = player.getSkills().drainSkill(Skills.RANGED, 10d, 0);
		final int total = (attack + defence + magic + ranged) / 4;
		final int strength = player.getSkills().getLevel(Skills.STRENGTH);
		final int trueStrength = player.getSkills().getLevelForXp(Skills.STRENGTH);
		final int level = strength > trueStrength ? trueStrength : strength;
		player.sendSound(RAMPAGE_SOUND);
		player.getSkills().setLevel(Skills.STRENGTH, level + 10 + total);
		player.setForceTalk(RAMPAGE_FORCETALK);
	}),
	SHOVE(AttackType.SLASH, new int[] { 1249, 1263, 3176, 5716, 5730, 11824, 11889 }, WEAPON_SPEED, MELEE, new Animation(1064), new Graphics(253, 0,
			96), (player, combat, target) -> {
	    player.sendSound(SHOVE_SOUND);

				if (target.getSize() == 1) {
					int x = target.getX(), y = target.getY();
					int px = player.getX();
					int py = player.getY();
					if (target instanceof Player) {
					    val tp = (Player) target;
                        val lastShoveTick = tp.getNumericTemporaryAttribute("Last shove push").longValue();
                        if (WorldThread.WORLD_CYCLE < lastShoveTick) {
                            return;
                        }
                        tp.getTemporaryAttributes().put("Last shove push", WorldThread.WORLD_CYCLE + 6);
                    }

					if (px > target.getX()) {
						x--;
					} else if (px < target.getX()) {
						x++;
					}
					if (py > target.getY()) {
						y--;
					} else if (py < target.getY()) {
						y++;
					}
					player.getActionManager().forceStop();
					player.setFaceLocation(target.getLocation());
					target.getWalkSteps().clear();
					target.setAnimation(null);
					target.performDefenceAnimation(player);
					target.addWalkSteps(x, y, 1, true);
					target.lock(5);
					target.setGraphics(SHOVE_GFX);
				}
			}),
	ABYSSAL_PUNCTURE(AttackType.STAB, new int[] { 13265, 13267, 13269, 13271 }, WEAPON_SPEED, MELEE, new Animation(1062),
            new Graphics(
			1283), (player, combat, target) -> {
	            player.sendSound(ABYSSAL_PUNCTURE_SOUND);
				final Hit hit = combat.getHit(player, target, 1.25, 0.85, 1, false);
				final int damage = combat.getMaxHit(player, 0.85, 1, false);
				if (target.getEntityType() == EntityType.PLAYER) {
					combat.delayHit(0, hit, hit.getDamage() == 0 ? new Hit(player, 0, HitType.MELEE)
							: new Hit(player, Utils.random(1, damage), HitType.MELEE));
				} else {
					combat.delayHit(0, hit);
					combat.delayHit(1, hit.getDamage() == 0 ? new Hit(player, 0, HitType.MELEE)
							: new Hit(player, Utils.random(1, damage), HitType.MELEE));
				}
			}),
	BINDING_TENTACLE(AttackType.SLASH, 12006, WEAPON_SPEED, MELEE, new Animation(1658), null, (player, combat, target) -> {
	    combat.delayHit(0, combat.getHit(player, target, 1, 1, 1, false));
	    player.sendSound(BINDING_TENTACLE_SOUND);
		WorldTasksManager.schedule(() -> {
			if (Utils.random(1) == 0) {
				target.getToxins().applyToxin(ToxinType.POISON, 4);
			}
			target.freeze(8);
			target.setGraphics(WHIP_GFX);
		});
	}),
	ENERGY_DRAIN(AttackType.SLASH, new int[] { 4151, 20405, 12773, 12774 }, WEAPON_SPEED, MELEE, new Animation(1658), null, (player, combat, target) -> {
        combat.delayHit(0, combat.getHit(player, target, 1.25, 1, 1, false));
		WorldTasksManager.schedule(() -> {
			if (target.getEntityType() == EntityType.PLAYER) {
				final Player p2 = (Player) target;
				final int targetEnergy = (int) p2.getVariables().getRunEnergy();
				final int siphon = (int) (targetEnergy * 0.10);
				p2.getVariables().setRunEnergy(p2.getVariables().getRunEnergy() - siphon);
				player.getVariables().setRunEnergy(
						(player.getVariables().getRunEnergy() + siphon) > 100 ? 100 : (player.getVariables().getRunEnergy() + siphon));
			}
			target.setGraphics(WHIP_GFX);
		});
	}),
	ARMADYL_EYE(AttackType.RANGED, 11785, 5, RANGED, new Animation(4230), null, (player, combat, target) -> {
		World.sendProjectile(player, target, ARMADYL_EYE_PROJ);
		player.sendSound(ARMADYL_EYE_SOUND);
		combat.delayHit(ARMADYL_EYE_PROJ.getTime(player.getLocation(), target.getLocation()), combat.getHit(player, target, 2, 1, 1, false));
	}),
	CHAINHIT(AttackType.RANGED, 805, 4, RANGED, new Animation(1068), new Graphics(257, 0, 90),
            (player, combat, target) -> {
		final List<Entity> possibleTargets = player.getPossibleTargets(EntityType.BOTH);
		possibleTargets.remove(target);
		possibleTargets.removeIf(e -> e instanceof Player && !player.canHit((Player) e));
		World.sendProjectile(player, target, CHAINHIT_PROJ);
		val delay = CHAINHIT_PROJ.getTime(player.getLocation(), target.getLocation());
        if (combat instanceof RangedCombat) {
            val ranged = (RangedCombat) combat;
            ranged.dropAmmunition(delay, false);
        }
		final int maxhit = combat.getMaxHit(player, 1, 1, false);
		combat.delayHit(delay,
				new Hit(player, combat.getRandomHit(player, target, maxhit, 1), HitType.RANGED));
		player.sendSound(THROWNAXE_SOUND);
		if (player.isMultiArea()) {
			WorldTasksManager.scheduleOrExecute(new WorldTask() {
				private int count = 0;
				private Entity t;
				private int delay;

				@Override
				public void run() {
					if (count == 4 || possibleTargets.isEmpty() || player.getCombatDefinitions().getSpecialEnergy() < 10) {
						stop();
						return;
					}
					count++;
					final Entity previousTarget = t == null ? target : t;
					for (int i = 0; i < possibleTargets.size(); i++) {
						t = possibleTargets.get(i);
						if (t.getLocation().getDistance(previousTarget.getLocation()) <= 3 || !t.isMultiArea()) {
							break;
						}
						if (i == possibleTargets.size()) {
							stop();
							return;
						}
					}
					delay = CHAINHIT_CHAIN_PROJ.getTime(previousTarget.getLocation(), t.getLocation());
					possibleTargets.remove(t);
					if (t.getEntityType() == EntityType.NPC && previousTarget.getEntityType() == EntityType.NPC) {
						val npc1 = (NPC) previousTarget;
 						val npc2 = (NPC) t;
						if (npc2.getId() == 2266 && npc1.getId() == 2267) {
							player.getAttributes().put("time_when_rune_thrown_axe_was_shot_at_prime_via_rex", RuneDate.currentTimeMillis());
						}
					}
					player.getCombatDefinitions().setSpecialEnergy(player.getCombatDefinitions().getSpecialEnergy() - 10);
					World.sendProjectile(previousTarget, t, CHAINHIT_CHAIN_PROJ);
					combat.delayHit(t, (delay < 0 ? (delay + 1) : delay),
							new Hit(player, combat.getRandomHit(player, target, maxhit, 1), HitType.RANGED));
				}

			}, CHAINHIT_PROJ.getTime(player.getLocation(), target.getLocation()) - 1, 0);
		}
	}),
	SWEEP(AttackType.SLASH, new int[] { 3204, 13080, 13081, 13082, 13083, 13084, 13085, 13086, 13087, 13088, 13089, 13090, 13091, 13092, 13093, 13094, 13095,
			13096, 13097, 13098, 13099, 13100, 13101 }, WEAPON_SPEED, MELEE, new Animation(1203), null, (player, combat, target) -> {
				final int weaponId = player.getWeapon() == null ? -1 : player.getWeapon().getId();
				final Location center = target.getMiddleLocation();
				final int x = target.getSize() == 1 ? target.getX() : (int) ((center.getX() + player.getX()) / 2f);
				final int y = target.getSize() == 1 ? target.getY() : (int) ((center.getY() + player.getY()) / 2f);
				player.sendSound(SWEEP_SOUND);
				Graphics gfx = weaponId == 3204 ? SWEEP_DRAGON_SOUTH_GFX : SWEEP_CRYSTAL_SOUTH_GFX;
				if (x > player.getX()) {
					gfx = weaponId == 3204 ? SWEEP_DRAGON_EAST_GFX : SWEEP_CRYSTAL_EAST_GFX;
				} else if (x < player.getX()) {
					gfx = weaponId == 3204 ? SWEEP_DRAGON_WEST_GFX : SWEEP_CRYSTAL_WEST_GFX;
				} else if (y < player.getY()) {
					gfx = weaponId == 3204 ? SWEEP_DRAGON_NORTH_GFX : SWEEP_CRYSTAL_NORTH_GFX;
				}
				final Location tile = new Location(x, y, player.getPlane());
				World.sendGraphics(gfx, tile);
				final Hit primaryHit = combat.getHit(player, target, 1, 1.1, 1, false);
				combat.delayHit(target, 0, primaryHit);
				if (target.getSize() > 1) {
					final Hit secondaryHit = combat.getHit(player, target, 0.75, 1.1, 1, false);
					combat.delayHit(target, 0, secondaryHit);
				}
				if (target.getSize() == 1) {
					final List<Entity> possibleTargets = player.getPossibleTargets(EntityType.BOTH);
					possibleTargets.remove(target);
					possibleTargets.removeIf(e -> e instanceof Player && !player.canHit((Player) e));
					for (final Entity e : possibleTargets) {
						if (e == null || !e.isMultiArea()) {
							continue;
						}
						if (e.getSize() == 1 && e.getLocation().withinDistance(tile, 1)) {
							final Hit hit = combat.getHit(player, e, 1, 1.1, 1, false);
							combat.delayHit(e, 0, hit);
						}
					}
				}
			}),
	POWERSTAB(AttackType.SLASH, new int[] { 7158, 20559 }, WEAPON_SPEED, MELEE, new Animation(3157), new Graphics(1214), (player,
                                                                                                           combat, target) -> {
		final List<Entity> possibleTargets = player.getPossibleTargets(EntityType.BOTH);
		final List<Entity> toRemove = new ArrayList<>();
		for (final Entity e : possibleTargets) {
			if (!e.getLocation().withinDistance(player.getLocation(), 1) || (e instanceof Player && !player.canHit((Player) e))) {
				toRemove.add(e);
			}
		}
		player.sendSound(POWERSTAB_SOUND);
		possibleTargets.remove(target);
		possibleTargets.removeAll(toRemove);
		combat.delayHit(target, 0, combat.getHit(player, target, 1, 1, 1, false));
		int count = 0;
		if (player.isMultiArea()) {
			for (final Entity e : possibleTargets) {
				if (!e.isMultiArea()) {
					continue;
				}
				combat.delayHit(e, 0, combat.getHit(player, e, 1, 1, 1, false));
				if (++count == 14) {
					break;
				}
			}
		}
	}),
	ROCK_KNOCKER(AttackType.SLASH, new int[] { 11920, 12797, 13243, 20014, 13244 }, 0, MELEE, null, null, (player,
                                                                                                           combat, target) -> {
		final int weaponId = player.getWeapon() == null ? -1 : player.getWeapon().getId();
		if (weaponId == 11920 || weaponId == 12797 || weaponId == 20014) {
			player.setAnimation(ROCK_KNOCKER_ANIM);
		} else if (weaponId == 13243) {
			player.setAnimation(ROCK_KNOCKER_INFERNAL_ANIM);
		}
		player.setForceTalk(ROCK_KNOCKER_FORCETALK);
        World.sendSoundEffect(player, ROCK_KNOCKER_SOUND);
		final int maxLevel = player.getSkills().getLevelForXp(Skills.MINING);
		final int currentLevel = player.getSkills().getLevel(Skills.MINING);
		final int level = currentLevel > maxLevel ? maxLevel : currentLevel;
		player.getSkills().setLevel(Skills.MINING, level + 3);
	}),
	LUMBER_UP(AttackType.SLASH, new int[] { 6739, 13241, 20011, 13242}, 0, MELEE, new Animation(2876),
            new Graphics(479), (player, combat, target) -> {
		player.setForceTalk(LUMBER_UP_FORCETALK);
		final int maxLevel = player.getSkills().getLevelForXp(Skills.WOODCUTTING);
		final int currentLevel = player.getSkills().getLevel(Skills.WOODCUTTING);
		final int level = currentLevel > maxLevel ? maxLevel : currentLevel;
		World.sendSoundEffect(player, LUMBER_UP_SOUND);
		player.getSkills().setLevel(Skills.WOODCUTTING, level + 3);
	}),
	FISHSTABBER(AttackType.STAB, new int[] { 21028, 21031, 21033 }, 0, MELEE, null, new Graphics(246), (player, combat,
                                                                                                     target) -> {
		final int weaponId = player.getWeapon() == null ? -1 : player.getWeapon().getId();
		if (weaponId == 21028) {
			player.setAnimation(FISHSTABBER_DRAGON_ANIM);
		} else {
			player.setAnimation(FISHSTABBER_INFERNAL_ANIM);
		}
		player.setForceTalk(FISHSTABBER_FORCETALK);
		final int maxLevel = player.getSkills().getLevelForXp(Skills.FISHING);
		final int currentLevel = player.getSkills().getLevel(Skills.FISHING);
		final int level = currentLevel > maxLevel ? maxLevel : currentLevel;
		player.getSkills().setLevel(Skills.FISHING, level + 3);
	}),
	MOMENTUM_THROW(AttackType.RANGED, new int[] { 20849, 21207 }, 4, RANGED, new Animation(7521), new Graphics(1317, 0,
            96), (player, combat, target) -> {
		World.sendProjectile(player, target, MOMENTUM_THROW_PROJ);
		final int delay = MOMENTUM_THROW_PROJ.getTime(player.getLocation(), target.getLocation());
		combat.delayHit(delay, combat.getHit(player, target, 1.25, 1, 1, false));
        player.sendSound(THROWNAXE_SOUND);
        if (combat instanceof RangedCombat) {
            val ranged = (RangedCombat) combat;
            ranged.dropAmmunition(delay, true);
        }
	}),
	WILD_STAB(AttackType.STAB, new int[] { 21009, 21206 }, WEAPON_SPEED, MELEE, new Animation(7515), new Graphics(1369, 0, 96),
            (player, combat, target) -> {
		final Hit hit = combat.getHit(player, target, 1.25, 1.25, 1, true);
		player.sendSound(WILD_STAB_SOUND);
		combat.delayHit(0, hit);
	}),
	UNLEASH(AttackType.STAB, new int[]{ItemId.DRAGON_HASTA, ItemId.DRAGON_HASTAKP, ItemId.DRAGON_HASTAP, ItemId.DRAGON_HASTAP_22737, ItemId.DRAGON_HASTAP_22740},
			WEAPON_SPEED, MELEE, new Animation(7515), new Graphics(1369, 0, 96),
			(player, combat, target) -> {
			val energy = player.getCombatDefinitions().getSpecialEnergy();
			val intervals = energy / 5;
			player.getCombatDefinitions().setSpecialEnergy(0);
			final Hit hit = combat.getHit(player, target, 1 + (intervals * 0.05), 1 + (intervals * 0.025), 1, false);
			player.sendSound(WILD_STAB_SOUND);
			combat.delayHit(0, hit);
	}),
	WARSTRIKE(AttackType.SLASH, new int[] { 11804, 20370, 20782, 21060 }, WEAPON_SPEED, MELEE, null, new Graphics(1212), (player, combat, target) -> {
		final int weaponId = player.getWeapon() == null ? -1 : player.getWeapon().getId();
		player.setAnimation(weaponId == 20370 ? ORNAMENT_WARSTRIKE_ANIM : WARSTRIKE_ANIM);
		final Hit hit = combat.getHit(player, target, 2, 1.1, 1.1, false);
		combat.delayHit(0, hit);
		World.sendSoundEffect(player, WARSTRIKE_SOUND);
        final boolean tekton = target instanceof Tekton;
		int damage = Math.max(tekton ? 10 : 0, hit.getDamage());
		damage -= target.drainSkill(Skills.DEFENCE, damage);
		damage -= target.drainSkill(Skills.STRENGTH, damage);
		damage -= target.drainSkill(Skills.PRAYER, damage);
		damage -= target.drainSkill(Skills.ATTACK, damage);
		damage -= target.drainSkill(Skills.MAGIC, damage);
		target.drainSkill(Skills.RANGED, damage);
	}),
	ICE_CLEAVE(AttackType.SLASH, new int[] { 11808, 20374 }, WEAPON_SPEED, MELEE, null, new Graphics(1210), (player, combat, target) -> {
		final int weaponId = player.getWeapon() == null ? -1 : player.getWeapon().getId();
		player.setAnimation(weaponId == 11808 ? ICE_CLEAVE_ANIM : ORNAMENT_ICE_CLEAVE_ANIM);
		final Hit hit = combat.getHit(player, target, 2, 1.1, 1, false);
		World.sendSoundEffect(player, ICE_CLEAVE_SOUND);
		combat.delayHit(0, hit);
		if (hit.getDamage() > 0) {
			target.resetWalkSteps();
			target.freezeWithNotification(32);
			target.setGraphics(ICE_CLEAVE_GFX);
		}
	}),
	SUNDER(AttackType.CRUSH, 10887, WEAPON_SPEED, MELEE, new Animation(5870), new Graphics(1027), (player, combat, target) -> {
		final Hit hit = combat.getHit(player, target, 2, 1.1, 1, false);
		combat.delayHit(0, hit);
		final int damage = hit.getDamage();
		final int random = Utils.random(3);
		World.sendSoundEffect(player, SUNDER_SOUND);
		target.drainSkill(random < 2 ? random : random == 2 ? Skills.MAGIC : Skills.RANGED, (int) (damage * 0.1));
	}),
	HAMMER_BLOW(AttackType.CRUSH, 21742, WEAPON_SPEED, MELEE, new Animation(1378), new Graphics(1450),
            (player, combat, target) -> {
		final Hit hit = combat.getHit(player, target, 1.5, 1, 1, false);
		hit.setDamage(hit.getDamage() + 5);
		combat.delayHit(0, hit);
		player.sendSound(HAMMER_BLOW_SOUND);
	}),
    IMPALE(AttackType.SLASH, 3101, WEAPON_SPEED, MELEE, new Animation(923), new Graphics(274, 0, 96), (player, combat, target) -> {
        combat.delayHit(0, combat.getHit(player, target, 1.1, 1.1, 1, false));
        player.sendSound(IMPALE_SOUND);
    }),
	SANCTUARY(AttackType.SLASH, 35, 0, MELEE, new Animation(1057), new Graphics(247), (player, combat, target) -> {
		final int maxLevel = player.getSkills().getLevelForXp(Skills.DEFENCE);
		final int currentLevel = player.getSkills().getLevel(Skills.DEFENCE);
		final int level = currentLevel > maxLevel ? maxLevel : currentLevel;
		player.sendSound(SANCTUARY_SOUND);
		player.getSkills().setLevel(Skills.DEFENCE, level + 8);
		player.setForceTalk(SANCTUARY_FORCETALK);
	}),
	WEAKEN(AttackType.STAB, new int[] { 6746, 19675 }, WEAPON_SPEED, MELEE, new Animation(2890), new Graphics(483), (player, combat
            , target) -> {
		combat.delayHit(0, combat.getHit(player, target, 1, 1, 1, false));
		player.sendSound(WEAKEN_SOUND);
		val demon = target.getEntityType() == EntityType.NPC && Demon.isDemon((NPC) target, false);
		//Weaken will stack additively upon successful hits, unlike the dragon warhammer, which stacks multiplicatively.
		//Two successful special attacks thus reduce an opponent's Strength, Attack and Defence by 10%, or 20% if it is a demon.
		val playerTarget = target instanceof Player;
		val normalAttack = playerTarget ? ((Player) target).getSkills().getLevelForXp(Skills.ATTACK) : NPCCDLoader.get(((NPC) target).getId()).getStatDefinitions().get(StatType.ATTACK);
		val normalStrength = playerTarget ? ((Player) target).getSkills().getLevelForXp(Skills.STRENGTH) : NPCCDLoader.get(((NPC) target).getId()).getStatDefinitions().get(StatType.STRENGTH);
		val normalDefence = playerTarget ? ((Player) target).getSkills().getLevelForXp(Skills.DEFENCE) : NPCCDLoader.get(((NPC) target).getId()).getStatDefinitions().get(StatType.DEFENCE);
		val percentage = demon ? 0.1D : 0.05D;
		val attackToDrain = (int) (normalAttack * percentage);
		val strengthToDrain = (int) (normalStrength * percentage);
		val defenceToDrain = (int) (normalDefence * percentage);
		target.drainSkill(Skills.ATTACK, attackToDrain);
		target.drainSkill(Skills.STRENGTH, strengthToDrain);
		target.drainSkill(Skills.DEFENCE, defenceToDrain);
	}),
	BACKSTAB(AttackType.STAB, new int[] { 8872, 8874, 8876, 8878 }, WEAPON_SPEED, MELEE, new Animation(4198), new Graphics(704),
            (player, combat, target) -> {
		double accuracy = 1;
		if (target.getAttackedBy() != player) {
			accuracy = 2;
		}
		player.sendSound(BACKSTAB_SOUND);
		final Hit hit = combat.getHit(player, target, accuracy, 1, 1, false);
		combat.delayHit(0, hit);
		target.drainSkill(Skills.DEFENCE, hit.getDamage());
	}),
	LIQUEFY(AttackType.SLASH, 11037, WEAPON_SPEED, MELEE, new Animation(6118), new Graphics(1048, 0, 96), (player, combat, target) -> {
		final Hit hit = combat.getHit(player, target, 2, 1, 1, false);
		combat.delayHit(0, hit);
		final int quarter = hit.getDamage() / 4;
		player.getSkills().boostSkill(Skills.ATTACK, quarter);
		player.getSkills().boostSkill(Skills.STRENGTH, quarter);
		player.getSkills().boostSkill(Skills.DEFENCE, quarter);
	}),
	FAVOUR_OF_THE_WAR_GOD(AttackType.CRUSH, 11061, WEAPON_SPEED, MELEE, new Animation(6147), new Graphics(1052), (player, combat,
                                                                                                   target) -> {
		final Hit hit = combat.getHit(player, target, 1, 1, 1, true);
		combat.delayHit(0, hit);
		int amount = hit.getDamage();
		final int amt = player.getPrayerManager().getPrayerPoints();
		player.sendSound(FAVOUR_OF_THE_WAR_GOD_SOUND);
		player.getPrayerManager().setPrayerPoints((Math.min(amt, player.getSkills().getLevelForXp(Skills.PRAYER))) + amount);
		if (player.inArea("Fight caves")) {
			player.putBooleanAttribute("drainedPrayerInFightCaves", true);
		}
		if (target.getEntityType() == EntityType.PLAYER) {
			final Player p2 = (Player) target;
			final int prayer = p2.getPrayerManager().getPrayerPoints();
			amount = prayer > hit.getDamage() ? hit.getDamage() : prayer;
			p2.getPrayerManager().drainPrayerPoints(amount);
		}
	}),
	SARADOMINS_LIGHTNING(AttackType.SLASH, 11838, WEAPON_SPEED, MELEE, new Animation(1132), new Graphics(1213), (player, combat, target) -> {
		target.setGraphics(SARADOMINS_LIGHTNING_GFX);
		final int meleeDamage = combat.getRandomHit(player, target, combat.getMaxHit(player, 1.1, 1, false), 1, AttackType.MAGIC);
		final int magicDamage = Utils.random(1, 16);
		combat.delayHit(0, new Hit(player, meleeDamage, HitType.MELEE), new Hit(player, magicDamage, HitType.MAGIC));
		World.sendSoundEffect(player, SARADOMINS_LIGHTNING_SWORD_SOUND);
		World.sendSoundEffect(player, SARADOMINS_LIGHTNING_SOUND);
	}),
	BLESSED_SARADOMINS_LIGHTNING(AttackType.SLASH, new int[] { 12808, 12809 }, WEAPON_SPEED, MELEE, new Animation(1133),
            new Graphics(
			1213), (player, combat, target) -> {
				target.setGraphics(SARADOMINS_LIGHTNING_GFX);
				World.sendGraphics(BLESSED_SARADOMINS_LIGHTNING_GFX, new Location(target.getLocation()));
				final int meleeDamage = combat.getRandomHit(player, target, combat.getMaxHit(player, 1.25, 1, false), 1,
                        AttackType.MAGIC);
				combat.delayHit(0, new Hit(player, meleeDamage, HitType.MELEE));
			}),
	SHIELD_BASH(AttackType.CRUSH, 21015, WEAPON_SPEED, MELEE, new Animation(7511), new Graphics(1336, 0, 30), (player, combat,
                                                                                                target) -> {
		final List<Entity> possibleTargets = player.getPossibleTargets(EntityType.BOTH);
		possibleTargets.remove(target);
		combat.delayHit(0, combat.getHit(player, target, 1.2, 1, 1, false));
		World.sendSoundEffect(player, SHIELD_BASH_SOUND);
		int count = 1;
		if (player.isMultiArea()) {
			for (final Entity e : possibleTargets) {
				if (!e.getLocation().withinDistance(player.getLocation(), 5) || !e.isMultiArea() || (e instanceof Player && !player.canHit((Player) e))) {
					continue;
				}
				combat.delayHit(e, 0, combat.getHit(player, e, 1.2, 1, 1, false));
				if (++count == 10) {
					break;
				}
			}
		}
	}),
	POWERSHOT(AttackType.RANGED, new int[] { 859, 10284 }, 5, RANGED, new Animation(426), new Graphics(250, 0, 95),
            (player, combat, target) -> {
		World.sendProjectile(player, target, SNAPSHOT_FIRST_PROJ);
		final int damage = combat.getMaxHit(player, 1, 1, false);
		val delay = SNAPSHOT_FIRST_PROJ.getTime(player.getLocation(), target.getLocation());
		combat.delayHit(delay,
				new Hit(player, Utils.random(1, damage), HitType.RANGED));
		player.sendSound(POWERSHOT_SOUND);
        if (combat instanceof RangedCombat) {
            val ranged = (RangedCombat) combat;
            ranged.dropAmmunition(delay, false);
        }
	}),
	SNIPE(AttackType.RANGED, 8880, 4, RANGED, new Animation(426), null, (player, combat, target) -> {
		World.sendProjectile(player, target, SNIPE_PROJ);
		double accuracy = 1;
		if (target.getAttackedBy() != player) {
			accuracy = 2;
		}
		final Hit hit = combat.getHit(player, target, accuracy, 1, 1, false);
		val delay = SNIPE_PROJ.getTime(player.getLocation(), target.getLocation());
		combat.delayHit(delay, hit);
		target.drainSkill(Skills.DEFENCE, hit.getDamage());
		player.sendSound(SNIPE_SOUND);
        if (combat instanceof RangedCombat) {
            val ranged = (RangedCombat) combat;
            ranged.dropAmmunition(delay, false);
        }
	}),
	SOULSHOT(AttackType.RANGED, 6724, 4, RANGED, new Animation(426), new Graphics(472, 0, 90),
            (player, combat, target) -> {
		World.sendProjectile(player, target, SOULSHOT_PROJ);
		target.setGraphics(SOULSHOT_GFX);
		final int maxhit = combat.getMaxHit(player, 1, 1, false);
		final int damage = Utils.random(1, maxhit);
		player.sendSound(SOULSHOT_SOUND);
		combat.delayHit(SOULSHOT_PROJ.getTime(player.getLocation(), target.getLocation()), new Hit(player, damage, HitType.RANGED));
		target.drainSkill(Skills.MAGIC, damage);
	}),
	TOXIC_SIPHON(AttackType.RANGED, 12926, 3, RANGED, new Animation(5061), null, (player, combat, target) -> {
		World.sendProjectile(player, target, TOXIC_SIPHON_PROJ);
		val hit = combat.getHit(player, target, 1, 1.5, 1, false);
		val delay = TOXIC_SIPHON_PROJ.getTime(player.getLocation(), target.getLocation());
		combat.delayHit(delay, hit);
        val clientCycles = TOXIC_SIPHON_PROJ.getProjectileDuration(player.getLocation(), target.getLocation());
        player.sendSound(TOXIC_SIPHON_DART_SOUND);
        player.sendSound(new SoundEffect(TOXIC_SIPHON_FART_SOUND.getId(), 0, clientCycles));
		player.heal(hit.getDamage() / 2);
		if (target.getEntityType() == EntityType.NPC) {
			WorldTasksManager.schedule(() -> player.getActionManager().setActionDelay(player.getActionManager().getActionDelay() - 1));
		}
	}),
    CONCENTRATED_SHOT(AttackType.RANGED, new int[]{19478, 19481}, 6, RANGED, new Animation(7222), null, (player, combat,
                                                                                                    target) -> {
        if (player.getAmmo() == null) {
            return;
        }
        final AmmunitionDefinitions defs = AmmunitionDefinitions.getConcentratedDefinitions(player.getAmmo().getId());
        if (defs == null) {
            return;
        }
        if (defs.getProjectile() == null) {
            return;
        }

        val projectile = defs.getProjectile();
        val clientCycles = projectile.getProjectileDuration(player.getLocation(), target.getLocation());
        target.setGraphics(new Graphics(344, clientCycles, 146));
        player.sendSound(CONCENTRATED_SHOT_SOUND);
        World.sendProjectile(player, target, defs.getProjectile());
        val delay = defs.getProjectile().getTime(player.getLocation(), target.getLocation());
        combat.delayHit(delay,
                combat.getHit(player, target, 1.25, 1.25, 1, false));
        if (combat instanceof RangedCombat) {
            val ranged = (RangedCombat) combat;
            ranged.dropAmmunition(delay, true);
        }
    }),
	POWER_OF_DEATH(AttackType.MAGIC, new int[] { 11791, 12904, 12902, 22296 }, 0, MAGIC, null, null, (player, combat,
                                                                                                  target) -> {
		final int weaponId = player.getWeapon() == null ? -1 : player.getWeapon().getId();
		if (weaponId == 22296) {
			player.setAnimation(SOL_POWER_OF_DEATH_ANIM);
			player.setGraphics(SOL_POWER_OF_DEATH_GFX);
		} else if (weaponId == 11791) {
			player.setAnimation(POWER_OF_DEATH_ANIM);
			player.setGraphics(POWER_OF_DEATH_GFX);
		} else {
			player.setAnimation(TOXIC_POWER_OF_DEATH_ANIM);
			player.setGraphics(TOXIC_POWER_OF_DEATH_GFX);
		}
		player.sendMessage(Colour.RS_GREEN.wrap("Spirits of deceased evildoers offer you their protection."));
		World.sendSoundEffect(player, POWER_OF_DEATH_SOUND);
		player.getVariables().schedule(100, TickVariable.POWER_OF_DEATH);
	}),

    SPEAR_WALL(AttackType.CRUSH, 22610, WEAPON_SPEED, MELEE, new Animation(8184), new Graphics(1627), (player, combat, target) -> {
       val possibleTargets = !player.isMultiArea() ? Arrays.asList(ArrayUtils.toArray(target)) :
               player.getPossibleTargets(EntityType.BOTH);
       val hash = player.getLocation().getPositionHash();
       var count = 0;
       player.addImmunity(HitType.MELEE, 4800);
       player.sendSound(SPEAR_WALL_SOUND);
       for (val possibleTarget : possibleTargets) {
           if (possibleTarget instanceof Player && !player.canHit((Player) player)) continue;
           val location = possibleTarget.getLocation();
           val distanceX = player.getX() - target.getX();
           val distanceY = player.getY() - target.getY();
           val size = target.getSize();
           if (possibleTarget != target && (location.getPositionHash() == hash || distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)) {
               continue;
           }
           combat.delayHit(possibleTarget, 0, combat.getHit(player, target, 1, 1, 1, false));
           if (++count >= 16)
               break;
       }
    }),

    FEINT(AttackType.STAB, 22613, WEAPON_SPEED, MELEE, new Animation(246), null, (player, combat, target) -> {
        //Accuracy modifier set to 4x as target's defence is reduced by 75%, which is an identical comparison.
        val hit = combat.getHit(player, target, 4, 1, 1, false);
        if (hit.getDamage() > 0) {
            hit.setDamage(((int) (combat.getMaxHit(player, 1, 1, false) * 0.2F)) + hit.getDamage());
        }
        player.sendSound(FEINT_SOUND);
        combat.delayHit(target, 0, hit);
    }),

    SWH_SMASH(AttackType.CRUSH, 22622, WEAPON_SPEED, MELEE, new Animation(1378), new Graphics(844), (player, combat, target) -> {
        val hit = combat.getHit(player, target, 1, 1, 1, false);
        if (hit.getDamage() > 0) {
            hit.setDamage(((int) (combat.getMaxHit(player, 1, 1, false) * 0.2F)) + hit.getDamage());
            target.drainSkill(Skills.DEFENCE, 30F);
        }
        player.sendSound(SMASH_SOUND);
        combat.delayHit(target, 0, hit);
    }),

    HAMSTRING(AttackType.RANGED, 22634, 4, RANGED, new Animation(929), new Graphics(1626, 0, 90), (player, combat,
                                                                                                   target) -> {
        val delay = World.sendProjectile(player, target, HAMSTRING_PROJ);
        val hit = combat.getHit(player, target, 1, 1, 1, false);
        if (hit.getDamage() > 0) {
            hit.setDamage(((int) (combat.getMaxHit(player, 1, 1, false) * 0.2F)) + hit.getDamage());
        }
        if (combat instanceof RangedCombat) {
            val ranged = (RangedCombat) combat;
            ranged.dropAmmunition(delay, true);
        }
        player.sendSound(HAMSTRING_SOUND);
        WorldTasksManager.schedule(() -> {
            combat.delayHit(target, -1, hit);
            if (target instanceof Player) {
                ((Player) target).getVariables().schedule(100, TickVariable.HAMSTRUNG);
                ((Player) target).sendMessage("You've been hamstrung! For the next minute, your run energy will drain 6x faster.");
            }
        }, delay);
    }),

    PHANTOM_STRIKE(AttackType.RANGED, 22636, 4, RANGED, new Animation(806), new Graphics(1621, 0, 90), (player,
                                                                                                        combat, target) -> {
        val delay = World.sendProjectile(player, target, PHANTOM_STRIKE_PROJ);
        val hit = combat.getHit(player, target, 1, 1, 1, false);
        if (combat instanceof RangedCombat) {
            val ranged = (RangedCombat) combat;
            ranged.dropAmmunition(delay, true);
        }
        WorldTasksManager.schedule(() -> {
            combat.delayHit(target, -1, hit);
            if (target instanceof Player) {
                WorldTasksManager.schedule(new TickTask() {
                    private final Player targetPlayer = (Player) target;
                    private int damage = hit.getDamage();
                    @Override
                    public void run() {
                        if (target.isFinished() || target.isDead() || target.isLocked()) {
                            stop();
                            return;
                        }
                        targetPlayer.sendMessage(ticks++ == 0 ? "You start to bleed as a payload of the javelin " +
                                "strike." : "You continue to bleed as a payload of the javelin strike.");
                        targetPlayer.applyHit(new Hit(player, damage > 5 ? 5 : damage, HitType.REGULAR));
                        if ((damage -= 5) <= 0) {
                            stop();
                        }
                    }
                }, 3, 3);
            }
        }, delay);
    }),

	ANNIHILATE(AttackType.RANGED, 21902, 5, RANGED, new Animation(4230), null, (player, combat, target) -> {
		World.sendProjectile(player, target, ANNIHILATE_PROJ);
		val delay = SNIPE_PROJ.getTime(player.getLocation(), target.getMiddleLocation());
		WorldTasksManager.schedule(() -> target.setGraphics(ANNIHILATE_GFX), delay);
		player.sendSound(ANNIHILATE_START_SOUND);
		val clientTicks = SNIPE_PROJ.getProjectileDuration(player.getLocation(), target.getMiddleLocation());
		player.sendSound(new SoundEffect(ANNIHILATE_END_SOUND.getId(), ANNIHILATE_END_SOUND.getRadius(), clientTicks));
		combat.attackTarget(combat.getMultiAttackTargets(player), originalTarget -> {
			val hit = combat.getHit(player, combat.target, 1, combat.target == originalTarget ? 1.2F : 1, 1, false);
			combat.delayHit(combat.target, delay, hit);
			if (combat.target == originalTarget) {
				return hit.getDamage() > 0;
			}
			hit.putAttribute("notMainFocusedTarget", true);
			return true;
		});
	}),
	PULSATE(AttackType.MAGIC, 22516, 4, MAGIC, new Animation(1167), new Graphics(1546, 0, 92), (player, combat, target) -> {
		if(target.getEntityType() == EntityType.NPC) {
			if(((NPC)target).getId() != 8370) {
				return;
			}
		}
		player.sendSound(new SoundEffect(178));
		World.sendProjectile(player, target, PULSATE_PROJ);
		int delay = PULSATE_PROJ.getTime(player.getLocation(), target.getMiddleLocation());
		val clientTicks = PULSATE_PROJ.getProjectileDuration(player.getLocation(), target.getMiddleLocation());

		WorldTasksManager.schedule(() -> {
			target.setGraphics(PULSATE_GFX);
			player.sendSound(PULSATE_SOUND);
		}, delay);
		Hit hit = new Hit(player, Utils.random(75, 150), HitType.VERZIK_SHIELD);
		combat.delayHit(target, delay, hit);

		/*null, 24, 0, new Animation(1167),
				new Graphics(1546, 0, 92), new Graphics(1545, 56, 60),
				new SoundEffect(178, 0, 0), new SoundEffect(1460, 10, 56),
				new Projectile(1544, 23, 15, 51, 16, 25, 64, 10 )*/
	});

	;

	@Getter
	private final int[] weapons;
	@Getter
	private final int delay;
	@Getter
	private final SpecialType type;
	@Getter
	private final Animation animation;
	@Getter
	private final Graphics graphics;
	@Getter
	private final SpecialAttackScript attack;
	@Getter private final AttackType attackType;

	public static final Map<Integer, SpecialAttack> SPECIAL_ATTACKS = new HashMap<Integer, SpecialAttack>();

	static {
		for (final SpecialAttack att : values()) {
			for (final int id : att.weapons) {
				SPECIAL_ATTACKS.put(id, att);
			}
		}
	}

	SpecialAttack(AttackType attackType, final int weapon, final int delay, final SpecialType type, final Animation animation, final Graphics graphics, final SpecialAttackScript attack) {
		this(attackType, new int[] { weapon }, delay, type, animation, graphics, attack);
	}

	SpecialAttack(AttackType attackType, final int[] weapons, final int delay, final SpecialType type, final Animation animation, final Graphics graphics, final SpecialAttackScript attack) {
		this.weapons = weapons;
		this.delay = delay;
		this.type = type;
		this.animation = animation;
		this.graphics = graphics;
		this.attack = attack;
		this.attackType = attackType;
	}

}
