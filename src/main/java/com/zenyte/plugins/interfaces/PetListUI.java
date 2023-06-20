package com.zenyte.plugins.interfaces;

import com.zenyte.game.ui.UserInterface;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.enums.EnumDefinitions;

/**
 * @author Kris | 25. veebr 2018 : 0:03.25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class PetListUI implements UserInterface {

	@Override
	public void handleComponentClick(final Player player, final int interfaceId, final int componentId, final int slotId, final int itemId, final int optionId, final String option) {
		if (componentId != 3) {
			return;
		}
		final EnumDefinitions map = EnumDefinitions.get(985);
		final int npcId = map.getIntValue(slotId);
		if (npcId == -1) {
			return;
		}
		Examine.sendItemExamine(player, itemId);
	}

	@Override
	public int[] getInterfaceIds() {
		return new int[] { 210 };
	}

}
