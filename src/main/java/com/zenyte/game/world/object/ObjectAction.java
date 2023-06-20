package com.zenyte.game.world.object;

import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 10. nov 2017 : 21:38.45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public interface ObjectAction {

	void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option);
	
	Object[] getObjects();
	
	default int getDelay() {
		return 0;
	}

	default void handle(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
	    player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object), getRunnable(player, object, name,
                optionId, option), getDelay()));
	}

	default Runnable getRunnable(final Player player, final WorldObject object, final String name, final int optionId,
                                 final String option) {
	    return () -> {
            val existingObject = World.getObjectWithId(object, object.getId());
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
	
}
