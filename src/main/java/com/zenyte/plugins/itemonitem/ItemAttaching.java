package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.enums.DismantleableItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.items.ItemDefinitions;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * @author Kris | 4. apr 2018 : 17:56.08
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ItemAttaching implements ItemOnItemAction {

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		DismantleableItem dis = null;
		final int f = from.getId();
		final int t = to.getId();
		for (final DismantleableItem val : DismantleableItem.VALUES) {
			final int base = val.getBaseItem();
			final int kit = val.getKit();
			if (base == f && kit == t || base == t && kit == f) {
				dis = val;
				break;
			}
		}
		if (dis == null) {
			return;
		}
		player.sendMessage("You attach the " + ItemDefinitions.get(dis.getKit()).getName().toLowerCase() + " onto the " + ItemDefinitions.get(dis.getBaseItem()).getName().toLowerCase() + ".");
		player.getInventory().deleteItem(fromSlot, from);
		player.getInventory().deleteItem(toSlot, to);
		player.getInventory().addItem(dis.getCompleteItem(), 1);
	}

	@Override
	public int[] getItems() {
		final IntArrayList list = new IntArrayList();
		for (final DismantleableItem val : DismantleableItem.VALUES) {
			list.add(val.getKit());
			list.add(val.getBaseItem());
		}
		return list.toArray(new int[list.size()]);
	}

}
