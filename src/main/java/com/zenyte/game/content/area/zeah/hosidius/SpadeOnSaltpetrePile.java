package com.zenyte.game.content.area.zeah.hosidius;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

public class SpadeOnSaltpetrePile implements ItemOnObjectAction
{
    @Override
    public void handleItemOnObjectAction(Player player, Item item, int slot, WorldObject object)
    {
        player.getActionManager().setAction(new SaltpetreDiggingAction());
    }

    @Override
    public Object[] getItems()
    {
        return new Object[] { ItemId.SPADE };
    }

    @Override
    public Object[] getObjects()
    {
        return new Object[] { ObjectId.SALTPETRE, ObjectId.SALTPETRE_27434, ObjectId.SALTPETRE_27435, ObjectId.SALTPETRE_27436 };
    }
}
