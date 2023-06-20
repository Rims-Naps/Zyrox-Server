package com.zenyte.game.content.skills.agility.canifisrooftop;

import com.zenyte.game.content.achievementdiary.diaries.MorytaniaDiary;
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
 * @author Noele | Apr 29, 2018 : 9:50:21 AM
 * @see https://noeles.life || noele@zenyte.com
 */
public class JumpGap implements Obstacle {
	
	private static final Animation JUMP = new Animation(2586);
	private static final Animation LAND = new Animation(2588);

	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		val gap = GapInfo.get(object.getId());
		if (gap == null) {
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			
			@Override
			public void run() {
				if(ticks == 0) {
					player.setAnimation(JUMP);
				} else if(ticks == 1) {
					if (gap.equals(GapInfo.FIFTH)) {
						player.getAchievementDiaries().update(MorytaniaDiary.COMPLETE_CANIFIS_COURSE_LAP);
						player.addAttribute("Canifis_lap_count", player.getNumericAttribute("Canifis_lap_count").intValue() + 1);
						val laps = player.getNumericAttribute("Canifis_lap_count").intValue();
						player.sendMessage("You have ran "+ Colour.RED.wrap(laps) + (laps == 1 ? " lap " : " laps ") + "on the Canifis Rooftop Course.");
					}
					player.setAnimation(LAND);
					player.setLocation(gap.getFinish());
					MarkOfGrace.spawn(player, CanifisRooftopCourse.MARK_LOCATIONS, 40, 40);
					stop();
				}
				ticks++;
			}
		}, 0, 0);
	}
	
	@Override
	public int getLevel(final WorldObject object) {
		return 40;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] { 10820, 10821, 10822, 10823, 10832 };
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 2;
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		if(object.getId() == 10832)
			return 175;
		return object.getId() == 10823 ? 11 : 8;
	}
	
	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		return GapInfo.get(object.getId()) == null ? object : GapInfo.get(object.getId()).getStart();
	}
	
}
