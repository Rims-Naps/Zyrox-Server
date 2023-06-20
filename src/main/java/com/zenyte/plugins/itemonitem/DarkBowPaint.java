package com.zenyte.plugins.itemonitem;

import java.util.ArrayList;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

/**
 * @author Kris | 28. aug 2018 : 17:05:07
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class DarkBowPaint implements ItemOnItemAction {

	@AllArgsConstructor
	public static enum BowPaint {

		GREEN_PAINT(12759, 12765),
		YELLOW_PAINT(12761, 12767),
		WHITE_PAINT(12763, 12768),
		BLUE_PAINT(12757, 12766);
		public static final BowPaint[] VALUES = values();
		public static final Int2ObjectOpenHashMap<BowPaint> MAPPED_VALUES = new Int2ObjectOpenHashMap<BowPaint>(VALUES.length);
		static {
			for (val value : VALUES) {
				MAPPED_VALUES.put(value.paintId, value);
				MAPPED_VALUES.put(value.recolouredBowId, value);
			}
		}
		private final int paintId;
		@Getter private final int recolouredBowId;
	}

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		val bow = from.getId() == 11235 ? from : to;
		val dye = bow == from ? to : from;
		val mix = BowPaint.MAPPED_VALUES.get(dye.getId());
		if (mix == null) {
			return;
		}
		player.getDialogueManager().start(new Dialogue(player) {
			@Override
			public void buildDialogue() {
				item(new Item(mix.recolouredBowId), Colour.RED + "WARNING!" + Colour.END
						+ " changing the colour of your Dark bow will render it untradeable. You will need to use a cleaning cloth to revert the changes. Are you sure?");
				options(TITLE, "Yes.", "No.").onOptionOne(() -> {
					val inventory = player.getInventory();
					inventory.deleteItem(fromSlot, from);
					inventory.deleteItem(toSlot, to);
					inventory.addItem(new Item(mix.recolouredBowId));
					player.sendMessage("You coat your dark bow in " + mix.toString().toLowerCase().replaceAll("_", " ") + ".");
				});
			}
		});
	}

	@Override
	public int[] getItems() {
		return null;
	}

	@Override
	public ItemPair[] getMatchingPairs() {
		val list = new ArrayList<ItemPair>();
		for (val paint : BowPaint.VALUES) {
			list.add(new ItemPair(11235, paint.paintId));
		}
		return list.toArray(new ItemPair[list.size()]);
	}

}
