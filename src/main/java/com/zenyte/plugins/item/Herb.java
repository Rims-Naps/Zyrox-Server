package com.zenyte.plugins.item;

import com.zenyte.game.content.skills.herblore.actions.Combine;
import com.zenyte.game.content.skills.herblore.actions.Combine.HerbloreData;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 25. aug 2018 : 22:26:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class Herb extends ItemPlugin {

	@Override
	public void handle() {
		bind("Clean", Herb::clean);
	}

	public static void clean(@NotNull final Player player, @NotNull final Item herbItem, @NonNegative final int slotId) {
		val herb = HerbloreData.get(herbItem.getId());
		if (player.getSkills().getLevel(Skills.HERBLORE) < herb.getLevel()) {
			player.sendMessage("You need a Herblore level of " + herb.getLevel() + " to clean the " + herb.getMaterials()[0].getDefinitions().getName().replace("Grimy ", "") + ".");
			return;
		}
		if (herb.equals(HerbloreData.MARRENTILL)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CLEAN_MARRENTILLS);
		} else if (herb.equals(HerbloreData.RANARR_WEED)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CLEAN_RANARRS);
		} else if (herb.equals(HerbloreData.CADANTINE)) {
			player.getDailyChallengeManager().update(SkillingChallenge.CLEAN_CADANTINES);
		}
		player.getSkills().addXp(Skills.HERBLORE, herb.getXp());
		player.getInventory().replaceItem(herb.getProduct().getId(), 1, slotId);
		player.sendFilteredMessage("You clean the " + herb.toString() + ".");
	}

	@Override
	public int[] getItems() {
		val list = new IntArrayList();
		for (final HerbloreData c : Combine.HerbloreData.HERBLORE.values()) {
			if (c.ordinal() >= Combine.HerbloreData.GUAM_LEAF.ordinal() && c.ordinal() <= Combine.HerbloreData.TORSTOL.ordinal()) {
				list.add(c.getMaterials()[0].getId());
			}
		}
		return list.toArray(new int[0]);
	}

}
