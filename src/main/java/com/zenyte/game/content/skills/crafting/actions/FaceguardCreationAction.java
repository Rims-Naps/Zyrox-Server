package com.zenyte.game.content.skills.crafting.actions;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.plugins.dialogue.ItemChat;
import com.zenyte.plugins.dialogue.PlainChat;

/**
 * @author Tommeh | 22 jul. 2018 | 00:12:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class FaceguardCreationAction extends Action {

	private static final Item HELM_OF_NEITIZNOT = new Item(10828);
	private static final Item BASILISK_JAW = new Item(30538);
	private static final Item NEITIZNOT_FACEGUARD = new Item(30536);
	private static final Animation ANIMATION = new Animation(898);

	private int ticks;
	
	@Override
	public boolean start() {
		if (!player.getInventory().containsItem(2347)) {
			player.getDialogueManager().start(new PlainChat(player, "You need to have a hammer to do this."));
			return false;
		}
		if (!player.getInventory().containsItem(HELM_OF_NEITIZNOT)) {
			player.getDialogueManager().start(new PlainChat(player, "You need to have a Helm of Neitiznot to attach the jaw to."));
			return false;
		}
		if (!player.getInventory().containsItem(BASILISK_JAW)) {
			player.getDialogueManager().start(new PlainChat(player, "You need to have a Basilisk Jaw so it can be attached onto the Helmet."));
			return false;
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) < 40) {
			player.getDialogueManager().start(new PlainChat(player, "You need to have a Smithing level of at least 40 to do this."));
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		return true;
	}

	@Override
	public int processWithDelay() {
		switch (ticks++) {
		case 0:
			player.setAnimation(ANIMATION);
			player.getDialogueManager().start(new ItemChat(player, BASILISK_JAW, "You set to work trying to attach the Basilisk Jaw to the Helm of Neitiznot."));
			break;
		case 5:
			player.setAnimation(ANIMATION);
			break;
		case 9:
			player.getDialogueManager().start(new ItemChat(player, NEITIZNOT_FACEGUARD, "Even for an expert armourer it is not an easy task,<br>but eventually it is ready. You have crafted the<br>draconic visage and anti-dragonbreath shield into a<br>dragonfire shield."));
			player.getInventory().deleteItem(BASILISK_JAW);
			player.getInventory().deleteItem(HELM_OF_NEITIZNOT);
			player.getInventory().addItem(NEITIZNOT_FACEGUARD);
			player.getSkills().addXp(Skills.CRAFTING, 2000);
			return -1;
		}
		return 0;
	}

	@Override
	public boolean interruptedByDialogue() {
		return false;
	}

}
