package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Cresinkel
 */
public class CelestialSignetItemCreationAction implements ItemOnItemAction {

	private static final Item CELESTIAL_SIGNET = new Item(32312);
	public static final Item CELESTIAL_RING = new Item(32307);
	public static final Item ELVEN_SIGNET = new Item(32309);

	//TODO animation
	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		player.getDialogueManager().start(new Dialogue(player) {
			@Override
			public void buildDialogue() {
				item(CELESTIAL_SIGNET,"<col=FF0040>Warning!</col><br>You won't be able to get the seperate rings out the celestial ring. The combined item is not tradeable.");
				options("Are you sure you wish to do this?", "Yes, let the rings merge.", "No, I'll keep my rings.")
				.onOptionOne(() -> {
					player.getInventory().deleteItem(CELESTIAL_RING);
					player.getInventory().deleteItem(ELVEN_SIGNET);
					player.getInventory().addItem(CELESTIAL_SIGNET);
				});
			}
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 32307, 32309 };
	}

}
