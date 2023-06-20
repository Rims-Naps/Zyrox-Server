package com.zenyte.plugins.item;

import com.zenyte.Constants;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

/**
 * @author Cresinkel
 */
public class GamblingToken extends ItemPlugin {

	@Override
	public void handle() {
		bind("Redeem", (player, item, slotId) -> {
			if (!Constants.isOwner(player)) {
				player.sendMessage("Coming soon...");
				return;
			}
			player.getWheelOfFortune().setSpins(player.getWheelOfFortune().getSpins() + 1);
			player.getInventory().deleteItem(ItemId.SURVIVAL_TOKEN, 1);
		});
		bind("Open Wheel", (player, item, slotId) -> {
			if (!Constants.isOwner(player)) {
				player.sendMessage("Coming soon...");
				return;
			}
			GameInterface.WHEEL_OF_FORTUNE.open(player);
		});
		bind("Drop table", (player, item, slotId) -> {
			if (!Constants.isOwner(player)) {
				player.sendMessage("Coming soon...");
				return;
			}
			player.getPacketDispatcher().sendURL("https://zenyte.com/community/");
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { ItemId.SURVIVAL_TOKEN };
	}

}
