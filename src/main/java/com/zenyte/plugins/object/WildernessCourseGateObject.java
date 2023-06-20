package com.zenyte.plugins.object;

import com.zenyte.game.tasks.WorldTask;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.Door;
import com.zenyte.game.world.object.DoubleDoor;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Tommeh | 25 feb. 2018 : 13:59:01
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class WildernessCourseGateObject implements ObjectAction {

	@Override
	public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
		val gate = DoubleDoor.handleGraphicalDoubleDoor(player, new WorldObject(23552, 0, 3,
                new Location(2998, 3931, 0)), null);
        player.setRunSilent(true);
        player.lock();
        player.freeze(16, 16);
        player.addWalkSteps(2998, 3916, -1, false);
        player.getAppearance().setRenderAnimation(new RenderAnimation(RenderAnimation.STAND, 762, RenderAnimation.WALK));
        val doorway = World.getObjectWithType(new Location(2998, 3917, 0), 0);
        WorldTasksManager.schedule(new WorldTask() {
            int ticks;
            WorldObject door;
            @Override
            public void run() {
                switch (ticks++) {
                    case 1:
                        DoubleDoor.handleGraphicalDoubleDoor(player, object, gate);
                        break;
                    case 13:
                        player.getAppearance().resetRenderAnimation();
                        door = Door.handleGraphicalDoor(doorway, null);
                        break;
                    case 15:
                        player.setRunSilent(false);
                        player.unlock();
                        Door.handleGraphicalDoor(door, doorway);
                        DoubleDoor.handleGraphicalDoubleDoor(player, object, gate);
                        stop();
                        break;
                }
            }
        }, 0, 0);
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { 23552, 23554 };
	}

}
