package com.zenyte.plugins.item;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

/**
 * @author Kris | 25. aug 2018 : 22:43:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RunePouchItem extends ItemPlugin {

	@Override
	public void handle() {
		bind("Open", (player, item, slotId) -> {
		    player.stopAll();
			player.addTemporaryAttribute("rune_pouch", item.getId());
            GameInterface.RUNE_POUCH.open(player);
		});
		bind("Empty", (player, item, slotId) -> {
			if (item.getId() == 12791) {
				player.getRunePouch().emptyRunePouch();
			} else {
				player.getSecondaryRunePouch().emptyRunePouch();
			}
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 12791, 30006 };
	}

}
