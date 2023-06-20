package com.zenyte.plugins.dialogue;

import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.Expression;

/**
 * @author Cresinkel
 */
public class FaladorBarD extends Dialogue {

	public FaladorBarD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		if (TreasureTrail.talk(player, npc)) {
			return;
		}
		npc("Heya! What can I get you?");
		player("What ales are you serving?");
		npc("Well, we've got Asgarnian Ale, Wizard's Mind Bomb and Dwarven Stout, all for 3 coins.");
		options(TITLE, "One Asgarnian Ale, please.", "I'll try the Mind Bomb.", "Can I have a Dwarven Stout?", "I don't feel like any of those.")
				.onOptionOne(() -> {
					if (player.getInventory().getAmountOf(ItemId.COINS_995) >= 3) {
						setKey(5);
					} else {
						setKey(25);
					}})
				.onOptionTwo(() -> {
					if (player.getInventory().getAmountOf(ItemId.COINS_995) >= 3) {
						setKey(10);
					} else {
						setKey(25);
					}})
				.onOptionThree(() -> {
					if (player.getInventory().getAmountOf(ItemId.COINS_995) >= 3) {
						setKey(15);
					} else {
						setKey(25);
					}})
				.onOptionFour(() -> setKey(20));

		player(5, "One Asgarnian Ale, please.").executeAction(() -> {
			player.getInventory().deleteItem(ItemId.COINS_995, 3);
			player.getInventory().addOrDrop(ItemId.ASGARNIAN_ALE);
		});
		player("Thanks " + npc.getName(player));

		player(10, "I'll try the Mind Bomb.").executeAction(() -> {
			player.getInventory().deleteItem(ItemId.COINS_995, 3);
			player.getInventory().addOrDrop(ItemId.WIZARDS_MIND_BOMB);
		});
		player("Thanks " + npc.getName(player));

		player(15, "Can I have a Dwarven Stout?").executeAction(() -> {
			player.getInventory().deleteItem(ItemId.COINS_995, 3);
			player.getInventory().addOrDrop(ItemId.DWARVEN_STOUT);
		});
		player("Thanks " + npc.getName(player));

		player(20, "I don't feel like any of those.");

		npc(25, "I said 3 coins! You haven't got 3 coins!", Expression.MAD);
	}

}
