package com.zenyte.plugins.itemonobject;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.skills.runecrafting.CombinationRunecrafting;
import com.zenyte.game.content.skills.runecrafting.Runecrafting;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Kris | 11. nov 2017 : 0:57.54
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 */
public final class TalismanItemOnRunecraftingRuinsAction implements ItemOnObjectAction {

	@Override
	public void handleItemOnObjectAction(final Player player, final Item item, int slot, final WorldObject object) {
		val rune = Runecrafting.getRuneByRuinsObject(object.getId());
		if (rune == null) {
			return;
		}
		if (item.getId() == rune.getTalismanId()) {
			if (rune.equals(Runecrafting.CHAOS_RUNE)) {
				player.getAchievementDiaries().update(WildernessDiary.ENTER_CHAOS_RUNECRAFTING_TEMPLE);
			}
			player.setLocation(rune.getPortalCoords());
		} else {
			player.sendMessage("Nothing interesting happens.");
		}
	}

	@Override
	public Object[] getItems() {
		val list = new ObjectOpenHashSet<Object>();
		for (int i = 1438; i <= 1456; i += 2) {
			list.add(i);
		}
		list.add(5525);
		list.add(22118);
		for (val e : Runecrafting.VALUES) {
		    list.add(e.getTalismanId());
        }
		for (val crune : CombinationRunecrafting.VALUES) {
			list.add(crune.getRequiredRuneId());
		}
		list.remove(-1);
		return list.toArray(new Object[list.size()]);
	}

	@Override
	public Object[] getObjects() {
		val list = new ArrayList<Object>();
		for (val r : Runecrafting.VALUES) {
			if (r.getRuinsObjectId() == -1) {
				continue;
			}
			list.add(r.getRuinsObjectId());
		}
		return list.toArray(new Object[list.size()]);
	}

}
