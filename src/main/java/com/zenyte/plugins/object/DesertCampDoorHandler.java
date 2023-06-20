package com.zenyte.plugins.object;

import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.Door;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Matt | 29. mai 2018 : 16:43:06
 */
public final class DesertCampDoorHandler implements ObjectAction {

    private static final Location OUTSIDE = new Location(3273, 3028, 0);
    private static final Location OUTSIDE2= new Location(3273, 3029, 0);

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (player.getLocation().getPositionHash() == OUTSIDE.getPositionHash()
                && !player.getInventory().containsItem(1839, 1)) {
            player.sendMessage("You need a key to unlock the door.");
            return;
        }
        player.lock(3);
        player.addWalkSteps(object.getX(), object.getY());
        WorldTasksManager.schedule(new WorldTask() {
            private int ticks;
            private WorldObject door;
            @Override
            public void run() {
                switch(ticks++) {
                    case 0:
                        door = Door.handleGraphicalDoor(object, null);
                        return;
                    case 1:
                        if (player.getLocation().getPositionHash() != OUTSIDE.getPositionHash()) {
                            player.addWalkSteps(door.getX(), door.getY(), 1, false);
                        } else {
                            player.addWalkSteps(object.getX(), object.getY(), 1, false);
                        }
                        return;
                    case 3:
                        Door.handleGraphicalDoor(door, object);
                        stop();
                        return;
                }
            }

        }, 0, 0);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 2674, 2673 };
    }

}
