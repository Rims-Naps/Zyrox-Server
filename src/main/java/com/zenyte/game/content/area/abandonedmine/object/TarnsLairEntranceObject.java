package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class TarnsLairEntranceObject implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.lock(1);
        if(object.getId() == 20482) {
            WorldTasksManager.schedule(() -> player.setLocation(new Location(3424, 9660, 0)));
        } else {
            WorldTasksManager.schedule(() -> player.setLocation(new Location(3166, 4547, 0)));
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {20822, 20482};
    }
}
