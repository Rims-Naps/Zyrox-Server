package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

/**
 * @author Kris | 25. aug 2018 : 21:53:16
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class BlackMask extends ItemPlugin {

	@Override
	public void handle() {
		bind("Uncharge", (player, item, slotId) -> {
			player.getDialogueManager().start(new Dialogue(player) {
				@Override
				public void buildDialogue() {
					options("Are you sure you wish to uncharge the mask?", "Yes", "No")
					.onOptionOne(() -> {
					    if (player.getInventory().getItem(slotId) != item) {
							return;
						}
					    if (!item.getName().contains("(i)") || item.getId() == 11784) {
							item.setId(8921);
						} else {
							item.setId(11784);
						}
						player.getInventory().refresh(slotId);
						player.sendMessage("You uncharge your black mask.");
					});
				}
			});
		});
	}

	@Override
	public int[] getItems() {
		val list = new IntArrayList();
		for (int i = 8901; i <= 8919; i+=2) {
			list.add(i);
		}
		for (int i = 11774; i <= 11784; i++) {
			list.add(i);
		}
		return list.toArray(new int[list.size()]);
	}

}
