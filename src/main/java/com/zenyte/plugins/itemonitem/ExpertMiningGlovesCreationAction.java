package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

public class ExpertMiningGlovesCreationAction implements ItemOnItemAction {
    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        for(val item : getItems()) {
            if(!player.containsItem(item)) {
                player.sendMessage("You do not have the required items to make Expert Mining Gloves.");
                return;
            }
        }
        val inventory = player.getInventory();
        inventory.deleteItem(from);
        inventory.deleteItem(to);
        inventory.addItem(new Item(ItemId.EXPERT_MINING_GLOVES));
        player.getCollectionLog().add(new Item((ItemId.EXPERT_MINING_GLOVES)));
    }

    @Override
    public int[] getItems() {
        return new int[] {ItemId.MINING_GLOVES, ItemId.SUPERIOR_MINING_GLOVES};
    }
}
