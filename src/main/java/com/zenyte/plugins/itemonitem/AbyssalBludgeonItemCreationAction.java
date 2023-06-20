package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.content.achievementdiary.DiaryReward;

public class AbyssalBludgeonItemCreationAction implements ItemOnItemAction {
    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        for(int itemId : getItems()) {
            if(!player.getInventory().containsItem(itemId)) {
                player.sendMessage("You do not have all three bludgeon pieces to create the abyssal bludgeon.");
                return;
            }
        }
        //if they have all three pieces in their inventory, remove them
        for(int itemId : getItems()) {
            player.getInventory().deleteItem(new Item(itemId));
        }
        player.getInventory().addItem(new Item(ItemId.ABYSSAL_BLUDGEON));
        player.sendMessage("You create the abyssal bludgeon.");
    }

    @Override
    public int[] getItems() {
        return new int[] {ItemId.BLUDGEON_AXON, ItemId.BLUDGEON_SPINE, ItemId.BLUDGEON_CLAW};
    }
}
