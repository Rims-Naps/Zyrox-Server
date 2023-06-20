package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.DoorHandler;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Kris | 10. nov 2017 : 22:01.53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class DoorObject implements ObjectAction {

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		if (option.equals("Open") || option.equals("Close")) {
			DoorHandler.handleDoor(object);
		}
	}

	@Override
	public Object[] getObjects() {
        return new Object[]{21814, 2623, 21600, "Door", 3444, 3445, "Bamboo Door", 4545, 4546, 2606, 9141, 17089, 1517};
	}
}
