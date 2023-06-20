package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public final class PickFlowerObject implements ObjectAction {

	@Override
	public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
		if(option.toLowerCase().equals("take-seed")) {
			player.sendMessage("There doesn't seem to be any seeds on this rosebush.");
			return;
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 9260, 9261, 9262 };
	}

}
