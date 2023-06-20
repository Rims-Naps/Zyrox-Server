package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class GlowingFungusObjectAction implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if(player.getInventory().getFreeSlots() > 0) {
            player.sendMessage("You pull the fungus from the water. It is very cold to the touch.");
            player.getInventory().addItem(new Item(4075));
        } else {
            player.sendMessage("You don't have enough inventory space to hold the fungus.");
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {4932, 4933};
    }

}