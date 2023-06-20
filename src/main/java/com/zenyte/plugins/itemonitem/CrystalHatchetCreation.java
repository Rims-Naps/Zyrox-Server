package com.zenyte.plugins.itemonitem;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.ItemChat;

/**
 * @author Tommeh | 31-1-2019 | 19:37
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class CrystalHatchetCreation implements ItemOnObjectAction {

	private static final Item COINS = new Item(995, 20000000);
	private static final Item BLUEPRINT = new Item(30800);
	private static final Item SEED = new Item(30806);
	private static final Item CRYSTAL_OUTCOME = new Item(30718);
	private static final Animation finalAnim = new Animation(811);

	@Override
	public void handleItemOnObjectAction(Player player, Item item, int slot, WorldObject object) {
		if (!player.getInventory().containsItem(COINS)) {
			player.getDialogueManager().start(new ItemChat(player, COINS, "You need at least 20M coins to make a crystal hatchet."));
			return;
		}
		if (!player.getInventory().containsItem(new Item(ItemId.DRAGON_AXE))) {
			player.getDialogueManager().start(new ItemChat(player, new Item(ItemId.DRAGON_AXE), "You will need a dragon axe to make a crystal hatchet."));
			return;
		}
		if (!player.getInventory().containsItem(SEED)) {
			player.getDialogueManager().start(new ItemChat(player, SEED, "You need a enhanced crystal tool seed to make a crystal hatchet."));
			return;
		}
		if (!player.getInventory().containsItem(BLUEPRINT)) {
			player.getDialogueManager().start(new ItemChat(player, BLUEPRINT, "You will need a blueprint to know how to do this."));
			return;
		}

		if (!DiaryReward.WESTERN_BANNER4.eligibleFor(player)) {
			player.sendMessage("You must complete all of the Western Province diaries before making this crystal tool. ");
			return;
		}
		player.getDialogueManager().start(new Dialogue(player) {

			@Override
			public void buildDialogue() {
				options("Combine coins, blueprint and seed to make a crystal hatchet?", "Yes.", "No.")
						.onOptionOne(() -> {
							player.setAnimation(finalAnim);
							player.getInventory().deleteItemsIfContains(new Item[] { COINS, BLUEPRINT, SEED }, () -> player.getInventory().addItem(CRYSTAL_OUTCOME));
							player.getInventory().deleteItem(ItemId.DRAGON_AXE, 1);
							setKey(5);
						});
				item(5, CRYSTAL_OUTCOME, "You combine the coins and tool seed following the blueprint and make a Crystal hatchet.");
			}
		});
	}

	@Override
	public Object[] getItems() {
		return new Object[] { 30800, 995, 30806 };
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 40008 };
	}
}
