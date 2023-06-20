package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.Gate;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Kris | 12. dets 2017 : 17:52.57
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class GateObject implements ObjectAction {

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		Gate.handleGate(object);
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 47, 48, 166, 167, 883, 1558, 1559, 1560, 1561, 1562, 1563, 1564, 1567,
				1730, 1731, 2050, 2051, 2438, 2439, 3725, 3726, 3727, 3728,
				4311, 4312, 8810, 8811, 8812, 8813, 9470, 9708, 12816, 12817, 12818,
				12986, 12987, 12988, 12989, 15510, 15511, 15512, 15513, 15514, 15515, 15516,
				15517, 23917, 23918, 23919, 24560, 24561 };
	}

}
