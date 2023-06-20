package com.zenyte.game.content.area.abandonedmine.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

public class AbandonedMineLockedDoor implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        player.sendMessage("The door seems to be locked.");
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 4963, 4964};
    }
}
