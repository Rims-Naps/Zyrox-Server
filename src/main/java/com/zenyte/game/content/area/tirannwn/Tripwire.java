package com.zenyte.game.content.area.tirannwn;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.ForceMovement;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

public class Tripwire implements ObjectAction
{
    private static final Animation ANIM = new Animation(6132);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option)
    {

        Direction direction = ((object.getRotation() & 0x1) != 0) ? (player.getX() > object.getX()) ? Direction.WEST : Direction.EAST : (player.getY() > object.getY()) ? Direction.SOUTH : Direction.NORTH;
        player.stopAll();
        player.lock(2);
        Location toLocation = null;
        switch(direction)
        {
            case NORTH:
                toLocation = new Location(player.getX(), player.getY() + 3);
                break;
            case SOUTH:
                toLocation = new Location(player.getX(), player.getY() - 3);
                break;
            case EAST:
                toLocation = new Location(player.getX() + 3, player.getY());
                break;
            case WEST:
                toLocation = new Location(player.getX() - 3, player.getY());
                break;
        }
        player.setForceMovement(new ForceMovement(player.getLocation(), 33, toLocation, 60, direction.getDirection()));
        player.setLocation(toLocation);
        player.setAnimation(ANIM);
        player.sendMessage("You jump over the " + object.getName().toLowerCase() + ".", MessageType.FILTERABLE);
    }

    @Override
    public Object[] getObjects()
    {
        return new Object[] {ObjectId.TRIPWIRE};
    }
}
