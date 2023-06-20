package com.zenyte.game.content.chambersofxeric.plugins.object;

import com.zenyte.game.content.chambersofxeric.skills.GourdPicking;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectHandler;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 06/07/2019 04:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class GourdTreeObject implements ObjectAction {
    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        player.getRaid().ifPresent(raid -> player.getActionManager().setAction(new GourdPicking(option.equalsIgnoreCase("Pick"))));
    }

    @Override
    public Runnable getRunnable(final Player player, final WorldObject object, final String name, final int optionId,
                         final String option) {
        return () -> {
            val existingObject = ObjectHandler.verifyObject(object, object.getId());
            if (existingObject == null || player.getPlane() != object.getPlane()) {
                return;
            }
            player.stopAll();
            player.faceObject(object);
            if (!ObjectHandler.handleOptionClick(player, optionId, object)) {
                return;
            }
            handleObjectAction(player, object, name, optionId, option);
        };
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {
                29772
        };
    }
}
