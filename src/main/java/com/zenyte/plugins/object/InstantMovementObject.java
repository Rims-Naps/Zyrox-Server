package com.zenyte.plugins.object;

import com.zenyte.game.content.achievementdiary.diaries.KaramjaDiary;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.InstantMovementObjects;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public class InstantMovementObject implements ObjectAction {

	@Override
	public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
		val entry = InstantMovementObjects.entries.get(object.getPositionHash());
		if (entry == null || entry.getId() != object.getId()) {
			return;
		}
		player.faceObject(object);
		if (entry.getAnimation() != Animation.STOP) {
			player.setAnimation(entry.getAnimation());
		}
		if (entry.equals(InstantMovementObjects.BRIMHAVEN_DUNG_LARGE_STAIRS_BOTTOM) || entry.equals(InstantMovementObjects.BRIMHAVEN_DUNG_LARGE_STAIRS_TOP)
            || entry.equals(InstantMovementObjects.BRIMHAVEN_DUNG_DEMON_STAIRS_TOP) || entry.equals(InstantMovementObjects.BRIMHAVEN_DUNG_DEMON_STAIRS_BOTTOM)) {
			player.getAchievementDiaries().update(KaramjaDiary.CLIMB_THE_STAIRS_IN_BRIMHAVEN_DUNGEON);
		}
		WorldTasksManager.schedule(() -> {
			player.setLocation(entry.getLocation());
			if (entry.getAnimation() != Animation.STOP) {
				player.setAnimation(Animation.STOP);
			}
		}, 0);
	}

	@Override
	public Object[] getObjects() {
		final List<Object> list = new ArrayList<Object>();
		for (InstantMovementObjects entry : InstantMovementObjects.VALUES)
			if (!list.contains(entry.getId()))
				list.add(entry.getId());
		return list.toArray(new Object[list.size()]);
	}

}
