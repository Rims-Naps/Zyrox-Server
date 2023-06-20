package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.content.achievementdiary.diaries.KandarinDiary;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import javax.print.attribute.standard.Destination;

/**
 * @author Cresinkel
 */
public final class LowWall implements Shortcut {

	private static final Location WESTERN_START = new Location(2776, 9590, 3);
	private static final Location EASTERN_START = new Location(2779, 9590, 3);
	private static final Location NORTHERN_START1 = new Location(2805, 9564, 3);
	private static final Location SOUTHERN_START1 = new Location(2805, 9561, 3);
	private static final Location NORTHERN_START2 = new Location(2783, 9564, 3);
	private static final Location SOUTHERN_START2 = new Location(2783, 9561, 3);
	
	private static final Animation ANIM = new Animation(839, 15);

    @Override
	public void startSuccess(final Player player, final WorldObject object) {
		Location destination;
		if (player.getLocation().matches(WESTERN_START)) {
			destination = new Location(object.getX() + 2, object.getY(), object.getPlane());
			player.setAnimation(ANIM);
			player.setForceMovement(new ForceMovement(destination, 90, ForceMovement.EAST));
		} else if (player.getLocation().matches(EASTERN_START)) {
			destination = new Location(object.getX() - 1, object.getY(), object.getPlane());
			player.setAnimation(ANIM);
			player.setForceMovement(new ForceMovement(destination, 90, ForceMovement.WEST));
		} else if (player.getLocation().matches(NORTHERN_START1)) {
			destination = new Location(object.getX(), object.getY() - 1, object.getPlane());
			player.setAnimation(ANIM);
			player.setForceMovement(new ForceMovement(destination, 90, ForceMovement.SOUTH));
		} else if (player.getLocation().matches(SOUTHERN_START1)) {
			destination = new Location(object.getX(), object.getY() + 2, object.getPlane());
			player.setAnimation(ANIM);
			player.setForceMovement(new ForceMovement(destination, 90, ForceMovement.NORTH));
		} else if (player.getLocation().matches(NORTHERN_START2)) {
			destination = new Location(object.getX(), object.getY() - 1, object.getPlane());
			player.setAnimation(ANIM);
			player.setForceMovement(new ForceMovement(destination, 90, ForceMovement.SOUTH));
		} else {
			destination = new Location(object.getX(), object.getY() + 2, object.getPlane());
			player.setAnimation(ANIM);
			player.setForceMovement(new ForceMovement(destination, 90, ForceMovement.NORTH));
		}
		WorldTasksManager.schedule(() -> {
			player.setLocation(destination);
		}, 2);
	}

	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		if (player.getLocation().withinDistance(2805,9568, 4)) {
			return NORTHERN_START1;
		} else if (player.getLocation().withinDistance(2805,9557, 4)) {
			return SOUTHERN_START1;
		} else if (player.getLocation().withinDistance(2783,9568, 4)) {
			return NORTHERN_START2;
		} else if (player.getLocation().withinDistance(2783,9557, 4)) {
			return SOUTHERN_START2;
		} else if (player.getLocation().withinDistance(2783,9590, 4)) {
			return EASTERN_START;
		} else {
			return WESTERN_START;
		}
	}

	@Override
	public int getLevel(final WorldObject object) {
		return 1;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] { 3565 };
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 8;
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 2;
	}

}
