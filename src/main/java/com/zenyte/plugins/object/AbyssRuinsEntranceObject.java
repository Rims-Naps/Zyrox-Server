package com.zenyte.plugins.object;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.skills.runecrafting.Runecrafting;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.renewednpc.MonkOfEntrana;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Tommeh | 30 jul. 2018 | 11:24:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class AbyssRuinsEntranceObject implements ObjectAction {

	@Override
	public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
		val rune = Runecrafting.getRuneByAbyssEntrance(object.getId());
		if (rune == Runecrafting.LAW_RUNE) {
			if (!MonkOfEntrana.isAllowed(player)) {
				player.sendMessage("The power of Saradomin prevents you from taking armour or weaponry to Entrana.");
				return;
			}
		} else if (rune == Runecrafting.CHAOS_RUNE) {
			player.getAchievementDiaries().update(WildernessDiary.ENTER_CHAOS_RUNECRAFTING_TEMPLE);
		}
		player.setLocation(rune.getPortalCoords());
	}

	@Override
	public Object[] getObjects() {
		val list = new ArrayList<Integer>();
		for (val r : Runecrafting.VALUES) {
			if (r.getAbyssEntranceId() == -1) {
				continue;
			}
			list.add(r.getAbyssEntranceId());
		}
		return list.toArray(new Object[list.size()]);
	}

}
