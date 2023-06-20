package com.zenyte.game.world.entity.player.action.combat.magic.spelleffect;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Toxins.ToxinType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;

public class SmokeEffect implements SpellEffect {

	public SmokeEffect(final int damage) {
		this.damage = damage;
	}

	private final int damage;

	@Override
	public void spellEffect(final Entity player, final Entity target, final int damage) {
		if (Utils.random(3) != 0) {
			return;
		}
		if(player instanceof Player) {
			target.getToxins().applyToxin(ToxinType.POISON,(int)(((Player) player).getEquipment().getId(EquipmentSlot.WEAPON) == ItemId.ZURIELS_STAFF ? this.damage * 2 : this.damage));
		} else {
			target.getToxins().applyToxin(ToxinType.POISON, this.damage);
		}
	}

}
