package com.zenyte.game.content.skills.agility.shortcut;

import com.zenyte.game.content.skills.agility.Shortcut;
import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Kris | 26. aug 2018 : 18:41:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class PiscatorisFishingColonyTunnel implements Shortcut {
	
	private static final int SOUTHERN_ENTRANCE_HASH = new Location(2344, 3651, 0).getPositionHash();
	
	private static final Animation DUCK = new Animation(2589);
	private static final Animation INVISIBLE = new Animation(2590);
	private static final Animation EMERGE = new Animation(2591);
	
	private static final Location NORTH = new Location(2344, 3655, 0);
	private static final Location SOUTH = new Location(2344, 3650, 0);
	
	private static final ForceMovement GO_NORTH = new ForceMovement(NORTH, 180, ForceMovement.NORTH);
	private static final ForceMovement GO_SOUTH = new ForceMovement(SOUTH, 180, ForceMovement.SOUTH);
	
	@Override
	public int getLevel(final WorldObject object) {
		return 0;
	}

	@Override
	public int[] getObjectIds() {
		return new int[] { 12656 };
	}

	@Override
	public int getDuration(final boolean success, final WorldObject object) {
		return 5;
	}

	@Override
	public void startSuccess(final Player player, final WorldObject object) {
		final boolean direction = object.getPositionHash() == SOUTHERN_ENTRANCE_HASH;
		
		player.setAnimation(DUCK);
		player.setForceMovement(direction ? GO_NORTH : GO_SOUTH);	
		WorldTasksManager.schedule(new WorldTask() {

			private int ticks;
			
			@Override
			public void run() {
				if(ticks == 2) {
					player.setAnimation(INVISIBLE);
				} else if (ticks == 3) {
					player.setAnimation(EMERGE);
				} else if(ticks == 4) {
					player.setLocation(direction ? NORTH : SOUTH);
					stop();
				}
				ticks++;
			}
			
		}, 0, 0);
	}

	@Override
	public double getSuccessXp(final WorldObject object) {
		return 0;
	}
	
	@Override
	public Location getRouteEvent(final Player player, final WorldObject object) {
		return object.getPositionHash() == SOUTHERN_ENTRANCE_HASH ? SOUTH : NORTH;
	}


}
