package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

/**
 * @author Cresinkel
 */
public class ColossalPouchCreation implements ItemOnItemAction {

	@AllArgsConstructor
	public static enum Pouches {

		SMALL(5509),
		MEDIUM(5510),
		LARGE(5512),
		GIANT(5514);

		public static final Pouches[] VALUES = values();
		public static final Int2ObjectOpenHashMap<Pouches> MAPPED_VALUES = new Int2ObjectOpenHashMap<Pouches>(VALUES.length);

		static {
			for (val value : VALUES) {
				MAPPED_VALUES.put(value.pouchId, value);
			}
		}

		private final int pouchId;
	}

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		val needle = from.getId() == 32201 ? from : to;
		val pouch = needle == from ? to : from;
		if (player.containsItem(ItemId.COLOSSAL_POUCH)) {
			player.sendMessage("You already have a colossal pouch.");
			return;
		}
		if (player.getInventory().containsItems(new Item(5509), new Item(5510), new Item(5512), new Item(5514)) ) {
			player.getInventory().deleteItems(new Item(5509), new Item(5510), new Item(5512), new Item(5514), new Item(32201));
			player.getInventory().addItem(32194, 1);
			player.sendMessage("You attempt to stitch together all of the pouches. It's difficult, but you eventually craft a strange, kind of gross, fleshy container. It seems capable of storing up to 40 essence.");
		} else {
			player.sendMessage("You do not own all 4 required pouches to sew them together.");
		}
	}

	@Override
	public int[] getItems() {
		return null;
	}

	@Override
	public ItemPair[] getMatchingPairs() {
		return new ItemPair[]{new ItemPair(32201, Pouches.SMALL.pouchId), new ItemPair(32201, Pouches.MEDIUM.pouchId), new ItemPair(32201, Pouches.LARGE.pouchId),
				new ItemPair(32201, Pouches.GIANT.pouchId)
		};
	}

}