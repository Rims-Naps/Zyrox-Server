package com.zenyte.game.content.area.zeah.piscarilius;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

public class SandwormHole implements ObjectAction
{
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option)
    {
        if(option.equals("Dig"))
        {
            if(player.getInventory().containsItem(ItemId.SPADE))
            {
                player.getActionManager().setAction(new SandwormDiggingAction());
            } else
            {
                player.sendMessage( "You can't dig without a spade.", MessageType.FILTERABLE);
            }
        }
    }

    @Override
    public Object[] getObjects()
    {
        return new Object[] { ObjectId.SANDWORM_CASTINGS };
    }
}
