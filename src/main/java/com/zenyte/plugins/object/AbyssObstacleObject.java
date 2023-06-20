package com.zenyte.plugins.object;

import com.zenyte.game.content.skills.runecrafting.abyss.AbyssObstacle;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Tommeh | 29 jul. 2018 | 22:14:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class AbyssObstacleObject implements ObjectAction {
	
	public static final Animation GAP_START_ANIMATION = new Animation(1331);
	public static final Animation GAP_END_ANIMATION = new Animation(1332);
	public static final Animation EYES_ANIMATION = new Animation(1057);

	@Override
	public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
	    val delay = player.getNumericTemporaryAttribute("abyss obstacle delay");
	    if (delay.longValue() > System.currentTimeMillis()) {
	        return;
        }
		val obstacle = AbyssObstacle.get(object.getId());
		val destination = obstacle.getDestination();
		player.lock();
		WorldTasksManager.schedule(() -> obstacle.getType().getScript().handle(player, object, destination));
	}

	@Override
	public Object[] getObjects() {
		val list = new ArrayList<Integer>();
		for (val obstacle : AbyssObstacle.VALUES) {
			list.add(obstacle.getId());
		}
		return list.toArray(new Object[list.size()]);
	}

}
