package com.zenyte.game.content.cluescrollbooks;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Matt
 *
 */
public class EliteClueBookPlugin implements PairedItemOnItemPlugin {

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        val inventory = player.getInventory();
        player.getInventory().deleteItem(new Item(28902, 1));
        player.getInventory().deleteItem(new Item(12073, 1));
        player.getInventory().addItem(new Item(20543, 1));
        player.sendMessage("You use a Sherlock's book on the clue and receive a casket.");
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {
                ItemPair.of(12073, 28902)
        };
    }
}
