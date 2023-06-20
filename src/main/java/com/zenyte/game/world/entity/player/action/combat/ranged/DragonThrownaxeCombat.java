package com.zenyte.game.world.entity.player.action.combat.ranged;

import com.zenyte.game.item.Item;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.action.combat.AmmunitionDefinitions;
import com.zenyte.game.world.entity.player.action.combat.RangedCombat;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;

import lombok.val;

/**
 * @author Kris | 2. juuni 2018 : 04:36:14
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class DragonThrownaxeCombat extends RangedCombat {

	public DragonThrownaxeCombat(final Entity target, final AmmunitionDefinitions defs) {
		super(target, defs);
	}

	@Override
	protected void dropAmmunition(final int delay, final boolean destroy) {
		if (ammunition == null) {
			return;
		}
		val slot = ammunition.isWeapon() ? EquipmentSlot.WEAPON : EquipmentSlot.AMMUNITION;
		val slotId = slot.getSlot();
		val ammo = player.getEquipment().getItem(slotId);
		if (ammo == null) {
			return;
		}
		val dropChance = getAmmunitionDropChance();
		val roll = Utils.random(100);
		val equipment = player.getEquipment();

		val destroyAmmo = (ammunition == AmmunitionDefinitions.DRAGON_THROWNAXE
				&& player.getTemporaryAttributes().get("dragonThrownaxe") != null);

		if (destroyAmmo || destroy || roll <= BREAK_CHANCE || roll <= (BREAK_CHANCE + dropChance)) {
			val ammoAmount = ammo.getAmount();
			if (ammoAmount > 1) {
				ammo.setAmount(ammoAmount - 1);
			} else {
				equipment.set(slot, null);
			}
			equipment.refresh(slotId);
			if (destroy || roll < BREAK_CHANCE) {
				return;
			}
		}

		if (roll <= (BREAK_CHANCE + dropChance)) {
			val location = new Location(target.getLocation());
			if (player.getDuel() != null && player.getDuel().inDuel()) {
				return;
			}
			WorldTasksManager.schedule(() -> {
				val item = new Item(ammo.getId());
				val duel = player.getDuel();
				if (duel != null) {
					duel.getAmmunitions().get(player).add(item);
				} else {
					World.spawnFloorItem(item, !World.isFloorFree(location, 1) ? new Location(player.getLocation()) :
                            location, 20, player, player, 300, 500);
				}
			}, delay);
		}
	}

}
