
package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Matt
 */
public class CrystalEntranceHandler implements ObjectAction {

    private static final int INSIDE_DOOR =40052;
    private static final int OUTSIDE_DOOR =2236;

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        switch (object.getId()) {
            case OUTSIDE_DOOR:
                new FadeScreen(player, () -> player.setLocation(new Location(2322, 3190))).fade(3);
                break;
            case INSIDE_DOOR:
                new FadeScreen(player, () -> player.setLocation(new Location(2762, 9376))).fade(3);
                break;
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{INSIDE_DOOR, OUTSIDE_DOOR};
    }

}