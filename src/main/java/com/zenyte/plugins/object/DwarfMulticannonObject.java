package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Kris | 10. nov 2017 : 22:17.17
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class DwarfMulticannonObject implements ObjectAction {

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		if (!player.getDwarfMulticannon().handleCannon(object, optionId)) {
			player.sendMessage("This is not your cannon.");
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 6, 7, 8, 9, 14916 };
	}

}
