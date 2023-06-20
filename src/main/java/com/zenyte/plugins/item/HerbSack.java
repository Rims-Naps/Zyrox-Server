package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

/**
 * @author Kris | 25. aug 2018 : 18:46:36
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class HerbSack extends ItemPlugin {

	public static final Item HERB_SACK = new Item(13226);

	@Override
	public void handle() {
		bind("Fill", (player, item, slotId) -> player.getHerbSack().fill());
		bind("Check", (player, item, slotId) -> player.getHerbSack().check());
		bind("Empty", (player, item, slotId) -> player.getHerbSack().empty(player.getInventory().getContainer()));
	}

	@Override
	public int[] getItems() {
		return new int[] { HERB_SACK.getId() };
	}

}
