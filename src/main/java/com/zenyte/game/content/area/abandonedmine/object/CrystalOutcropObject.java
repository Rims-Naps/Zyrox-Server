package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class CrystalOutcropObject implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if(option.equals("Cut")) {
            if(player.getInventory().containsItem(ItemId.CHISEL)) {
                if(player.getInventory().getFreeSlots() > 0) {
                    player.sendMessage("You cut a shard from the crystal.");
                    player.getInventory().addItem(ItemId.SALVE_SHARD, 1);
                } else {
                    player.sendMessage("You don't have enough inventory space to hold the crystal.");
                }
            } else {
                player.sendMessage("You don't have anything suitable for cutting the crystals.");
            }
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 4926, 4927, 4928 };
    }
}
