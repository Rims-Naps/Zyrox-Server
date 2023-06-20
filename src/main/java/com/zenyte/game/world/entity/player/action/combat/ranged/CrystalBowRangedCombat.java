package com.zenyte.game.world.entity.player.action.combat.ranged;

import com.zenyte.game.item.degradableitems.DegradeType;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Entity.EntityType;
import com.zenyte.game.world.entity.pathfinding.events.player.CombatEntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.PredictedEntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.action.combat.AmmunitionDefinitions;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.action.combat.RangedCombat;
import com.zenyte.game.world.region.area.plugins.PlayerCombatPlugin;
import lombok.val;

/**
 * @author Kris | 1. juuni 2018 : 22:44:23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class CrystalBowRangedCombat extends RangedCombat {

	public CrystalBowRangedCombat(final Entity target, final AmmunitionDefinitions defs) {
		super(target, defs);
	}

	@Override
	public int processWithDelay() {
		if (!target.startAttacking(player, CombatType.RANGED)) {
			return -1;
		}
		if (!isWithinAttackDistance()) {
			return 0;
		}
		if (!canAttack()) {
			return -1;
		}
        val area = player.getArea();
        if (area instanceof PlayerCombatPlugin) {
            ((PlayerCombatPlugin) area).onAttack(player, target, "Ranged");
        }
		addAttackedByDelay(player, target);
		val ticks = this.fireProjectile();
		animate();
		val hit = getHit(player, target, 1, 1, 1, false);
		dropAmmunition(ticks, false);
		if (hit.getDamage() > 0) {
			addPoisonTask(ticks);
		}
		resetFlag();
		delayHit(ticks, hit);
		drawback();
		checkIfShouldTerminate();
		return getWeaponSpeed();
	}
	
	@Override
	protected void dropAmmunition(final int delay, final boolean destroy) {
		player.getChargesManager().removeCharges(DegradeType.OUTGOING_HIT);
	}
	
	@Override
	protected boolean initiateCombat(final Player player) {
		if (player.isDead() || player.isFinished() || player.isLocked() || player.isStunned()) {
			return false;
		}
		if (target.isDead() || target.isFinished() || target.isCantInteract()) {
			return false;
		}
		if (outOfAmmo()) {
			player.sendMessage("You've ran out of ammo!");
			return false;
		}
		val distanceX = player.getX() - target.getX();
		val distanceY = player.getY() - target.getY();
		val size = target.getSize();
		val viewDistance = player.getViewDistance();
		if (player.getPlane() != target.getPlane() || distanceX > size + viewDistance || distanceX < -1 - viewDistance
				|| distanceY > size + viewDistance || distanceY < -1 - viewDistance) {
			return false;
		}
		if (target.getEntityType() == EntityType.PLAYER) {
			if (!player.isCanPvp() || !((Player) target).isCanPvp()) {
				player.sendMessage("You can't attack someone in a safe zone.");
				return false;
			}
		}
		if (player.isFrozen() || player.getMovementLock() > Utils.currentTimeMillis()) {
			return true;
		}

		if (colliding()) {
			player.getCombatEvent().process();
			return true;
		}
		if (handleDragonfireShields(player, false)) {
			if (!canAttack()) {
				return false;
			}
			handleDragonfireShields(player, true);
			player.getActionManager().addActionDelay(4);
			return true;
		}
		return pathfind();
	}

	@Override
	public boolean start() {
        player.setCombatEvent(new CombatEntityEvent(player, new PredictedEntityStrategy(target)));
		player.setLastTarget(target);
		if (player.isFrozen()) {
			final int distanceX = player.getX() - target.getX();
			final int distanceY = player.getY() - target.getY();
			final int size = target.getSize();
			final int maxDistance = getAttackDistance();
			final boolean projectileClipped = player.isProjectileClipped(target, false);
			if (projectileClipped || distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance
					|| distanceY < -1 - maxDistance) {
				player.sendMessage("A magical force stops you from moving.");
			}
		}
		float modifier = 0;
		if (player.getAmulet() != null && CombatUtilities.SALVE_AFFECTED_NPCS.contains(name)) {
			final int id = player.getAmulet().getId();
			if (id == 12017) {
				modifier = 0.15f;
			} else if (id == 12018) {
				modifier = 0.2f;
			}
		}
		if (modifier == 0) {
			if (player.getSlayer().isCurrentAssignment(target)) {
				final String helm = player.getHelmet() == null ? "null" : player.getHelmet().getDefinitions().getName().toLowerCase();
				if ((helm.contains("black mask") || helm.contains("slayer helm")) && helm.endsWith("(i)")) {
					modifier = 0.15f;
				}
			}
		}
		if (CombatUtilities.hasFullRangedVoid(player, true)) {
			maxhitModifier += 0.12;
		} else if (CombatUtilities.hasFullRangedVoid(player, false)) {
			maxhitModifier += 0.1;
		}
		if (maxhitModifier > 1) {
			accuracyModifier += maxhitModifier - 1;
		}
		maxhitModifier += modifier;
		accuracyModifier += modifier;
		player.setFaceEntity(target);
		if (initiateCombat(player)) {
			return true;
		}
		player.setFaceEntity(null);
		return false;
	}
	
}
