package com.zenyte.plugins.itemonitem;

import com.zenyte.game.content.skills.herblore.actions.Combine.HerbloreData;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.skills.HerbloreD;

import lombok.val;

import java.util.ArrayList;

/**
 * @author Kris | 11. nov 2017 : 0:30.14
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class HerbloreItemAction implements ItemOnItemAction {

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		val data = HerbloreData.getDataByMaterial(from, to);
		if (data != null) {
			if (HerbloreData.hasRequirements(player, data)) {
				player.getDialogueManager().start(new HerbloreD(player, data));
			}
			return;
		} else {
			player.sendMessage("Nothing interesting happens");
		}
	}

	@Override
	public ItemPair[] getMatchingPairs() {
		val pairs = new ArrayList<ItemPair>();
		for (int i = 0; i < HerbloreData.values.length; i++) {
			val data = HerbloreData.values[i];
			val materials = data.getMaterials();
			if (data.equals(HerbloreData.SUPER_COMBAT_WITH_HERB) || data.equals(HerbloreData.SUPER_COMBAT_WITH_UNF)) {
				for (val material : materials) {
					if (material.getId() == materials[0].getId()) {
						continue;
					}
					pairs.add(new ItemPair(materials[0].getId(), material.getId()));
				}
			} else {
				if (materials.length > 1) {
					pairs.add(new ItemPair(materials[0].getId(), materials[1].getId()));
				}
			}
		}
		return pairs.toArray(new ItemPair[pairs.size()]);
	}

	@Override
	public int[] getItems() { 
		return null;
	}

}
