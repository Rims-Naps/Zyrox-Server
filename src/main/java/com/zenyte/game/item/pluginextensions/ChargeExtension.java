package com.zenyte.game.item.pluginextensions;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.degradableitems.ChargesManager;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.item.degradableitems.DegradeType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.ContainerWrapper;
import lombok.val;

import java.text.DecimalFormat;

/**
 * @author Kris | 25. aug 2018 : 17:05:15
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public interface ChargeExtension {

	DecimalFormat FORMATTER = ChargesManager.FORMATTER;

	void removeCharges(final Player player, final Item item, final ContainerWrapper wrapper, int slotId, final int amount);
	
	default void checkCharges(final Player player, final Item item) {
		val name = item.getName();
		if (item.getCharges() <= 0) {
			val payload = item.getName().toLowerCase().endsWith("s") ? " are completely degraded" : " is completely degraded";
			player.sendMessage("Your " + item.getName() + payload);
			return;
		}

		val deg = DegradableItem.ITEMS.get(item.getId());
		if (deg == null) {
			return;
		}
		if (deg.getType() == DegradeType.RECOIL || deg.getType() == DegradeType.USE) {
			player.sendMessage(
					"Your " + name + " has " + item.getCharges() + " charge" + (item.getCharges() == 1 ? "" : "s") + " remaining.");
			return;
		}
		val percentage = FORMATTER.format(
				item.getCharges() / (float) DegradableItem.getFullCharges(item.getId()) * 100);
		player.sendMessage(
				"Your " + name + " " + (name.contains("legs") ? "have " : "has ") + percentage.replace(".0", "") + "% charges remaining.");
	}
	
}
