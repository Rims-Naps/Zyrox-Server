package com.zenyte.game.content.bloodfury;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Matt, redone by Cresinkel
 *
 */
public class BloodFuryCharging implements PairedItemOnItemPlugin {

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        val inventory = player.getInventory();
        inventory.deleteItemsIfContains(new Item[] { from, to }, () -> {
            inventory.addOrDrop(new Item(24788));
            player.sendMessage("You combine the blood shard and the amulet of blood fury together to provide more charges");
        });
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {
                ItemPair.of(24780, 24777), ItemPair.of(24782, 24777),
                ItemPair.of(24784, 24777), ItemPair.of(24786, 24777)
        };
    }
}
