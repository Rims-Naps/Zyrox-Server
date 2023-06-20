package com.zenyte.game.world.entity.player.action.combat.magic.spelleffect;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import lombok.val;

public class BloodEffect implements SpellEffect {

	@Override
	public void spellEffect(final Entity player, final Entity target, final int damage) {
		if (damage < 4) {
			return;
		}
		if(player instanceof Player && ((Player) player).getEquipment().getId(EquipmentSlot.WEAPON) == ItemId.ZURIELS_STAFF) {
			player.heal((int) (damage / ( 4 / 1.5f)));
		} else {
			player.heal(damage / 4);
		}
	}

}
