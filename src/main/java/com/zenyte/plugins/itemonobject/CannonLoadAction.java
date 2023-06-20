package com.zenyte.plugins.itemonobject;

import com.zenyte.game.content.multicannon.DwarfMulticannon;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Christopher
 * @since 1/23/2020
 */
public class CannonLoadAction implements ItemOnObjectAction  {

    @Override
    public void handleItemOnObjectAction(Player player, Item item, int slot, WorldObject object) {
        if (object.equals(DwarfMulticannon.placedCannons.get(player.getUsername()))) {
            player.getDwarfMulticannon().loadCannon();
        } else {
            player.sendMessage("This is not your cannon.");
        }
    }

    @Override
    public Object[] getItems() {
        return new Object[] {ItemId.GRANITE_CANNONBALL, ItemId.CANNONBALL};
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 6, 7, 8, 9, 14916 };
    }

}
