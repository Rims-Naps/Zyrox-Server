package com.zenyte.plugins.item;

import com.zenyte.game.item.enums.DismantleableItem;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import mgi.types.config.items.ItemDefinitions;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import lombok.val;

/**
 * @author Kris | 25. aug 2018 : 22:30:05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class Dismantleable extends ItemPlugin {

	@Override
	public void handle() {
		bind("Dismantle", (player, item, slotId) -> {
			if (!player.getInventory().hasFreeSlots()) {
				player.sendMessage("You need some free inventory space to dismantle this item.");
				return;
			}
			final DismantleableItem dis = DismantleableItem.MAPPED_VALUES.get(item.getId());
			if (dis == null) {
				return;
			}
			player.getInventory().deleteItem(slotId, item);
			player.getInventory().addItem(dis.getKit(), 1);
			player.getInventory().addItem(dis.getBaseItem(), 1);
			player.sendMessage("You dismantle the " + ItemDefinitions.get(dis.getKit()).getName().toLowerCase() + " from the " + ItemDefinitions.get(dis.getBaseItem()).getName().toLowerCase() + ".");

		});
	}

	@Override
	public int[] getItems() {
		val list = new IntArrayList();
		for (final DismantleableItem val : DismantleableItem.VALUES) {
			list.add(val.getCompleteItem());
		}
		return list.toArray(new int[list.size()]);
	}

}
