package com.zenyte.game.content.skills.agility.alkharidrooftop;

import com.zenyte.game.content.achievementdiary.diaries.LumbridgeDiary;
import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Tommeh | 25 feb. 2018 : 22:44:58
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class Gap implements Obstacle {

	private static final Location START_LOC = new Location(3300, 3192, 3);
	private static final Location END_LOC = new Location(3299, 3194, 0);
	private static final Animation JUMP_ANIM = new Animation(2586);
	
	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		return START_LOC;
	}
	
	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		player.setAnimation(JUMP_ANIM);
		WorldTasksManager.schedule(new WorldTask() {
			int ticks;
			@Override
			public void run() {
				switch (ticks++) {
				case 0:
					player.setLocation(END_LOC);
					break;
				case 1:
					player.getAchievementDiaries().update(LumbridgeDiary.COMPLETE_ALKHARID_COURSE);
					player.setAnimation(Animation.STOP);
					MarkOfGrace.spawn(player, AlKharidRooftopCourse.MARK_LOCATIONS, 40, 20);
					player.addAttribute("Alkharid_lap_count", player.getNumericAttribute("Alkharid_lap_count").intValue() + 1);
					val laps = player.getNumericAttribute("Alkharid_lap_count").intValue();
					player.sendMessage("You have ran "+ Colour.RED.wrap(laps) + (laps == 1 ? " lap " : " laps ") + "on the Al Kharid Rooftop Course.");
					stop();
					break;
				}
			}
		}, 0, 1);
	}

	@Override
	public int getLevel(final WorldObject object) {
		return 20;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] { 10352 };
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 30;
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 1;
	}

}
