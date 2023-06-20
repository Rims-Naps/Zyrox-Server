package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.container.RequestResult;

import lombok.val;

/**
 * @author Kris | 25. aug 2018 : 19:16:07
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class BraceletOfEthereum extends ItemPlugin {

	public static final Item BRACELET_OF_ETHEREUM = new Item(21816);
	
	@Override
	public void handle() {
		bind("Toggle-absorption", (player, item, slotId) -> {
		    val oldValue = player.getNumericAttribute("ethereum absorption").intValue();
		    player.addAttribute("ethereum absorption", oldValue == 1 ? 0 : 1);
			player.sendMessage("Absorption on the bracelet has been " + (oldValue == 1 ? "disabled." : "enabled."));
		});
        bind("Toggle absorption", (player, item, slotId) -> {
            val oldValue = player.getNumericAttribute("ethereum absorption").intValue();
            player.addAttribute("ethereum absorption", oldValue == 1 ? 0 : 1);
            player.sendMessage("Absorption on the bracelet has been " + (oldValue == 1 ? "disabled." : "enabled."));
        });
		
		bind("Uncharge", (player, item, slotId) -> {
			val ether = item.getCharges();
			if (player.getInventory().checkSpace()) {
				if (!player.getInventory().addItem(new Item(21820, ether)).getResult().equals(RequestResult.OVERFLOW)) {
					item.setCharges(item.getCharges() - ether);
					player.sendMessage("You successfully uncharge your bracelet of ethereum.");
                    if (item.getId() == 21816) {
                        item.setId(21817);
                        player.getInventory().refresh(slotId);
                    }
				} else {
					player.sendMessage("You have too much revenant ether in your inventory.");
				}
			}
		});
		
		bind("Dismantle", (player, item, slotId) -> {
            player.getInventory().deleteItem(item);
            player.sendMessage("You successfully dismantle your bracelet of ethereum.");
            player.getInventory().addOrDrop(new Item(21820, 250));
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 21816, 21817 };
	}

}
