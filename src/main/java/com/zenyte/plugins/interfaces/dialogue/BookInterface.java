package com.zenyte.plugins.interfaces.dialogue;

import com.zenyte.game.content.Book;
import com.zenyte.game.ui.UserInterface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Plugin;

import lombok.val;

/**
 * @author Kris | 10. juuli 2018 : 21:33:45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */

@Plugin
public final class BookInterface implements UserInterface {

	@Override
	public void handleComponentClick(final Player player, final int interfaceId, final int componentId, final int slotId, final int itemId, final int optionId, final String option) {
		val object = player.getTemporaryAttributes().get("book");
		if (object instanceof Book) {
			((Book) object).handleButtons(componentId);
			return;
		}
	}

	@Override
	public int[] getInterfaceIds() {
		return new int[] { 26, 27 };
	}

}
