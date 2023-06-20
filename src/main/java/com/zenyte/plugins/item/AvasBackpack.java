package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;

import lombok.val;

/**
 * @author Kris | 25. aug 2018 : 21:52:36
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class AvasBackpack extends ItemPlugin {

	@Override
	public void handle() {
		bind("Commune", (player, item, slotId) -> {
			val map = player.getAttributes();
			if (map.containsKey("avasDeviceRetrieve")) {
				map.remove("avasDeviceRetrieve");
				player.sendMessage("You are no longer collecting random metal items.");
			} else {
				map.put("avasDeviceRetrieve", true);
				player.sendMessage("You are now collecting random metal items.");
			}
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 10498, 10499, 22109, 13337, 21898 };
	}

}
