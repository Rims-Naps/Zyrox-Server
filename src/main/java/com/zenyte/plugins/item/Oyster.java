package com.zenyte.plugins.item;

import com.zenyte.game.content.drops.table.DropTable;
import com.zenyte.game.content.drops.table.impl.ImmutableDropTable;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

/**
 * @author Corey
 */
public class Oyster extends ItemPlugin {
    
    public static final ImmutableDropTable OYSTER_TABLE = new DropTable()
                                                                  .append(ItemId.OYSTER_PEARL, 76)
                                                                  .append(ItemId.EMPTY_OYSTER, 23)
                                                                  .append(ItemId.OYSTER_PEARLS, 1)
                                                                  .toImmutable().build();
    
    @Override
    public void handle() {
        bind("Open", (player, item, slotId) -> {
            player.sendFilteredMessage("You open the oyster shell.");
            player.getInventory().deleteItem(new Item(ItemId.OYSTER));
            player.getInventory().addItem(OYSTER_TABLE.rollItem());
        });
    }
    
    @Override
    public int[] getItems() {
        return new int[]{ItemId.OYSTER};
    }
}
