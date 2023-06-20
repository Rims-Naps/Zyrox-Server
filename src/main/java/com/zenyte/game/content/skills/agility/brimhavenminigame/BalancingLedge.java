package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Cresinkel
 */
public final class BalancingLedge implements Shortcut {

	private static final RenderAnimation RENDER = new RenderAnimation(RenderAnimation.STAND, 756, RenderAnimation.WALK);
	private static final RenderAnimation BACKWARDS_RENDER = new RenderAnimation(RenderAnimation.STAND, 754, RenderAnimation.WALK);
	private static final Location START_LOC_1 = new Location(2770, 9590, 3);
	private static final Location START_LOC_2 = new Location(2763, 9590, 3);
	private static final Location START_LOC_3 = new Location(2770, 9546, 3);
	private static final Location START_LOC_4 = new Location(2763, 9546, 3);
	private static final Location START_LOC_5 = new Location(2803, 9546, 3);
	private static final Location START_LOC_6 = new Location(2796, 9546, 3);

	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		if (player.getLocation().withinDistance(2805,9546, 4)) {
			return START_LOC_5;
		} else if (player.getLocation().withinDistance(2794,9546, 4)) {
			return START_LOC_6;
		} else if (player.getLocation().withinDistance(2772,9546, 4)) {
			return START_LOC_3;
		} else if (player.getLocation().withinDistance(2761,9546, 4)) {
			return START_LOC_4;
		} else if (player.getLocation().withinDistance(2772,9590, 4)) {
			return START_LOC_1;
		} else {
			return START_LOC_2;
		}
	}
	
	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		if (player.getLocation().withinDistance(2805,9546, 4)) {
			player.addWalkSteps(2796, 9546, -1, false);
			player.getAppearance().setRenderAnimation(RENDER);
		} else if (player.getLocation().withinDistance(2794,9546, 4)) {
			player.addWalkSteps(2803, 9546, -1, false);
			player.getAppearance().setRenderAnimation(BACKWARDS_RENDER);
		} else if (player.getLocation().withinDistance(2772,9546, 4)) {
			player.addWalkSteps(2763, 9546, -1, false);
			player.getAppearance().setRenderAnimation(RENDER);
		} else if (player.getLocation().withinDistance(2761,9546, 4)) {
			player.addWalkSteps(2770, 9546, -1, false);
			player.getAppearance().setRenderAnimation(BACKWARDS_RENDER);
		} else if (player.getLocation().withinDistance(2772,9590, 4)) {
			player.addWalkSteps(2763, 9590, -1, false);
			player.getAppearance().setRenderAnimation(RENDER);
		} else {
			player.addWalkSteps(2770, 9590, -1, false);
			player.getAppearance().setRenderAnimation(BACKWARDS_RENDER);
		}
	}

	@Override
	public void endSuccess(final Player player, final WorldObject object) {
		player.getAppearance().resetRenderAnimation();
	}

	@Override
	public int getLevel(final WorldObject object) {
		return 1;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] { 3559, 3561 };
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 16;
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 8;
	}


}
