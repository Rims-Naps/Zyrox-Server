package com.zenyte.plugins.dialogue.magebank;

import com.zenyte.game.constants.GameConstants;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;


/**
 * @author Cresinkel
 */
public class LundailD extends Dialogue {

	private static final DiaryReward[] DIARY_REWARDS = {
			DiaryReward.WILDERNESS_SWORD4, DiaryReward.WILDERNESS_SWORD3,
			DiaryReward.WILDERNESS_SWORD2, DiaryReward.WILDERNESS_SWORD1
	};

	private static final int[] RUNES = {ItemId.AIR_RUNE, ItemId.WATER_RUNE, ItemId.FIRE_RUNE, ItemId.EARTH_RUNE, ItemId.MIND_RUNE,
			ItemId.BODY_RUNE, ItemId.NATURE_RUNE, ItemId.CHAOS_RUNE, ItemId.LAW_RUNE, ItemId.COSMIC_RUNE, ItemId.DEATH_RUNE};

	public LundailD(Player player, NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		if (player.getAppearance().isMale()) {
			npc("Hello Sir.");
		} else {
			npc("Hello Miss");
		}
		npc("How can I help you, brave adventurer?");
		options(TITLE, "What are you selling?", "What's that big old building above us?", "Claim free runes.")
				.onOptionOne(() -> setKey(5))
				.onOptionTwo(() -> setKey(10))
				.onOptionThree(() -> {
					if (!canClaim(player)) {
						player.sendMessage("You need to complete at least the easy Wilderness diaries to get free runes from Lundail.");
						return;
					}
					if (player.getAttributes().containsKey("DAILY_LUNDAIL_RUNES")) {
						setKey(20);
					} else {
						setKey(15);
					}

				});
		player(5, "What are you selling?");
		npc("I sell rune stones. I've got some good stuff, some really<br><br>powerful little rocks. Take a look.").executeAction(() -> player.openShop("Lundail's Arena-side Rune Shop"));
		player(10, "What that big old building above us?");
		npc("That, my friend, is the mage battle arena. Top mages<br><br>come from all over " + GameConstants.SERVER_NAME + " to compete in the arena.");
		player("Wow.");
		npc("Few return, most get fried, hence the smell.");
		player("Hmmm.. I did notice.");
		npc(15, "There you go, free runes. Return tomorrow for some more.").executeAction(() -> {
			var maxAmount = 0;
			for (int index = 0; index < DIARY_REWARDS.length; index++) {
				val reward = DIARY_REWARDS[index];
				val amount = index == 0 ? 50 : index == 1 ? 30 : index == 2 ? 20 : 10;
				if (reward.eligibleFor(player)) {
					maxAmount = amount;
					break;
				}
			}
			val amt = maxAmount;
			for (int i = amt; i > 0; --i) {
				val index = Utils.random(10);
				val runeid = RUNES[index];
				player.getInventory().addOrDrop(runeid);
			}
			player.getAttributes().put("DAILY_LUNDAIL_RUNES", 1);
		});
		npc(20, "I've already given you your free allowance for today. Come back to me tomorrow for some more.");
	}

	private boolean canClaim(final Player player) {
		for (val reward : DIARY_REWARDS) {
			if (reward.eligibleFor(player)) {
				return true;
			}
		}
		return false;
	}
}
