package com.zenyte.game.content.skills.crafting.actions;

import com.zenyte.game.content.achievementdiary.diaries.FremennikDiary;
import com.zenyte.game.content.achievementdiary.diaries.LumbridgeDiary;
import com.zenyte.game.content.skills.crafting.CraftingDefinitions.JewelleryData;
import com.zenyte.game.content.treasuretrails.clues.SherlockTask;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Tommeh | 25 aug. 2018 | 20:47:51
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@RequiredArgsConstructor
public class JewelleryCrafting extends Action {

	private static final Animation ANIMATION = new Animation(3243);

	private JewelleryData data;
	private final int amount;
	private int cycle, ticks;

	public JewelleryCrafting(final JewelleryData data, final int amount) {
		this.data = data;
		this.amount = amount;
	}

	@Override
	public boolean start() {
		if (data.equals(JewelleryData.SLAYER_RING) && player.getInventory().containsItem(JewelleryData.ETERNAL_SLAYER_RING.getMaterials()[0])) {
			data = JewelleryData.ETERNAL_SLAYER_RING;
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) < data.getLevel()) {
			player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
			player.getDialogueManager().start(new PlainChat(player, "You need at least level " + data.getLevel() + " Crafting to make that."));
			return false;
		}
		for (val item : data.getMaterials()) {
			if (!player.getInventory().containsItem(item))
				return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		for (val item : data.getMaterials()) {
			if (!player.getInventory().containsItem(item))
				return false;
		}
		return cycle < amount;
	}

	@Override
	public int processWithDelay() {
		if (ticks == 0) {
			player.getInterfaceHandler().closeInterface(InterfacePosition.CENTRAL);
			player.setAnimation(ANIMATION);
		} else if (ticks == 2) {
			player.getInventory().deleteItemsIfContains(data.getMaterials(), () -> {
				if (data.equals(JewelleryData.DIAMOND_AMULET)) {
					player.getAchievementDiaries().update(LumbridgeDiary.CRAFT_AMULET_OF_POWER, 0x1);
				} else if (data.equals(JewelleryData.TIARA)) {
					player.getAchievementDiaries().update(FremennikDiary.CRAFT_A_TIARA, 0x4);
				} else if (data.equals(JewelleryData.DRAGONSTONE_AMULET)) {
					player.getAchievementDiaries().update(FremennikDiary.CREATE_A_DRAGONSTONE_AMULET);
					player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_DRAGONSTONE_AMULETS);
					SherlockTask.CREATE_UNSTRUNG_DRAGONSTONE_AMULET.progress(player);
				} else if (data.equals(JewelleryData.GOLD_BRACELET)) {
					player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_GOLD_BRACELETS);
				} else if (data.equals(JewelleryData.TOPAZ_AMULET)) {
					player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_TOPAZ_AMULETS);
				} else if (data.equals(JewelleryData.SAPPHIRE_NECKLACE)) {
					player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_SAPPHIRE_NECKLACES);
				} else if (data.equals(JewelleryData.OPAL_BRACELET)) {
					player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_OPAL_BRACELETS);
				} else if (data.equals(JewelleryData.EMERALD_RING)) {
					player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_EMERALD_RINGS);
				} else if (data.equals(JewelleryData.DIAMOND_RING)) {
					player.getDailyChallengeManager().update(SkillingChallenge.CRAFT_DIAMOND_RINGS);
				}
				player.getInventory().addItem(data.getProduct());
				player.getSkills().addXp(Skills.CRAFTING, data.getXp());
				cycle++;
			});
			return ticks = 0;
		}
		ticks++;
		return 0;
	}

}
