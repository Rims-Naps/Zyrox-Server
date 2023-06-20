/**
 * 
 */
package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Cresinkel
 */
public class HandHolds implements Shortcut {

	private static final Animation JUMP = new Animation(2583);
	private static final Animation HANG = new Animation(1118);
	private static final Animation LAND = new Animation(1120);
	
	private static final Location[] HOLDS = {
			new Location(2792, 9592, 3),
			new Location(2791, 9592, 3),
			new Location(2790, 9592, 3),
			new Location(2789, 9592, 3),
			new Location(2788, 9592, 3),
			new Location(2787, 9592, 3),
			new Location(2786, 9592, 3),
			new Location(2785, 9592, 3),
	};

	private static final Location[] HOLDS2 = {
			new Location(2759, 9566, 3),
			new Location(2759, 9565, 3),
			new Location(2759, 9564, 3),
			new Location(2759, 9563, 3),
			new Location(2759, 9562, 3),
			new Location(2759, 9561, 3),
			new Location(2759, 9560, 3),
			new Location(2759, 9559, 3),
	};

	private static final Location[] HOLDS3 = {
			new Location(2792, 9544, 3),
			new Location(2791, 9544, 3),
			new Location(2790, 9544, 3),
			new Location(2789, 9544, 3),
			new Location(2788, 9544, 3),
			new Location(2787, 9544, 3),
			new Location(2786, 9544, 3),
			new Location(2785, 9544, 3),
	};

	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		if (player.getLocation().withinDistance(HOLDS[0], 1)) {
			WorldTasksManager.schedule(new WorldTask() {
				private int ticks;

				@Override
				public void run() {
					if (ticks == 0) {
						player.faceDirection(Direction.NORTH);
						player.setAnimation(JUMP);
					} else if (ticks == 1) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[0]);
					} else if (ticks == 2) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[1], 20, ForceMovement.NORTH));
					} else if (ticks == 3) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[1]);
					} else if (ticks == 4) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[2], 20, ForceMovement.NORTH));
					} else if (ticks == 5) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[2]);
					} else if (ticks == 6) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[3], 20, ForceMovement.NORTH));
					} else if (ticks == 7) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[3]);
					} else if (ticks == 8) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[4], 20, ForceMovement.NORTH));
					} else if (ticks == 9) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[4]);
					} else if (ticks == 10) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[5], 20, ForceMovement.NORTH));
					} else if (ticks == 11) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[5]);
					} else if (ticks == 12) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[6], 20, ForceMovement.NORTH));
					} else if (ticks == 13) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[6]);
					} else if (ticks == 14) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[7], 20, ForceMovement.NORTH));
					} else if (ticks == 15) {
						player.setLocation(HOLDS[7]);
					} else if (ticks == 16) {
						player.setAnimation(LAND);
						stop();
					}
					ticks++;
				}
			}, 0, 0);
		}
		else if (player.getLocation().withinDistance(HOLDS[7], 1)) {
			WorldTasksManager.schedule(new WorldTask() {
				private int ticks;

				@Override
				public void run() {
					if (ticks == 0) {
						player.faceDirection(Direction.NORTH);
						player.setAnimation(JUMP);
					} else if (ticks == 1) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[7]);
					} else if (ticks == 2) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[6], 20, ForceMovement.NORTH));
					} else if (ticks == 3) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[6]);
					} else if (ticks == 4) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[5], 20, ForceMovement.NORTH));
					} else if (ticks == 5) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[5]);
					} else if (ticks == 6) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[4], 20, ForceMovement.NORTH));
					} else if (ticks == 7) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[4]);
					} else if (ticks == 8) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[3], 20, ForceMovement.NORTH));
					} else if (ticks == 9) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[3]);
					} else if (ticks == 10) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[2], 20, ForceMovement.NORTH));
					} else if (ticks == 11) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[2]);
					} else if (ticks == 12) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[1], 20, ForceMovement.NORTH));
					} else if (ticks == 13) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS[1]);
					} else if (ticks == 14) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS[0], 20, ForceMovement.NORTH));
					} else if (ticks == 15) {
						player.setLocation(HOLDS[0]);
					} else if (ticks == 16) {
						player.setAnimation(LAND);
						stop();
					}
					ticks++;
				}
			}, 0, 0);
		} else if (player.getLocation().withinDistance(HOLDS2[0], 1)) {
			WorldTasksManager.schedule(new WorldTask() {
				private int ticks;

				@Override
				public void run() {
					if (ticks == 0) {
						player.faceDirection(Direction.WEST);
						player.setAnimation(JUMP);
					} else if (ticks == 1) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[0]);
					} else if (ticks == 2) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[1], 20, ForceMovement.WEST));
					} else if (ticks == 3) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[1]);
					} else if (ticks == 4) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[2], 20, ForceMovement.WEST));
					} else if (ticks == 5) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[2]);
					} else if (ticks == 6) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[3], 20, ForceMovement.WEST));
					} else if (ticks == 7) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[3]);
					} else if (ticks == 8) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[4], 20, ForceMovement.WEST));
					} else if (ticks == 9) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[4]);
					} else if (ticks == 10) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[5], 20, ForceMovement.WEST));
					} else if (ticks == 11) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[5]);
					} else if (ticks == 12) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[6], 20, ForceMovement.WEST));
					} else if (ticks == 13) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[6]);
					} else if (ticks == 14) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[7], 20, ForceMovement.WEST));
					} else if (ticks == 15) {
						player.setLocation(HOLDS2[7]);
					} else if (ticks == 16) {
						player.setAnimation(LAND);
						stop();
					}
					ticks++;
				}
			}, 0, 0);
		}
		else if (player.getLocation().withinDistance(HOLDS2[7], 1)) {
			WorldTasksManager.schedule(new WorldTask() {
				private int ticks;

				@Override
				public void run() {
					if (ticks == 0) {
						player.faceDirection(Direction.WEST);
						player.setAnimation(JUMP);
					} else if (ticks == 1) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[7]);
					} else if (ticks == 2) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[6], 20, ForceMovement.WEST));
					} else if (ticks == 3) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[6]);
					} else if (ticks == 4) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[5], 20, ForceMovement.WEST));
					} else if (ticks == 5) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[5]);
					} else if (ticks == 6) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[4], 20, ForceMovement.WEST));
					} else if (ticks == 7) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[4]);
					} else if (ticks == 8) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[3], 20, ForceMovement.WEST));
					} else if (ticks == 9) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[3]);
					} else if (ticks == 10) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[2], 20, ForceMovement.WEST));
					} else if (ticks == 11) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[2]);
					} else if (ticks == 12) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[1], 20, ForceMovement.WEST));
					} else if (ticks == 13) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS2[1]);
					} else if (ticks == 14) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS2[0], 20, ForceMovement.WEST));
					} else if (ticks == 15) {
						player.setLocation(HOLDS2[0]);
					} else if (ticks == 16) {
						player.setAnimation(LAND);
						stop();
					}
					ticks++;
				}
			}, 0, 0);
		} else if (player.getLocation().withinDistance(HOLDS3[0], 1)) {
			WorldTasksManager.schedule(new WorldTask() {
				private int ticks;

				@Override
				public void run() {
					if (ticks == 0) {
						player.faceDirection(Direction.SOUTH);
						player.setAnimation(JUMP);
					} else if (ticks == 1) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[0]);
					} else if (ticks == 2) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[1], 20, ForceMovement.SOUTH));
					} else if (ticks == 3) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[1]);
					} else if (ticks == 4) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[2], 20, ForceMovement.SOUTH));
					} else if (ticks == 5) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[2]);
					} else if (ticks == 6) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[3], 20, ForceMovement.SOUTH));
					} else if (ticks == 7) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[3]);
					} else if (ticks == 8) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[4], 20, ForceMovement.SOUTH));
					} else if (ticks == 9) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[4]);
					} else if (ticks == 10) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[5], 20, ForceMovement.SOUTH));
					} else if (ticks == 11) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[5]);
					} else if (ticks == 12) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[6], 20, ForceMovement.SOUTH));
					} else if (ticks == 13) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[6]);
					} else if (ticks == 14) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[7], 20, ForceMovement.SOUTH));
					} else if (ticks == 15) {
						player.setLocation(HOLDS3[7]);
					} else if (ticks == 16) {
						player.setAnimation(LAND);
						stop();
					}
					ticks++;
				}
			}, 0, 0);
		}
		else if (player.getLocation().withinDistance(HOLDS3[7], 1)) {
			WorldTasksManager.schedule(new WorldTask() {
				private int ticks;

				@Override
				public void run() {
					if (ticks == 0) {
						player.faceDirection(Direction.SOUTH);
						player.setAnimation(JUMP);
					} else if (ticks == 1) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[7]);
					} else if (ticks == 2) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[6], 20, ForceMovement.SOUTH));
					} else if (ticks == 3) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[6]);
					} else if (ticks == 4) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[5], 20, ForceMovement.SOUTH));
					} else if (ticks == 5) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[5]);
					} else if (ticks == 6) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[4], 20, ForceMovement.SOUTH));
					} else if (ticks == 7) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[4]);
					} else if (ticks == 8) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[3], 20, ForceMovement.SOUTH));
					} else if (ticks == 9) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[3]);
					} else if (ticks == 10) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[2], 20, ForceMovement.SOUTH));
					} else if (ticks == 11) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[2]);
					} else if (ticks == 12) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[1], 20, ForceMovement.SOUTH));
					} else if (ticks == 13) {
						player.setAnimation(HANG);
						player.setLocation(HOLDS3[1]);
					} else if (ticks == 14) {
						player.setForceMovement(new ForceMovement(player.getLocation(), 10, HOLDS3[0], 20, ForceMovement.SOUTH));
					} else if (ticks == 15) {
						player.setLocation(HOLDS3[0]);
					} else if (ticks == 16) {
						player.setAnimation(LAND);
						stop();
					}
					ticks++;
				}
			}, 0, 0);
		}
	}
	@Override
	public int getLevel(final WorldObject object) {
		return 20;
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 16;
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 22;
	}
	
	@Override
	public int[] getObjectIds() {
		return new int[] { 3583 };
	}
}
