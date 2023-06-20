package com.zenyte.plugins.dialogue;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Cresinkel
 */
public class ApprenticeFelixD extends Dialogue {

	public ApprenticeFelixD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("Good day friend, how can I help you?");
		options(TITLE, "Can I view your shop please?", "Can you make master runecrafting outfits?", "Cancel.")
				.onOptionOne(() -> setKey(5))
				.onOptionTwo(() -> setKey(25));
		player(5, "Can I view your shop please?");
		npc("Sure.").executeAction(() -> {
			player.openShop("Apprentice Felix's Shop");
		});
		player(25,"Can you make master runecrafting outfits?");
		npc("I sure can, lets see if you have any of the pieces.").executeAction(() -> {
			int count = 0;
			if (player.getInventory().containsItem(32212) && player.getInventory().containsItem(32196)) {
				count += 1;
			}
			if (player.getInventory().containsItem(32214) && player.getInventory().containsItem(32204)) {
				count += 1;
			}
			if (player.getInventory().containsItem(32216) && player.getInventory().containsItem(32206)) {
				count += 1;
			}
			if (player.getInventory().containsItem(32218) && player.getInventory().containsItem(32208)) {
				count += 1;
			}
			if (player.getInventory().containsItem(32220) && player.getInventory().containsItem(32210)) {
				count += 1;
			}
			if (count == 0) {
				setKey(30);
			} else {
				setKey(35);
			}
		});
		npc(30, "You do not seem to have any pairing pieces of the runecrafting skilling outfits.");
		npc(35, "I can make you master runecrafting skilling outfit pieces, but this is not reversable. Are you sure?");
		options(TITLE, "Yes.", "No.").onOptionOne(() -> setKey(40));
		npc(40, "One second please.").executeAction(() -> {
			if (player.getInventory().containsItem(32212) && player.getInventory().containsItem(32196)) {
				player.getInventory().deleteItems(new Item(32212), new Item(32196));
				player.getInventory().addOrDrop(32222);
			}
			if (player.getInventory().containsItem(32214) && player.getInventory().containsItem(32204)) {
				player.getInventory().deleteItems(new Item(32214), new Item(32204));
				player.getInventory().addOrDrop(32224);
			}
			if (player.getInventory().containsItem(32216) && player.getInventory().containsItem(32206)) {
				player.getInventory().deleteItems(new Item(32216), new Item(32206));
				player.getInventory().addOrDrop(32226);
			}
			if (player.getInventory().containsItem(32218) && player.getInventory().containsItem(32208)) {
				player.getInventory().deleteItems(new Item(32218), new Item(32208));
				player.getInventory().addOrDrop(32228);
			}
			if (player.getInventory().containsItem(32220) && player.getInventory().containsItem(32210)) {
				player.getInventory().deleteItems(new Item(32220), new Item(32210));
				player.getInventory().addOrDrop(32230);
			}
		});

	}

}
