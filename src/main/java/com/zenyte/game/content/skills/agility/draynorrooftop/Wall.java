package com.zenyte.game.content.skills.agility.draynorrooftop;

import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Tommeh | 25 feb. 2018 : 22:44:58
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class Wall implements Obstacle {

	private static final Location START_LOC = new Location(3088, 3257, 3);
	private static final Animation FIRST_ANIM = new Animation(2583);
	private static final Animation SECOND_ANIM = new Animation(2585);
	
	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		return START_LOC;
	}
	
	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		player.setAnimation(FIRST_ANIM);
		player.setForceMovement(new ForceMovement(new Location(player.getX(), player.getY() - 1, player.getPlane()), 30, new Location(player.getX(), player.getY() - 1, player.getPlane()), 35, ForceMovement.SOUTH));
		WorldTasksManager.schedule(new WorldTask() {
			int ticks;
			@Override
			public void run() {
				switch (ticks++) {
				case 0:
					player.setLocation(new Location(3088, 3256, player.getPlane()));
					player.setAnimation(SECOND_ANIM);
					break;
				case 1:
					player.setLocation(new Location(3088, 3255, player.getPlane()));
					stop();
					break;
				}
			}
		}, 0, 1);
	}

	@Override
	public int getLevel(final WorldObject object) {
		return 10;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] { 10084 };
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 10;
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 3;
	}
	


}
