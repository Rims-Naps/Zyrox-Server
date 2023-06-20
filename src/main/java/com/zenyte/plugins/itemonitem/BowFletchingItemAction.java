package com.zenyte.plugins.itemonitem;

import com.zenyte.game.content.skills.fletching.FletchingDefinitions.BowFletchingData;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.skills.BowStringingD;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

/**
 * @author Kris | 11. nov 2017 : 0:32.30
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class BowFletchingItemAction implements ItemOnItemAction {

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		val bow = BowFletchingData.getDataByMaterial(from, to);
		if (bow != null && BowFletchingData.hasRequirements(player, bow)) {
			player.getDialogueManager().start(new BowStringingD(player, bow));
			return;
		} else {
			player.sendMessage("Nothing interesting happens");
		}
	}

	@Override
	public int[] getItems() {
		val list = new IntArrayList();
		for (val data : BowFletchingData.VALUES_ARR) {
			for (val item : data.getMaterials()) {
				list.add(item.getId());
			}
		}
		return list.toArray(new int[list.size()]);
	}

}
