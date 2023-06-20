package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Tommeh | 24 apr. 2018 | 00:00:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class AvernicDefenderCreationAction implements ItemOnItemAction {

	private static final Item DRAGON_DEFENDER = new Item(12954);
	private static final Item AVERNIC_DEFENDER_HILT = new Item(22477);
	private static final Item AVERNIC_DEFENDER = new Item(22322);

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		player.getDialogueManager().start(new Dialogue(player) {
			@Override
			public void buildDialogue() {
				doubleItem(12954, 22477, "<col=FF0040>Warning!</col><br>Creating the Avernic defender will consume the hilt. You won't be able to get the hilt back again. The Avernic defender is not tradeable.");
				options("Are you sure you wish to do this?", "Yes, make the Avernic defender.", "No, I'll keep my hilt.")
				.onOptionOne(() -> {
					player.getInventory().deleteItem(AVERNIC_DEFENDER_HILT);
					player.getInventory().deleteItem(DRAGON_DEFENDER);
					player.getInventory().addItem(AVERNIC_DEFENDER);
				});
			}
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 12954, 22477 };
	}

}
