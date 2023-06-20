package com.zenyte.plugins.itemonnpc.maxcape;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.ItemChat;

/**
 * @author Tommeh | 31-1-2019 | 19:37
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class farmingcape implements ItemOnObjectAction {

	private static final Item CAPE = new Item(9811);
	private static final Item NEWCAPE = new Item(32049);
	private static final Animation finalAnim = new Animation(811);

	@Override
	public void handleItemOnObjectAction(Player player, Item item, int slot, WorldObject object) {
		if (!player.getInventory().containsItem(CAPE)) {
			player.getDialogueManager().start(new ItemChat(player, CAPE, "You will need a 99 skillcape (t) in order to do this."));
			return;

		}

		if (player.getSkills().getExperience(Skills.FARMING) < 200000000) {
			player.sendMessage("You will need at least 200m xp in this skill in order to make a 200m skillcape.");
			return;
		}
		player.getDialogueManager().start(new Dialogue(player) {

			@Override
			public void buildDialogue() {
				options("Would you like to transfer your cape into a 200m skillcape?", "Yes.", "No.")
						.onOptionOne(() -> {
							player.setAnimation(finalAnim);
							player.getInventory().deleteItemsIfContains(new Item[] { CAPE}, () -> player.getInventory().addItem(NEWCAPE));
							setKey(5);
						});
				item(5, NEWCAPE, "You are able to obtain a 200m skillcape because of your commitment toward the skill.");
			}
		});
	}

	@Override
	public Object[] getItems() {
		return new Object[] { 9811, 32049};
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 40035 };
	}
}

