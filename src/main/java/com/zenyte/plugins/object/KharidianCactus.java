package com.zenyte.plugins.object;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

public class KharidianCactus implements ObjectAction {
    private static final Animation slash = new Animation(911);
    private static final int[] waterskins = {ItemId.WATERSKIN0, ItemId.WATERSKIN1, ItemId.WATERSKIN2, ItemId.WATERSKIN3};
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if(option.equalsIgnoreCase("cut")) {
            val inventory = player.getInventory();
            if(inventory.containsItem(ItemId.KNIFE) && inventory.containsAnyOf(waterskins)) {
                for(val waterskinId : waterskins) {
                    int slot = inventory.getContainer().getSlotOf(waterskinId);
                    if(slot != -1) {
                        player.lock(2);
                        player.setAnimation(slash);
                        inventory.deleteItem(waterskinId, 1);
                        inventory.addItem(new Item(ItemId.WATERSKIN4));
                        player.sendMessage("You slice the cactus to refill your waterskin.");
                        return;
                    }
                }
            } else {
                player.sendMessage("You do not have a knife to cut the cactus or a waterskin that needs filling.");
            }
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 2670 };
    }
}
