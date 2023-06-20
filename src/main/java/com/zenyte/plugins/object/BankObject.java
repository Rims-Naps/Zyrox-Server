package com.zenyte.plugins.object;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public final class BankObject implements ObjectAction {

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		if (option.equals("Bank") || option.equals("Use")) {
            GameInterface.BANK.open(player);
		} else if (option.equalsIgnoreCase("Collect")) {
            GameInterface.GRAND_EXCHANGE_COLLECTION_BOX.open(player);
        } else if (option.equalsIgnoreCase("Presets")) {
			GameInterface.PRESET_MANAGER.open(player);
		}
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { "Bank", "Bank booth", "Bank chest", "Open chest", "Bank counter", "Bank Chest-wreck" };
	}

}
