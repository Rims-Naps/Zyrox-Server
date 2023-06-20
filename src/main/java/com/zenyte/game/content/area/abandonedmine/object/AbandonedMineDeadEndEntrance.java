package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class AbandonedMineDeadEndEntrance implements ObjectAction {

    private static final Location ENTRANCE = new Location(2791, 4592, 0);
    private static final Location EXIT = new Location(3454, 3242, 0);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.lock(2);
        WorldTasksManager.schedule(() -> player.setLocation(player.inArea("Abandoned Mine") ? EXIT : ENTRANCE));
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {4923, 4919};
    }
}
