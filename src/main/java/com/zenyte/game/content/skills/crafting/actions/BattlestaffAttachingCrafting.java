package com.zenyte.game.content.skills.crafting.actions;

import com.zenyte.game.content.skills.crafting.CraftingDefinitions.BattlestaffAttachingData;
import com.zenyte.game.content.vote.BoosterPerks;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Tommeh | 25 aug. 2018 | 20:23:30
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@RequiredArgsConstructor
public class BattlestaffAttachingCrafting extends Action {

	private static final Animation ANIMATION = new Animation(7531);

	private final BattlestaffAttachingData data;
	private final int amount;
	private int cycle;

	@Override
	public boolean start() {
		for (val item : data.getMaterials()) {
			if (!player.getInventory().containsItem(item)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean process() {
		for (val item : data.getMaterials()) {
			if (!player.getInventory().containsItem(item)) {
				return false;
			}
		}
		if (cycle >= amount) {
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay() {
		player.setAnimation(ANIMATION);
		player.setGraphics(data.getGraphics());
		for (Item item : data.getMaterials()) {
			player.getInventory().deleteItem(item);
		}
		player.getInventory().addItem(data.getProduct());
	    player.getSkills().addXp(Skills.CRAFTING, data.getXp());
		player.sendFilteredMessage("You attach the battlestaff to the " + data.getMaterials()[0].getDefinitions().getName().toLowerCase() + ".");
		cycle++;
		return BoosterPerks.isActive(player, BoosterPerks.CRAFTING) ? (Utils.random(100) <= 5 ? 1 : 2) : 2;
	}

}
