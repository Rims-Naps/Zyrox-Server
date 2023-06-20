package com.zenyte.game.content.area.strongholdofsecurity;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.ItemChat;
import com.zenyte.plugins.dialogue.PlainChat;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Kris | 5. sept 2018 : 01:49:55
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class SceptreCreation implements PairedItemOnItemPlugin {

	private static final int TOP_OF_SCEPTRE = 9010;
	private static final int BOTTOM_OF_SCEPTRE = 9011;
	private static final int RIGHT_SKULL_HALF = 9007;
	private static final int LEFT_SKULL_HALF = 9008;
	private static final int RUNED_SCEPTRE = 9012;
	private static final int STRANGE_SKULL = 9009;
	
	private static final int SKULL_SCEPTRE = 9013;
	private static final int IMBUED_SKULL_SCEPTRE = 21276;
	
	private static final ItemPair SCEPTRE_HALVES = new ItemPair(TOP_OF_SCEPTRE, BOTTOM_OF_SCEPTRE);
	private static final ItemPair SKULL_HALVES = new ItemPair(RIGHT_SKULL_HALF, LEFT_SKULL_HALF);
	private static final ItemPair SCEPTRE_SKULL_HALVES = new ItemPair(RUNED_SCEPTRE, STRANGE_SKULL);
	
	private static final ItemPair[] RECHARGE_PAIRS = new ItemPair[] {
			new ItemPair(IMBUED_SKULL_SCEPTRE, TOP_OF_SCEPTRE),
			new ItemPair(IMBUED_SKULL_SCEPTRE, BOTTOM_OF_SCEPTRE),
			new ItemPair(IMBUED_SKULL_SCEPTRE, RIGHT_SKULL_HALF),
			new ItemPair(IMBUED_SKULL_SCEPTRE, LEFT_SKULL_HALF),
			new ItemPair(IMBUED_SKULL_SCEPTRE, RUNED_SCEPTRE),
			new ItemPair(IMBUED_SKULL_SCEPTRE, STRANGE_SKULL)
	};
	
	private final ItemPair[] concatenatedPairs = concatenate(RECHARGE_PAIRS, SCEPTRE_HALVES, SKULL_HALVES, SCEPTRE_SKULL_HALVES);

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		val inventory = player.getInventory();
		val pair = getMatchingPair(from, to, concatenatedPairs);
		val maxCharges = DiaryReward.VARROCK_ARMOUR4.eligibleFor(player) ? 13
				: DiaryReward.VARROCK_ARMOUR3.eligibleFor(player) ? 11
				: DiaryReward.VARROCK_ARMOUR2.eligibleFor(player) ? 9
				: DiaryReward.VARROCK_ARMOUR1.eligibleFor(player) ? 7
				: 5;

		if (ArrayUtils.contains(RECHARGE_PAIRS, pair)) {
			val fromId = from.getId();
			val sceptre = fromId == IMBUED_SKULL_SCEPTRE ? from : to;

			if (sceptre.getCharges() >= maxCharges) {
				player.getDialogueManager().start(new PlainChat(player, "Your Skull Sceptre can't contain any more charges."));
				return;
			}
			val piece = sceptre == from ? to : from;
			inventory.deleteItem(piece);
			val pieceId = piece.getId();
			val chargesToAdd = pieceId == RUNED_SCEPTRE || pieceId == STRANGE_SKULL ? 6 : 3;
			sceptre.setCharges(Math.min(maxCharges, sceptre.getCharges() + chargesToAdd));
			player.getDialogueManager().start(new ItemChat(player, piece, "You charge the sceptre with a small sceptre piece."));
			return;
		}
		
		inventory.deleteItem(from);
		inventory.deleteItem(to);
		if (pair == SCEPTRE_HALVES) {
			val runedSceptre = new Item(RUNED_SCEPTRE);
			inventory.addItem(runedSceptre);
			player.getDialogueManager().start(new ItemChat(player, runedSceptre,
					"The two halves of the Sceptre fit perfectly. The Sceptre<br>appears to be designed to have something on top."));
		} else if (pair == SKULL_HALVES) {
			val strangeSkull = new Item(STRANGE_SKULL);
			inventory.addItem(strangeSkull);
			player.getDialogueManager().start(new ItemChat(player, strangeSkull,
					"The two halves of the skull fit perfectly, they appear to<br>have a fixing point, perhaps they are to be mounted on<br>something?"));
		} else if (pair == SCEPTRE_SKULL_HALVES) {
			val sceptre = new Item(SKULL_SCEPTRE);
			if (player.containsItem(sceptre) || player.containsItem(new Item(IMBUED_SKULL_SCEPTRE))) {
				player.sendMessage("You can only create one Skull Sceptre at a time.");
				return;
			}
			sceptre.setCharges(5);
			inventory.addItem(sceptre);
			player.getDialogueManager().start(new ItemChat(player, sceptre,
					"The skull fits perfectly atop the Sceptre. You feel there<br>is great magical power at work here, and that the<br>sceptre has 5 charges."));
		}
	}

	@Override
	public ItemPair[] getMatchingPairs() {
		val pairs = new ArrayList<ItemPair>();
		pairs.add(SCEPTRE_HALVES);
		pairs.add(SKULL_HALVES);
		pairs.add(SCEPTRE_SKULL_HALVES);
        pairs.addAll(Arrays.asList(RECHARGE_PAIRS));
		return pairs.toArray(new ItemPair[pairs.size()]);
	}

}
