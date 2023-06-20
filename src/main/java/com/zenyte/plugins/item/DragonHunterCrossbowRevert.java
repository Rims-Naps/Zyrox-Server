package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Cresinkel
 */

public class DragonHunterCrossbowRevert extends ItemPlugin {

	@Override
	public void handle() {
		bind("Dismantle", (player, item, slotId) -> {
			player.getDialogueManager().start(new Dialogue(player) {
				@Override
				public void buildDialogue() {
					options("Are you sure you wish to do this?", "Yes, dismantle the " + item.getName() + ".", "No, I'll keep my " + item.getName() + ".")
					.onOptionOne(() -> {
						player.getInventory().deleteItem(item.getId(), 1);
						player.getInventory().addItem(new Item(ItemId.DRAGON_HUNTER_CROSSBOW));
						player.getInventory().addOrDrop(item.getId() == 32197 ? 7980 : 21907);
					});
				}
			});
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 32197, 32199 };
	}

}
