package com.zenyte.plugins.itemonitem;

import com.zenyte.game.content.boss.kingblackdragon.KingBlackDragon;
import com.zenyte.game.content.combatachievements.combattasktiers.*;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Cresinkel
 */

public class DragonHunterCrossbowColoring implements ItemOnItemAction {

	@AllArgsConstructor
	public static enum BowPaint {

		TURQUOISE(21907, 32199),
		BLACK(7980, 32197);
		public static final BowPaint[] VALUES = values();
		public static final Int2ObjectOpenHashMap<BowPaint> MAPPED_VALUES = new Int2ObjectOpenHashMap<BowPaint>(VALUES.length);
		static {
			for (val value : VALUES) {
				MAPPED_VALUES.put(value.headId, value);
				MAPPED_VALUES.put(value.recolouredBowId, value);
			}
		}
		private final int headId;
		@Getter private final int recolouredBowId;
	}

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		val bow = from.getId() == 21012 ? from : to;
		val head = bow == from ? to : from;
		val recolor = BowPaint.MAPPED_VALUES.get(head.getId());
		if (recolor == null) {
			return;
		}
		boolean easy = EasyTasks.allEasyCombatAchievementsDone(player);
		boolean medium = MediumTasks.allMediumCombatAchievementsDone(player) && easy;
		boolean hard = HardTasks.allHardCombatAchievementsDone(player) && medium;
		boolean elite = EliteTasks.allEliteCombatAchievementsDone(player) && hard;
		if (recolor.headId == 7980 && !hard) {
			player.sendMessage("You do not have the hard, medium and easy combat achievements completed.");
		} else if (recolor.headId == 21907 && !elite) {
			player.sendMessage("You do not have the elite, hard, medium and easy combat achievements completed.");
		} else {
			player.getDialogueManager().start(new Dialogue(player) {
				@Override
				public void buildDialogue() {
					item(new Item(recolor.recolouredBowId), Colour.RED + "WARNING!" + Colour.END
							+ " changing the colour of your Dragon hunter crossbow will render it untradeable. If you revert the changes you lose the head. Are you sure?");
					options(TITLE, "Yes.", "No.").onOptionOne(() -> {
						val inventory = player.getInventory();
						inventory.deleteItem(fromSlot, from);
						inventory.deleteItem(toSlot, to);
						inventory.addItem(new Item(recolor.recolouredBowId));
						player.sendMessage("You coat your Dragon hunter crossbow in " + recolor.toString().toLowerCase().replaceAll("_", " ") + ".");
					});
				}
			});
		}
	}

	@Override
	public int[] getItems() {
		return null;
	}

	@Override
	public ItemPair[] getMatchingPairs() {
		val list = new ArrayList<ItemPair>();
		for (val head : BowPaint.VALUES) {
			list.add(new ItemPair(21012, head.headId));
		}
		return list.toArray(new ItemPair[list.size()]);
	}

}
