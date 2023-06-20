package com.zenyte.plugins.item;

import com.zenyte.game.content.consumables.Consumable;
import com.zenyte.game.content.consumables.Drinkable;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import lombok.val;

/**
 * @author Kris | 25. aug 2018 : 22:24:57
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class ConsumableItem extends ItemPlugin {

	@Override
	public void handle() {
		bind("Eat", (player, item, slotId) -> {
		    val consumable = Consumable.consumables.get(item.getId());
		    if (consumable == null) {
		        throw new RuntimeException("Consumable " + item.getId() + " is null.");
            }
            consumable.consume(player, item, slotId);
        });
        bind("Drink", (player, item, slotId) -> {
            val consumable = Consumable.consumables.get(item.getId());
            if (consumable == null) {
                throw new RuntimeException("Consumable " + item.getId() + " is null.");
            }
            consumable.consume(player, item, slotId);
        });
        bind("Empty", (player, item, slotId) -> {
            val consumable = Consumable.consumables.get(item.getId());
            if (!(consumable instanceof Drinkable)) {
                throw new RuntimeException("Consumable " + item.getId() + " is not a drink.");
            }
            val replacement = ((Drinkable) consumable).getItem(0);
            player.getInventory().set(slotId, replacement);
            player.sendMessage("You empty the " + replacement.getName().toLowerCase() + ".");
        });
	}

	@Override
	public int[] getItems() {
		return Consumable.consumables.keySet().toIntArray();
	}

}
