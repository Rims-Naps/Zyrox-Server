package com.zenyte.game.world.entity.player.action.combat.magic.spelleffect;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;

public class SanguinestiEffect implements SpellEffect {

	@Override
	public void spellEffect(final Entity player, final Entity target, final int damage) {
		if (damage < 2) {
			return;
		}
		if(player instanceof Player &&
				(((Player) player).getEquipment().getId(EquipmentSlot.WEAPON) == ItemId.SANGUINESTI_STAFF || ((Player) player).getEquipment().getId(EquipmentSlot.WEAPON) == 32314)
				&& Utils.random(5) == 0) {
			int targetHitpoints = target.getHitpoints();
			if (targetHitpoints < damage) {
				player.heal(targetHitpoints /2);
			} else {
				player.heal(damage/2);
			}
		}
	}

}
