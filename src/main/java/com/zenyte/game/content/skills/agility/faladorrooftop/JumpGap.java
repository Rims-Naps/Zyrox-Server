/**
 * 
 */
package com.zenyte.game.content.skills.agility.faladorrooftop;

import com.zenyte.game.content.skills.agility.MarkOfGrace;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Noele | May 1, 2018 : 12:26:53 AM
 * @see https://noeles.life || noele@zenyte.com
 */
public class JumpGap implements Obstacle {
	
	private static final Location FIRST_START = new Location(3048, 3358, 3);
	private static final Location FIRST_FINISH = new Location(3048, 3361, 3);
	
	private static final Location SECOND_START = new Location(3045, 3361, 3);
	private static final Location SECOND_FINISH = new Location(3041, 3361, 3);

	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		val first = object.getId() == 11161;
		player.setFaceLocation(first ? FIRST_FINISH : SECOND_FINISH);
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			
			@Override
			public void run() {
				if(ticks == 0) {
					player.setAnimation(Animation.JUMP);
					player.setForceMovement(new ForceMovement(player.getLocation(), 15, first ? FIRST_FINISH : SECOND_FINISH, 35, first ? ForceMovement.NORTH : ForceMovement.WEST));
				} else if(ticks == 1) {
					player.setLocation(first ? FIRST_FINISH : SECOND_FINISH);
					MarkOfGrace.spawn(player, FaladorRooftopCourse.MARK_LOCATIONS, 50, 50);
					stop();
				}
				ticks++;
			}	
		}, 0, 0);
	}
	
	@Override
	public int getLevel(final WorldObject object) {
		return 50;
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 2;
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 20;
	}
	
	@Override
	public int[] getObjectIds() {
		return new int[] { 11360, 11161 };
	}
	
	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		return object.getId() == 11161 ? FIRST_START : SECOND_START;
	}
}
