package com.zenyte.plugins.object;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.ItemChat;

import lombok.val;

/**
 * @author Tommeh | 30 jul. 2018 | 23:23:26
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class BarbarianBedObjectAction implements ObjectAction {

	private static final Item BARBARIAN_ROD = new Item(11323);

	@Override
	public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
		val hasSpace = player.getInventory().hasFreeSlots();
		val message = hasSpace ? "You find a heavy fishing rod under the bed and take it." : "You find a heavy fishing rod under the bed, but don't have space for it.";
		if (player.containsItem(BARBARIAN_ROD)) {
			player.getDialogueManager().start(new Dialogue(player) {
				@Override
				public void buildDialogue() {
					options("You already have a heavy fishing rod.", "Take another.", "Don't take any more.").onOptionOne(() -> {
						if (hasSpace) {
							//TODO animation
							player.getInventory().addItem(BARBARIAN_ROD);
						}
						setKey(5);
					});
					item(5, BARBARIAN_ROD, message);
				}
			});
		} else {
			player.getDialogueManager().start(new ItemChat(player, BARBARIAN_ROD, message));
			if (hasSpace) {
				//TODO animation
				player.getInventory().addItem(BARBARIAN_ROD);
			}
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 25268 };
	}

}
