package com.zenyte.game.content.skills.smithing;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Tommeh | 25 aug. 2018 | 15:54:39
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class DragonArmourySmithing extends Action {
	
	public static final Item DRAGON_PLATEBODY = new Item(21892);
	public static final Item DRAGON_KITESHIELD = new Item(21895);
	private static final Item DRAGON_CHAINBODY = new Item(3140);
	private static final Item DRAGON_SQ_SHIELD = new Item(1187);
	private static final Item METAL_SHARD = new Item(22097);
	private static final Item METAL_SLICE = new Item(22100);
	private static final Item METAL_LUMP = new Item(22103);

	private final String item;
	private final int amount;
	private int cycle;

	private static final Item[][] MATERIALS = {
			new Item[] { DRAGON_SQ_SHIELD, METAL_SLICE, METAL_SHARD },
			new Item[] { DRAGON_CHAINBODY, METAL_LUMP, METAL_SHARD }};

	@Override
	public boolean start() {
		val level = item.equals("Kiteshield") ? 75 : 90;
		if (!player.getInventory().containsItem(Smithing.HAMMER)) {
			player.getDialogueManager().start(new PlainChat(player, "You need to have a hammer to do this."));
			return false;
		}
		if (player.getSkills().getLevel(Skills.SMITHING) < level) {
			player.getDialogueManager().start(new PlainChat(player, "You need to have a Smithing level of at least " + level + " to do this."));
			return false;
		}
		if (!player.getInventory().containsItems(MATERIALS[item.equals("Kiteshield") ? 0 : 1])) {
			player.sendMessage("You don't have all the necessary items to make the " + item.toLowerCase() + ".");
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		if (!player.getInventory().containsItems(MATERIALS[item.equals("Kiteshield") ? 0 : 1])) {
			return false;
		}
		if (cycle >= amount) {
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay() {
		val experience = item.equals("Kiteshield") ? 1000 : 2000;
		val product = item.equals("Kiteshield") ? DRAGON_KITESHIELD : DRAGON_PLATEBODY;
		player.setAnimation(Smithing.ANIMATION);
		player.getSkills().addXp(Skills.SMITHING, experience);
		for (val item : MATERIALS[item.equals("Kiteshield") ? 0 : 1]) {
			player.getInventory().deleteItem(item);
		}
		player.getInventory().addItem(product);
		cycle++;
		return 3;
	}

}