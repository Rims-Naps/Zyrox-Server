package com.zenyte.game.content.skills.agility.canifisrooftop;

import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

public class PoleVault implements Obstacle {
	
	private static final Animation RUN = new Animation(1995);
	private static final Animation VAULT = new Animation(7132);
	private static final Animation LAND = new Animation(2588);
	
	private static final Location START = new Location(3478, 3484, 2);
	private static final Location FINISH = new Location(3489, 3476, 3);
	private static final Location LEDGE = new Location(3487, 3476, 2);
	
	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		player.setLocation(START);
		WorldTasksManager.schedule(new WorldTask() {

			private int ticks;
			
			@Override
			public void run() {
				if(ticks == 0) {
					player.setAnimation(RUN);
					player.setForceMovement(new ForceMovement(object, 60, ForceMovement.EAST));
				} else if(ticks == 1)
					player.setAnimation(VAULT);
				else if(ticks == 2) {
					player.setLocation(object);
					player.setForceMovement(new ForceMovement(LEDGE, 90, ForceMovement.EAST));
				} else if(ticks == 5) {
					player.setAnimation(LAND);
					player.setLocation(FINISH);
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
		return new int[] { 10831 };
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 5;
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 10;
	}
}
