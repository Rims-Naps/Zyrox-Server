package com.zenyte.plugins.object;

import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.CastleWarsTable;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class CastlewarsTableAction implements ObjectAction {


    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (!player.inArea("Castle Wars")) {
            player.sendMessage("You aren't in a castle wars game!");
            return;
        }

        if (!player.getInventory().hasFreeSlots()) {
            player.sendMessage("Not enough space in your inventory.");
        }

        final CastleWarsTable table = CastleWarsTable.getData(object.getId());
        val slotsNeeded = option.equals("Take-5") ? 5 : 1;
        player.faceObject(object);
        player.setAnimation(Animation.GRAB);
        player.getInventory().addItem(table.getLoot().getId(), Math.min(player.getInventory().getFreeSlots(), slotsNeeded));
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 4458, 4459, 4460, 4461, 4462, 4463, 4464 };
    }
}
