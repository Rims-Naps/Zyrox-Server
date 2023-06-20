package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Cresinkel
 */

public class Stairs implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Climb")) {
            player.sendMessage("You barely escaped, it wouldn't be wise to go back there.");
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.STAIRS_32995};
    }
}
