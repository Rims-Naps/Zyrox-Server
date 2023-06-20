package com.zenyte.game.content.gracefulcreation;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Matt 7/26/2021
 */
public class BluecapePlugin implements PairedItemOnItemPlugin {

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        val inventory = player.getInventory();
        inventory.deleteItemsIfContains(new Item[] { from, to }, () -> {
            inventory.addOrDrop(new Item(21064));
            player.sendMessage("You color the piece of graceful.");
        });
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {
                ItemPair.of(32300, 11852 )
        };
    }
}