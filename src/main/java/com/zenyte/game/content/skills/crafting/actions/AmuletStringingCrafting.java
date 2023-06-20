package com.zenyte.game.content.skills.crafting.actions;

import com.zenyte.game.content.achievementdiary.diaries.LumbridgeDiary;
import com.zenyte.game.content.skills.crafting.CraftingDefinitions;
import com.zenyte.game.content.skills.crafting.CraftingDefinitions.AmuletStringingData;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;

import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Tommeh | 27 mei 2018 | 00:30:39
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class AmuletStringingCrafting extends Action {

	private final AmuletStringingData data;
	private final int amount;
	private int cycle;


	@Override
	public boolean start() {
		if (!player.getInventory().containsItem(CraftingDefinitions.BALL_OF_WOOL)) {
			player.sendMessage("You need a ball of wool to string the amulet.");
			return false;
		}
		if (!player.getInventory().containsItem(data.getMaterials()[0])) {
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		if (!player.getInventory().containsItems(data.getMaterials())) {
			return false;
		}
		if (cycle >= amount) {
			return false;
		}

		return true;
	}

	@Override
	public int processWithDelay() {
		if (data.equals(AmuletStringingData.DIAMOND_AMULET)) {
			player.getAchievementDiaries().update(LumbridgeDiary.CRAFT_AMULET_OF_POWER, 0x2);
		}
		for (val item : data.getMaterials()) {
			player.getInventory().deleteItem(item);
		}
		player.getInventory().addItem(data.getProduct());
		player.getSkills().addXp(Skills.CRAFTING, 4);
		player.sendFilteredMessage("You string the amulet.");
		cycle++;
		return 2;
	}

}
