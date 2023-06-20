package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Cresinkel
 */

public class SmallChest implements ObjectAction {
    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.equals("Check")) {
            player.sendMessage("It won't open. There's nothing here for you.");
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.SMALL_CHEST_33016};
    }
}
