package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;


/**
 * @author Cresinkel
 */
public final class Ropeswing implements Shortcut {

	private static final Location START_LOC_1 = new Location(2806, 9587, 3);
	private static final Location START_LOC_2 = new Location(2804, 9582, 3);
	private static final Location START_LOC_3 = new Location(2769, 9567, 3);
	private static final Location START_LOC_4 = new Location(2764, 9569, 3);
	private static final Animation SWINGING_ANIM = new Animation(751);
	private static final Animation ROPE_ANIM = new Animation(497);
	private static final ForceMovement FORCE_MOVEMENT_1 = new ForceMovement(new Location(2806, 9587, 3), 30, new Location(2806, 9582, 3), 60, ForceMovement.SOUTH);
	private static final ForceMovement FORCE_MOVEMENT_2 = new ForceMovement(new Location(2804, 9582, 3), 30, new Location(2804, 9587, 3), 60, ForceMovement.NORTH);
	private static final ForceMovement FORCE_MOVEMENT_3 = new ForceMovement(new Location(2769, 9567, 3), 30, new Location(2764, 9567, 3), 60, ForceMovement.WEST);
	private static final ForceMovement FORCE_MOVEMENT_4 = new ForceMovement(new Location(2764, 9569, 3), 30, new Location(2769, 9569, 3), 60, ForceMovement.EAST);


	@Override
	public void startSuccess(final Player player, WorldObject object) {
		ForceMovement forcemovement;
		if (player.getLocation().withinDistance(2805,9589, 4)) {
			object = World.getObjectWithId(new Location(2806, 9585,3), 3566);
			forcemovement = FORCE_MOVEMENT_1;
		} else if (player.getLocation().withinDistance(2805,9580, 4)){
			object = World.getObjectWithId(new Location(2804, 9584,3), 3566);
			forcemovement = FORCE_MOVEMENT_2;
		} else if (player.getLocation().withinDistance(2771,9568, 4)){
			object = World.getObjectWithId(new Location(2767, 9567,3), 3566);
			forcemovement = FORCE_MOVEMENT_3;
		} else {
			object = World.getObjectWithId(new Location(2766, 9569,3), 3566);
			forcemovement = FORCE_MOVEMENT_4;
		}
		player.setAnimation(SWINGING_ANIM);
		World.sendObjectAnimation(object, ROPE_ANIM);
		player.setFaceLocation(forcemovement.getToFirstTile());
		player.setForceMovement(forcemovement);
		WorldTasksManager.schedule(() -> player.setLocation(forcemovement.getToSecondTile()), 1);
	}
	
	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		if (player.getLocation().withinDistance(2805,9589, 4)) {
			return START_LOC_1;
		} else if (player.getLocation().withinDistance(2805,9580, 4)){
			return START_LOC_2;
		} else if (player.getLocation().withinDistance(2771,9568, 4)){
			return START_LOC_3;
		} else {
			return START_LOC_4;
		}
	}

	@Override
	public int getLevel(final WorldObject object) {
		return 1;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] { 3566 };
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 20;
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 2;
	}

}
