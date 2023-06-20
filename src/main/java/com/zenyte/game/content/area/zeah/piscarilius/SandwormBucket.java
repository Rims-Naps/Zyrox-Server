package com.zenyte.game.content.area.zeah.piscarilius;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;

public class SandwormBucket extends ItemPlugin
{

    @Override
    public void handle()
    {
        bind("Take-grubs", (player, item, container, slotId) -> {
            if(player.getInventory().getFreeSlots() > 0 || player.getInventory().containsItem(ItemId.SANDWORMS))
            {
                player.getInventory().deleteItem(ItemId.BUCKET_OF_SANDWORMS, 1);
                player.getInventory().addItem(ItemId.BUCKET, 1);
                player.getInventory().addItem(ItemId.SANDWORMS, Utils.random(1, 5));
            }
            else
            {
                player.sendMessage("You don't have enough free space!");
            }
        });
    }

    @Override
    public int[] getItems()
    {
        return new int[] {ItemId.BUCKET_OF_SANDWORMS};
    }
}
