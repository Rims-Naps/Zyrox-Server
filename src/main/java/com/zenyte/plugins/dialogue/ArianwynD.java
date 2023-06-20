package com.zenyte.plugins.dialogue;

import com.zenyte.game.content.RespawnPoint;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Cresinkel
 */
public class ArianwynD extends Dialogue {

	public ArianwynD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("Good day friend, how can I help you?");
		options(TITLE, "Can I buy a Crystal crown please?", "Cancel.").onOptionOne(() -> setKey(5));
		player(5, "Can I buy a Crystal crown please?");
		npc("Sure, that would cost you 250m coins though.");
		options(TITLE, "Okay, that sounds reasonable.", "That is way too expensive.")
				.onOptionOne(() -> setKey(10))
				.onOptionTwo(() -> setKey(15));
		player(10, "Okay, that sounds reasonable.").executeAction(() -> {
			if (player.getInventory().containsItem(ItemId.COINS_995, 250000000)) {
				player.getInventory().deleteItem(ItemId.COINS_995, 250000000);
				player.getInventory().addOrDrop(ItemId.CRYSTAL_CROWN);
				setKey(20);
			} else {
				setKey(25);
			}
		});
		player(15, "That is way too expensive.").executeAction(() -> setKey(30));
		npc(20, "Thank you for your purchase!");
		npc(25, "You do not have 250m coins in your inventory, come back when you do.");
		npc(30, "Okay, be the little cheapskate if you want to be.");
	}

}
