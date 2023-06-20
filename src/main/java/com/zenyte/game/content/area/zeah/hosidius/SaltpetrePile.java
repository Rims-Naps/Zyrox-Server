package com.zenyte.game.content.area.zeah.hosidius;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.events.ServerLaunchEvent;

public class SaltpetrePile implements ObjectAction
{

    @Subscribe
    public static final void onServerLaunch(final ServerLaunchEvent event) {
        World.spawnObject(new WorldObject(ObjectId.SALTPETRE, 10, 0, new Location(1668, 3545, 0)));
        World.spawnObject(new WorldObject(ObjectId.SALTPETRE, 10, 0, new Location(1686, 3529, 0)));
        World.spawnObject(new WorldObject(ObjectId.SALTPETRE, 10, 0, new Location(1708, 3545, 0)));
        World.spawnObject(new WorldObject(ObjectId.SALTPETRE, 10, 0, new Location(1687, 3512, 0)));
        World.spawnObject(new WorldObject(ObjectId.SALTPETRE, 10, 0, new Location(1716, 3518, 0)));
    }

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option)
    {
        if(option.equals("Dig"))
        {
            if(player.getInventory().containsItem(ItemId.SPADE))
            {
                player.getActionManager().setAction(new SaltpetreDiggingAction());
            } else
            {
                player.sendMessage( "You can't dig without a spade.", MessageType.FILTERABLE);
            }
        }
    }

    @Override
    public Object[] getObjects()
    {
        return new Object[]{ ObjectId.SALTPETRE, ObjectId.SALTPETRE_27434, ObjectId.SALTPETRE_27435, ObjectId.SALTPETRE_27436 };
    }
}
