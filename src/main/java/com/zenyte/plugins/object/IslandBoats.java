package com.zenyte.plugins.object;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.cutscene.FadeScreen;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;

/**
 * @author Matt
 */
public class IslandBoats implements ObjectAction {

    private static final int NORTH_BOAT = 30915;
    private static final int ISLAND_BOAT = 30919;
    private static final int ANCHOR_ROPE = 30948;

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        switch (object.getId()) {
            case NORTH_BOAT:
                new FadeScreen(player, () -> player.setLocation(new Location(3768, 3898))).fade(2);
                break;
            case ISLAND_BOAT:
                new FadeScreen(player, () -> player.setLocation(new Location(3731, 10279, 1))).fade(2);
                break;
            case ANCHOR_ROPE:
                new FadeScreen(player, () -> player.setLocation(new Location(3770, 3898))).fade(2);
                break;
        }
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{NORTH_BOAT, ISLAND_BOAT, ANCHOR_ROPE};
    }

}
