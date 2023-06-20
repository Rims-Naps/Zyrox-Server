package com.zenyte.game.world.entity.player.action.combat.ranged;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.masks.Hit;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.action.combat.AmmunitionDefinitions;
import com.zenyte.game.world.entity.player.action.combat.CombatType;
import com.zenyte.game.world.entity.player.action.combat.CombatUtilities;
import com.zenyte.game.world.entity.player.action.combat.RangedCombat;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import com.zenyte.game.world.region.area.plugins.PlayerCombatPlugin;
import lombok.val;

import static com.zenyte.game.world.entity.player.action.combat.CombatUtilities.KARILS_SET_GFX;

/**
 * @author Kris | 1. juuni 2018 : 23:18:01
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class KarilsRangedCombat extends RangedCombat {

	public KarilsRangedCombat(final Entity target, final AmmunitionDefinitions defs) {
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
		addAttackedByDelay(player, target);
        val area = player.getArea();
        if (area instanceof PlayerCombatPlugin) {
            ((PlayerCombatPlugin) area).onAttack(player, target, "Ranged");
        }
		val ticks = this.fireProjectile();
		animate();
		dropAmmunition(ticks, !ammunition.isRetrievable());
		resetFlag();
		val hit = getHit(player, target, 1, 1, 1, false);
		if (hit.getDamage() > 0) {
			addPoisonTask(ticks);
		}
		delayHit(ticks, hit);
		val hasFullSet = CombatUtilities.hasFullBarrowsSet(player, "Karil's");
		val amuletId = player.getEquipment().getId(EquipmentSlot.AMULET);
		if ((amuletId == 12851 || amuletId == 12853) && hasFullSet && Utils.random(3) == 0) {
			val secondHit = new Hit(player, (int) (hit.getDamage() / 2F), hit.getHitType());
			delayHit(ticks, secondHit);
		}
		if (hit.getDamage() > 0 && Utils.random(3) == 0 && hasFullSet) {
			WorldTasksManager.schedule(() -> {
				target.setGraphics(KARILS_SET_GFX);
				target.drainSkill(Skills.AGILITY, 20D);
			});
		}
		drawback();
		checkIfShouldTerminate();
		return getWeaponSpeed();
	}

}
