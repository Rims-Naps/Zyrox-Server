package com.zenyte.game.content.area.abandonedmine.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;

public class SalveAmuletCreation implements ItemOnItemAction {
    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        player.getInventory().deleteItemsIfContains(new Item[]{new Item(ItemId.BALL_OF_WOOL), new Item(ItemId.SALVE_SHARD)}, () -> {
            player.getInventory().addItem(new Item(ItemId.SALVE_AMULET));
            player.sendMessage("You carefully string the shard of crystal.");
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {ItemId.BALL_OF_WOOL, ItemId.SALVE_SHARD };
    }

}
