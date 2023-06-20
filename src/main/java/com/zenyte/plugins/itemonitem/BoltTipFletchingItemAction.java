package com.zenyte.plugins.itemonitem;

import com.zenyte.game.content.skills.fletching.FletchingDefinitions;
import com.zenyte.game.content.skills.fletching.FletchingDefinitions.BoltTipFletchingData;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.skills.BoltTipFletchingD;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

/**
 * @author Tommeh | 24 nov. 2017 : 21:14:06
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public final class BoltTipFletchingItemAction implements ItemOnItemAction {

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		val tips = BoltTipFletchingData.getDataByMaterial(from, to);
		if (tips != null && BoltTipFletchingData.hasRequirements(player, tips)) {
			player.getDialogueManager().start(new BoltTipFletchingD(player, tips));
			return;
		} else {
			player.sendMessage("Nothing interesting happens");
		}
	}

	@Override
	public int[] getItems() {
		val list = new IntArrayList();
		for (val data : BoltTipFletchingData.VALUES) {
			list.add(data.getMaterial().getId());
		}
		list.add(FletchingDefinitions.CHISEL.getId());
		return list.toArray(new int[list.size()]);
	}

}
