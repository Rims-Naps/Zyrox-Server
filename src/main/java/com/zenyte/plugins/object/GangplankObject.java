package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Tommeh | 7 jul. 2018 | 22:41:50
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GangplankObject implements ObjectAction {

	@Override
	public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
		if (option.equals("Cross")) {
			Location location = null;
			final int plane = player.getPlane() == 0 ? 1 : 0;
			if (object.getRotation() == 0 || object.getRotation() == 2) {
				if (player.getX() > object.getX()) {
					location = new Location(player.getX() - 3, player.getY(), plane);
				} else {
					location = new Location(player.getX() + 3, player.getY(), plane);
				}
			} else if (object.getRotation() == 1 || object.getRotation() == 3) {
				if (player.getY() > object.getY()) {
					location = new Location(player.getX(), player.getY() - 3, plane);
				} else {
					location = new Location(player.getX(), player.getY() + 3, plane);
				}
			}
			player.setLocation(location);
			player.sendMessage("You cross the gangplank.");
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 2081, 2082, 2083, 2084, 2085, 2086, 2087, 2088, 2412, 2413, 2414, 2415, 14304, 14305, 14306, 14307, 17392, 17393, 17394, 17395, 17396, 17397, 17398,
                17399, 17400, 17401, 17402, 17403, 17404, 17405, 17406, 17407, 17408, 17409, 27777, 27778, 27779, 27780, 29723, 29724, 31756, 34672 };
	}

}
