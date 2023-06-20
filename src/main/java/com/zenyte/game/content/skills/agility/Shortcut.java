package com.zenyte.game.content.skills.agility;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.events.player.TileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Kris | 14. apr 2018 : 22:25.28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public interface Shortcut extends Obstacle, ObjectAction {

	@Override
	public default void schedule(final Player player, final WorldObject object, final boolean success, final double additionalXp) {
		if (success) {
			startSuccess(player, object);
		} else {
			((Failable) this).startFail(player, object);
		}
		if (getDuration(success, object) != -1) {
			WorldTasksManager.schedule(() -> {
				finish(player, object, additionalXp, success);
				if (success) {
					endSuccess(player, object);
				} else {
					((Failable) Shortcut.this).endFail(player, object);
				}
			}, getDuration(success, object));
		}
	}

	@Override
	public default void handle(final Player player, final WorldObject object, final double additionalXp) {
		final boolean success = AgilityManager.calculateSuccess(player, object, this);
		val event = getRouteEvent(player, object);
		if (event == null) {
			return;
		}
		final Runnable runnable = () -> {
			player.stopAll();
			player.faceObject(object);
			if (player.getSkills().getLevel(Skills.AGILITY) < getLevel(object)) {
				player.sendMessage("You need an Agility level of at least " + getLevel(object) + " to use this Agility shortcut.");
				return;
			}
            if (!preconditions(player, object)) {
                return;
            }
			player.lock();
			player.addFreezeImmunity(getDelay());
			player.getTemporaryAttributes().put("courseRun", player.isRun());
			player.setRunSilent(true);
			if (getRenderAnimation() != null) {
				player.getAppearance().setRenderAnimation(getRenderAnimation());
			}
			if (getStartMessage(success) != null) {
				player.sendMessage(getStartMessage(success));
			} else if (getFilterableStartMessage(success) != null) {
				player.sendFilteredMessage(getFilterableEndMessage(success));
			}
            try {
                schedule(player, object, success, additionalXp);
            } catch (Exception e) {
                e.printStackTrace();
                player.unlock();
            }
		};
		if (event instanceof WorldObject) {
			player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy((WorldObject) event, distance(object)), runnable, 1));
		} else {
			player.setRouteEvent(new TileEvent(player, new TileStrategy(event, distance(object)), runnable, 1));
		}
	}

    @Override
	public default void finish(final Player player, final WorldObject object, final double additionalXp, final boolean success) {
		player.setRunSilent(false);
		player.unlock();
		if (getRenderAnimation() != null) {
			player.getAppearance().resetRenderAnimation();
		}
		if (getEndMessage(success) != null) {
			player.sendMessage(getEndMessage(success));
		} else if (getFilterableEndMessage(success) != null) {
			player.sendFilteredMessage(getFilterableEndMessage(success));
		}
		player.getSkills().addXp(Skills.AGILITY, success ? getSuccessXp(object) : ((Failable) Shortcut.this).getFailXp(object));
	}

    @Override
    default void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        handle(player, object, 0);
    }
}
