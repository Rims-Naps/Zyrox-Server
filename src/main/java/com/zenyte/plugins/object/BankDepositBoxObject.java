package com.zenyte.plugins.object;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Kris | 10. nov 2017 : 22:19.57
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class BankDepositBoxObject implements ObjectAction {

	private static final Location[] DEPOSIT_BOXES = {
			new Location(2902,3119,0),
			new Location(3655,3229,0),
			new Location(1767,3847,0),
			new Location(2524,3768,0),
			new Location(2595,4316,0),
			new Location(1817,3772,0),
			new Location(3478,3416,0),
	};

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		if (!WellOfGoodwill.DEPOBOX) {
			for (int i = DEPOSIT_BOXES.length - 1; i > -1; i--) {
				if (object.getPosition().matches(DEPOSIT_BOXES[i])) {
					player.sendMessage("The community can get access to this deposit box via the well of goodwill.");
					return;
				}
			}
		}
        GameInterface.BANK_DEPOSIT_INTERFACE.open(player);
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { "Bank deposit box", "Bank Deposit Chest", "Deposit Box", "Bank Deposit Pot"};
	}

}
