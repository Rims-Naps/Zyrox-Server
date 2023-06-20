package com.zenyte.plugins.object;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class CastlewarsOutsideStairs implements ObjectAction {

    private final static Location ZAMORAK_TOP = new Location(2383, 3133, 0);
    private final static Location ZAMORAK_BOTTOM = new Location(2382, 3130, 0);

    private final static Location SARADOMIN_TOP = new Location(2416, 3074, 0);
    private final static Location SARADOMIN_BOTTOM = new Location(2417, 3077, 0);

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        val saradomin = object.getId() == 4419;
        val top = saradomin ? player.getX() < 2417 : player.getX() > 2382;

        player.lock();
        player.faceObject(object);
        WorldTasksManager.schedule(() -> {
            player.setLocation(saradomin ? (top ? SARADOMIN_BOTTOM : SARADOMIN_TOP) : (top ? ZAMORAK_BOTTOM : ZAMORAK_TOP));
            player.unlock();
        }, 0);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { 4419, 4420 };
    }
}
