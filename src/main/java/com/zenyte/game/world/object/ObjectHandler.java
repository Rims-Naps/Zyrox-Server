package com.zenyte.game.world.object;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.pathfinding.events.player.ObjectEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.ObjectStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.types.config.ObjectDefinitions;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ObjectHandler {

	private static final Map<String, ObjectAction> plugins = new HashMap<>();

	/**
	 * A temporary method for until we rewrite region object management to allow up to 5 objects per location. This fixes the generic gourd tree issue for now.
	 */
	public static final WorldObject verifyObject(@NotNull final Location tile, final int id) {
		val object = World.getObjectWithId(tile, id);
		if (object == null && id == 29772) {
			val overlappingObject = World.getObjectWithType(tile, 9);
			if (overlappingObject != null && overlappingObject.getId() == 29781) {
				return new WorldObject(id, 10, 1, tile);
			}
		}
		return object;
	}

	public static final void handle(final Player player, final int id, final Location tile, final boolean forcerun, final int option) {
		val object = verifyObject(tile, id);
		if (object == null || player.isLocked() || object.isLocked()) {
			return;
		}

		if (object.getId() == 19039 && !DiaryReward.WESTERN_BANNER3.eligibleFor(player)) {
			player.sendMessage("You need to have unlocked the hard Western Provinces diaries to enter this cave.");
			return;
		}
        val defs = object.getDefinitions();

		val transformedId = player.getTransmogrifiedId(defs, object.getId());
		val transformedDefinitions = Utils.getOrDefault(ObjectDefinitions.get(transformedId), defs);
		val op = transformedDefinitions.getOption(option);
		val name = transformedDefinitions.getName();
		val action = plugins.computeIfAbsent(Integer.toString(id), unused -> plugins.get(name.toLowerCase()));
		if (action == null) {
            log.info("[" + name + "], id=" + transformedId + (transformedId != id ? ("(real id: " + id + ")") : "") + ", type="
					+ object.getType() + ", rotation=" + object.getRotation() + ", option=" + (op == null ? "null" : op) + "(" + option
					+ ")" + ", tile=" + object.getX() + ", " + object.getY() + ", " + object.getPlane() + ", varbit=" + defs.getVarbit() + ", varp=" + defs.getVarp() );
		}
		player.stopAll();
		player.getPacketDispatcher().sendMapFlag(object.getXInScene(player), object.getYInScene(player));
		if (forcerun && player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
			// player.setNextWorldTile(new WorldTile(object));
			log.info("[" + name + "], id=" + transformedId + (transformedId != id ? ("(real id: " + id + ")") : "") + ", type="
					+ object.getType() + ", rotation=" + object.getRotation() + ", option=" + (op == null ? "null" : op) + "(" + option
					+ ")" + ", tile=" + object.getX() + ", " + object.getY() + ", " + object.getPlane());
			player.sendMessage("Object: <col=C22731>" + object.getName() + "</col> - <col=C22731>" + object.getId() + "</col>, coords: "
					+ object.getX() + ", " + object.getY() + ", " + object.getPlane());
			return;
		} else if (forcerun) {
			player.setRun(true);
		}

		if (action != null) {
            log.info("[" + name + ", " + action.getClass().getSimpleName() + "], id=" + transformedId + (transformedId != id ? "(real id: " + id + ")" : "") + ", type=" + object.getType() + ", rotation=" + object.getRotation() + ", option=" + (op == null ? "null" : op) + "(" + option + ")" + ", tile=" + object.getX() + ", " + object.getY() + ", " + object.getPlane() + ", varbit=" + defs.getVarbit() + ", varp=" + defs.getVarp());
		}

		if (action == null) {
			player.setRouteEvent(new ObjectEvent(player, new ObjectStrategy(object),  () -> {
				if (World.getObjectWithId(object, object.getId()) == null || player.getPlane() != object.getPlane()) {
					return;
				}
				player.stopAll();
				player.faceObject(object);
				if (!handleOptionClick(player, option, object)) {
					return;
				}
				player.sendMessage("Nothing interesting happens.");
			}));
			return;
		}
		action.handle(player, object, name, option, op == null ? "null" : op);
	}

	public static final boolean handleOptionClick(final Player player, final int option, final WorldObject object) {
		if (option == 1) {
			if (!player.getControllerManager().processObjectClick1(object)) {
				return false;
			}
		} else if (option == 2) {
			if (!player.getControllerManager().processObjectClick2(object)) {
				return false;
			}
		} else if (option == 3) {
			if (!player.getControllerManager().processObjectClick3(object)) {
				return false;
			}
		} else if (option == 4) {
			if (!player.getControllerManager().processObjectClick4(object)) {
				return false;
			}
		} else if (option == 5) {
			if (!player.getControllerManager().processObjectClick5(object)) {
				return false;
			}
		}
		return true;
	}

	public static final void add(final Class<?> c) {
		try {
			if (c.isInterface() || !ObjectAction.class.isAssignableFrom(c)) {
				return;
			}
			final Object o = c.newInstance();
			if (!(o instanceof ObjectAction)) {
				return;
			}
			final ObjectAction action = (ObjectAction) o;
			for (final Object object : action.getObjects()) {
			    assert object instanceof Integer || object instanceof String;
                val previous = plugins.put(object.toString().toLowerCase(), action);
                if (previous != null) {
                    System.err.println("OVERLAPPING object handler: " + previous.getClass().getSimpleName() + ", " + action.getClass().getSimpleName());
                }
			}
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}

	public static ObjectAction getPlugin(final String name) {
		return plugins.get(name);
	}

}
