package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.DoubleDoor;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Kris | 10. veebr 2018 : 22:27.12
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class DoubleDoorOA implements ObjectAction {

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		if (option.equalsIgnoreCase("Open") || option.equalsIgnoreCase("Close")) {
            DoubleDoor.handleDoubleDoor(player, object);
        }
	}

	@Override
	public Object[] getObjects() {
		return new Object[] {12349, 12350, 1727, 1728, 1568, 1569, 1521, 1524, 2143, 2144, 73, 74,
				1513, 1511, 2260, 2259, 11621, 11620, 2624, 2625, 26081, 26082, 25638, 25640, 25825, 25827, 14751, 14752,
				25813, 25814, 25748, 25750, 26130, 26131, 52, 53, 2546, 2548, 17091, 17093, 134, 135,
				30387, 30388, 1551, 1549, 2115, 2116, 21505, 21507, 21405, 21403, 10262, 10263, 10264, 10265, 3489, 3490,
				24565, 24567, 27485, 27486, 2108, 2111, 9038, 9039, 12045, 12047, 11766, 11767, 3506, 3507,
				35009, 35010, 35011, 35012,
				28456, 28457, 28458, 28459, 28460, 28461, 28462, 28463, 28464, 28465, 28466, 28467, 28468, 28469,
				28470, 28471, 33570, 33572, 12446, 12448,
				5183, 5186, 5187, 5188
		};
	}

}
