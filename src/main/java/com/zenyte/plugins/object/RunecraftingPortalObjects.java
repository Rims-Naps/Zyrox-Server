package com.zenyte.plugins.object;

import java.util.ArrayList;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.content.skills.runecrafting.Runecrafting;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

import lombok.val;

/**
 * @author Tommeh | 3 okt. 2018 | 21:50:40
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public final class RunecraftingPortalObjects implements ObjectAction {

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		val rune = Runecrafting.getRuneByPortalObject(object.getId());
		if (rune == null) {
			return;
		}
		player.setLocation(rune.getRuinsCoords());
	}

    @Override
    public Object[] getObjects() {
       val list = new ArrayList<Object>();
       	for (val r : Runecrafting.VALUES) {
       		if (r.getPortalObjectId() == -1) {
       			continue;
       		}
       		list.add(r.getPortalObjectId());
       	}
		return list.toArray(new Object[list.size()]);
	}

}
