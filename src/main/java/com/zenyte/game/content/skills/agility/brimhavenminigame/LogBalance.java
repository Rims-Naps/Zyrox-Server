package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Cresinkel
 */
public final class LogBalance implements Shortcut {

	private static final RenderAnimation RENDER = new RenderAnimation(RenderAnimation.STAND, 762, RenderAnimation.WALK);
	private static final Location START_LOC_1 = new Location(2794, 9588, 3);
	private static final Location START_LOC_2 = new Location(2794, 9581, 3);
	private static final Location START_LOC_3 = new Location(2770, 9579, 3);
	private static final Location START_LOC_4 = new Location(2763, 9579, 3);
	private static final Location START_LOC_5 = new Location(2805, 9555, 3);
	private static final Location START_LOC_6 = new Location(2805, 9548, 3);

	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		if (player.getLocation().withinDistance(2805,9557, 4)) {
			return START_LOC_5;
		} else if (player.getLocation().withinDistance(2805,9546, 4)) {
			return START_LOC_6;
		} else if (player.getLocation().withinDistance(2794,9590, 4)) {
			return START_LOC_1;
		} else if (player.getLocation().withinDistance(2794,9579, 4)) {
			return START_LOC_2;
		} else if (player.getLocation().withinDistance(2772,9579, 4)) {
			return START_LOC_3;
		} else {
			return START_LOC_4;
		}
	}
	
	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		if (player.getLocation().withinDistance(2805,9557, 4)) {
			player.addWalkSteps(2805, 9548, -1, false);
		} else if (player.getLocation().withinDistance(2805,9546, 4)) {
			player.addWalkSteps(2805, 9555, -1, false);
		} else if (player.getLocation().withinDistance(2794,9590, 4)) {
			player.addWalkSteps(2794, 9581, -1, false);
		} else if (player.getLocation().withinDistance(2794,9579, 4)) {
			player.addWalkSteps(2794, 9588, -1, false);
		} else if (player.getLocation().withinDistance(2772,9579, 4)) {
			player.addWalkSteps(2763, 9579, -1, false);
		} else {
			player.addWalkSteps(2770, 9579, -1, false);
		}
	}

	@Override
	public int getLevel(final WorldObject object) {
		return 1;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] { 3557, 3553 };
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 12;
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 7;
	}
	
	@Override
	public RenderAnimation getRenderAnimation() {
		return RENDER;
	}

}
