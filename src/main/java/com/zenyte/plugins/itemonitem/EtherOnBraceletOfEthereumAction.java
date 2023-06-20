package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;

import lombok.val;

/**
 * @author Tommeh | 9 aug. 2018 | 15:15:27
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class EtherOnBraceletOfEthereumAction implements ItemOnItemAction {
	
	private static final Item REVENANT_ETHER = new Item(21820);

	@Override
	public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
		if ((from.getId() == 21816 || to.getId() >= 21817) && from.getId() != 21820 && to.getId() != 21820) {
			return;
		}
		val bracelet = from.getId() != REVENANT_ETHER.getId() ? from : to;
		val charges = bracelet.getCharges();
		val ether = player.getInventory().getAmountOf(REVENANT_ETHER.getId());
		int toCharge = ether;
		if (charges + ether > 16000) {
			toCharge = 16000 - bracelet.getCharges();
			if (toCharge <= 0) {
			    player.sendMessage("Your bracelet is already fully charged.");
			    return;
            }
			player.getInventory().deleteItem(REVENANT_ETHER.getId(), toCharge);
			player.sendMessage("You charge the braclet with " + toCharge + " charges.");
		} else {
			player.getInventory().deleteItem(REVENANT_ETHER.getId(), ether);
			player.sendMessage("You charge the bracelet with " + ether + " charges.");
		}
		bracelet.setCharges(bracelet.getCharges() + toCharge);
		if (bracelet.getId() == 21817) {
		    bracelet.setId(21816);
		    player.getInventory().refresh(fromSlot, toSlot);
        }
	}

	@Override
	public int[] getItems() {
		return new int[] { 21816, 21817, 21820 };
	}

}
