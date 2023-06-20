package com.zenyte.game.content.skills.agility;

import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.RenderAnimation;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.events.player.TileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author Kris | 19. dets 2017 : 2:29.28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public interface Obstacle extends Succeedable, ObjectAction {

	int getLevel(final WorldObject object);

	int[] getObjectIds();

	default String getStartMessage(final boolean success) {
		return null;
	}

	default String getFilterableStartMessage(final boolean success) {
		return null;
	}

	default double getSuccessModifier(final Player player, final WorldObject object) {
		return 0;
	}

	default String getEndMessage(final boolean success) {
		return null;
	}

	default String getFilterableEndMessage(final boolean success) {
		return null;
	}

	default RenderAnimation getRenderAnimation() {
		return null;
	}

	default Location getRouteEvent(final Player player, final WorldObject object) {
		return object;
	}

	int getDuration(final boolean success, final WorldObject object);

	default boolean successful(@NotNull final Player player, @NotNull final WorldObject object) {
		final int pLevel = player.getSkills().getLevel(Skills.AGILITY);
		final int oLevel = getLevel(object);
		double scale = 0.5F + ((pLevel - oLevel) / ((oLevel + 30F) - oLevel)) / 2F;
		scale += getSuccessModifier(player, object);
		scale = Math.max(0.1f, scale);
		return Utils.randomDouble() < scale;
	}

	default void schedule(final Player player, final WorldObject object, final boolean success, final double additionalXp) {
		if (success) {
			startSuccess(player, object);
		} else {
			((Failable) this).startFail(player, object);
		}
		WorldTasksManager.schedule(() -> {
			finish(player, object, additionalXp, success);
			if (success) {
				endSuccess(player, object);
			} else {
				((Failable) Obstacle.this).endFail(player, object);
			}
		}, getDuration(success, object));
	}

    @Override
    default void handle(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        handleObjectAction(player, object, name, optionId, option);
    }

	default void handle(final Player player, final WorldObject object, final double additionalXp) {
		final boolean success = AgilityManager.calculateSuccess(player, object, this);
		val event = getRouteEvent(player, object);
		if (event == null) {
			return;
		}
		final Runnable runnable = () -> {
			if (this instanceof Irreversible) {
				val obstacle = (Irreversible) this;
				if (!obstacle.failOnReverse() && obstacle.checkForReverse(player, object)) {
					obstacle.onReverse(player, object);
					return;
				}
			}
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
				player.sendFilteredMessage(getFilterableStartMessage(success));
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

	default int distance(@NotNull final WorldObject object) {
		return 0;
	}

	default boolean preconditions(final Player player, final WorldObject object) {
	    return true;
    }

	default void finish(final Player player, final WorldObject object, final double additionalXp, final boolean success) {
		player.setRunSilent(false);
		player.unlock();
		final int stage = player.getNumericAttribute("courseStage").intValue();
		player.getAttributes().put("courseStage", stage + 1);
		if (getRenderAnimation() != null) {
			player.getAppearance().resetRenderAnimation();
		}
		if (getEndMessage(success) != null) {
			player.sendMessage(getEndMessage(success));
		} else if (getFilterableEndMessage(success) != null) {
			player.sendFilteredMessage(getFilterableEndMessage(success));
		}
		player.getSkills().addXp(Skills.AGILITY, getSuccessXp(object) + additionalXp);
	}

    @Override
    default void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        if (AgilityManager.courseStarters.contains(object.getId())) {
            player.addAttribute("courseStage", 0);
        }
        final CourseObstacle obstacle = AgilityManager.COURSE_OBSTACLES.get(object.getId());
        obstacle.run(player, object);
    }

    @Override
    default Object[] getObjects() {
        val list = new ArrayList<Integer>();
        for (int i : getObjectIds()) {
            list.add(i);
        }
        return list.toArray();
    }

}
