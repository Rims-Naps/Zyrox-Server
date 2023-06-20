package com.zenyte.plugins.object;

import org.apache.commons.lang3.ArrayUtils;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import mgi.types.config.ObjectDefinitions;

/**
 * @author Kris | 19. veebr 2018 : 17:39.34
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class StaircaseObject implements ObjectAction {

	private static final int[] LARGE_STAIRCASES = new int[] { 25604, 11499, 2118, 2120, 16647 };
	
	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		final ObjectDefinitions defs = object.getDefinitions();
		final boolean up = option.equals("Climb-up") || option.equals("Walk-up");
		final int plane = player.getPlane() + (up ? 1 : -1);
		if (up && plane > 3 || !up && plane < 0) {
			return;
		}
		if (defs.getSizeY() >= 3) {
			final int offset = defs.getSizeY() + 1;
			switch(object.getRotation()) {
			case 0:
				player.setLocation(new Location(player.getX(), player.getY() + offset, plane));
				return;
			case 1:
				player.setLocation(new Location(player.getX() + offset, player.getY(), plane));
				return;
			case 2:
				player.setLocation(new Location(player.getX(), player.getY() - offset, plane));
				return;
			case 3:
				player.setLocation(new Location(player.getX() - offset, player.getY(), plane));
				return;
			}
		} else if (defs.getSizeY() == 2) {
			final int offset = 4 + (ArrayUtils.contains(LARGE_STAIRCASES, object.getId()) ? 1 : 0);
			switch(object.getRotation()) {
			case 0:
				player.setLocation(new Location(player.getX(), player.getY() - offset, plane));
				return;
			case 1:
				player.setLocation(new Location(player.getX() - offset, player.getY(), plane));
				return;
			case 2:
				player.setLocation(new Location(player.getX(), player.getY() + offset, plane));
				return;
			case 3:
				player.setLocation(new Location(player.getX() + offset, player.getY(), plane));
				return;
			}
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 2114, 2119, 2121, 5206, 6648, 10525, 11498,
                15645, 15646, 15647, 15649, 15653, 16646, 16661,
				18991, 24672, 25786, 26106, 30139,2118, 2120, 2122, 5207, 6649,
				10526, 13650, 13651, 13652, 15648,
				15650, 15652, 15654, 16647, 16662, 16663, 18992,
				24673, 25787, 4756, 4755 };
	}

}
