package com.zenyte.game.content.skills.crafting.actions;

import com.zenyte.game.content.skills.crafting.CraftingDefinitions.GlassBlowingData;
import com.zenyte.game.content.vote.BoosterPerks;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Tommeh | 25 aug. 2018 | 20:43:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@RequiredArgsConstructor
public class GlassBlowingCrafting extends Action {

	private static final Animation ANIMATION = new Animation(884);

	private final GlassBlowingData data;
	private final int amount;
	private int cycle;

	@Override
	public boolean start() {
		if (player.getSkills().getLevel(Skills.CRAFTING) < data.getLevel()) {
			player.getDialogueManager().start(new PlainChat(player, "You need a Crafting level of " + data.getLevel() + " to make a " + data.getProduct().getDefinitions().getName().toLowerCase() + "."));
			return false;
		}
		if (!player.getInventory().containsItem(data.getMaterial())) {
			player.sendMessage("You need " + data.getMaterialsName() + " to make this item.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		if (!player.getInventory().containsItem(data.getMaterial())) {
			return false;
		}
		if (cycle >= amount) {
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay() {
		val product = data.getProduct().getDefinitions().getName().toLowerCase();
		val vowel = product.startsWith("a") || product.startsWith("o") || product.startsWith("u") || product.startsWith("i") || product.startsWith("e");
		player.setAnimation(ANIMATION);
		player.getInventory().deleteItemsIfContains(new Item[] { data.getMaterial() }, () -> {
			if (data.equals(GlassBlowingData.UNPOWERED_ORB)) {
				player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_UNPOWERED_ORBS);
			}
			player.getInventory().addItem(data.getProduct());
			player.getSkills().addXp(Skills.CRAFTING, data.getXp());
			player.sendFilteredMessage("You make " + (vowel ? "an " : "a ") + product + ".");
		});
		cycle++;
		return BoosterPerks.isActive(player, BoosterPerks.CRAFTING) ? (Utils.random(100) <= 5 ? 1 : 2) : 2;
	}

}
