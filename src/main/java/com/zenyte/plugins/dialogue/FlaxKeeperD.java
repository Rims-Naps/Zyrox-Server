package com.zenyte.plugins.dialogue;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;

/**
 * @author Cresinkel
 */
public class FlaxKeeperD extends Dialogue {

	public FlaxKeeperD(final Player player, final NPC npc) {
		super(player, npc);
	}

	private static final DiaryReward[] DIARY_REWARDS = {
			DiaryReward.KANDARIN_HEADGEAR4, DiaryReward.KANDARIN_HEADGEAR3,
			DiaryReward.KANDARIN_HEADGEAR2, DiaryReward.KANDARIN_HEADGEAR1
	};

	@Override
	public void buildDialogue() {
		if (TreasureTrail.talk(player, npc)) {
			return;
		}
		if (!canClaim(player)) {
			npc("Hello. I'm the flax keeper. I tend to the flax fields, keeping the yield and continued flax harvest healthy.");
			player("Ooh! Great work!");
			npc("Thank you. I must get back to work. You never know when a great flax harvest will begin!");
		} else if (!player.getAttributes().containsKey("DAILY_FLAX")) {
			var maxAmount = 0;
			for (int index = 0; index < DIARY_REWARDS.length; index++) {
				val reward = DIARY_REWARDS[index];
				val amount = index == 0 ? 250 : index == 1 ? 120 : index == 2 ? 60 : 30;
				if (reward.eligibleFor(player)) {
					maxAmount = amount;
					break;
				}
			}
			val amt = maxAmount;
			npc("Hello. Do you want to exchange some flax banknotes for bowstrings? I'll exchange " + amt + " for you today, and you can come back tomorrow for more.");
			if (!player.getInventory().containsItem(1780, amt)) {
				player("I didn't actually bring " + amt +" flax banknotes with me.");
				npc("Oh. Well, if you fetch " + amt +" flax banknotes, I'll swap them for bowstrings for you.");
			} else {
				options(TITLE, "Agree.", "Decline")
						.onOptionOne(() -> {
							if (!player.getInventory().hasFreeSlots()) {
								setKey(5);
							} else {
								player.getInventory().deleteItem(1780, amt);
								player.getInventory().addOrDrop(1778, amt);
								player.getAttributes().put("DAILY_FLAX", 1);
							}
						});
				npc(5, "Um... Actually, you seem to have so much stuff that you can't hold the bowstrings.");
			}
		} else {
			npc("Hello again. I'm afraid I won't be able to exchange any more flax for bowstrings today. Try me again tomorrow.");
		}
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
