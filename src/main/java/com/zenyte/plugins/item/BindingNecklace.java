package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ChargeExtension;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.ContainerWrapper;

import lombok.val;

/**
 * @author Kris | 27. aug 2018 : 14:36:50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BindingNecklace extends ItemPlugin{

	@Override
	public void handle() {
		bind("Check", (player, item, container, slotId) -> {
			val uses = player.getNumericAttribute("binding necklace uses").intValue();
			player.sendMessage("You have " + (16 - uses) + " charges left before your Binding necklace disintegrates.");
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 5521 };
	}
}
