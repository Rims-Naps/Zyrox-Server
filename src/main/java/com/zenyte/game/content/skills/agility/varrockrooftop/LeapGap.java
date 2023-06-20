package com.zenyte.game.content.skills.agility.varrockrooftop;

import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

public class LeapGap implements Obstacle {

	private static final Animation JUMP = new Animation(2586);
	
	private static final Location START = new Location(3201, 3416, 3);
	private static final Location LAND = new Location(3197, 3416, 1);
	
	private static final Location START2 = new Location(3232, 3402, 3);
	private static final Location LAND2 = new Location(3236, 3403, 3);
	
	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		val direction = object.getId() == 10642;
		player.setFaceLocation(direction ? LAND : LAND2);
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			
			@Override
			public void run() {
				if(ticks == 0) {
					player.setAnimation(JUMP);
				} else if(ticks == 1) {
					player.setLocation(direction ? LAND : LAND2);
					MarkOfGrace.spawn(player, VarrockRooftopCourse.MARK_LOCATIONS, 40, 30);
					stop();
				}
				ticks++;
			}
		}, 0, 0);
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		if (object.getId() == 10780) {
			return 4;
		}
		return 17;
	}

	@Override
	public int getLevel(final WorldObject object) {
		return 30;
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 2;
	}
	
	@Override
	public int[] getObjectIds() {
		return new int[] { 10642, 10780 };
	}
	
	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		return object.getId() == 10642 ? START : START2;
	}

}
