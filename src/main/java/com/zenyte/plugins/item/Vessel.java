package com.zenyte.plugins.item;

import com.zenyte.game.item.enums.ContainerItem;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import lombok.val;

/**
 * @author Kris | 25. aug 2018 : 22:20:37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class Vessel extends ItemPlugin {

	@Override
	public void handle() {
		bind("Empty", (player, item, slotId) -> {
			val container = ContainerItem.all.get(item.getId());
			if (container == null) {
				return;
			}
			player.getInventory().replaceItem(container.getType().getEmpty().getId(), 1, slotId);
			player.sendMessage(
					"You empty the contents of the " + container.getType().getEmpty().getName().toLowerCase() + " onto the floor.");
        });
	}

	@Override
	public int[] getItems() {
		val list = new IntArrayList();
		for (final ContainerItem container : ContainerItem.lists.get("empty")) {
			list.add(container.getContainer().getId());
		}
		return list.toArray(new int[0]);
	}

}
