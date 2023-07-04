package com.zenyte.game.world.entity.npc.actions;

import com.zenyte.Constants;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.events.player.EntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import mgi.types.config.npcs.NPCDefinitions;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 6. jaan 2018 : 2:53.42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class NPCHandler {

	private static final Logger logger = LogManager.getLogger(NPCHandler.class);

	public static void handle(@NonNull final Player player, @NonNull final NPC npc, final boolean forcerun,
                              final int option) {
	    if (npc.isFinished())
	        return;
        val id = npc.getId();
        val baseDefinitions = NPCDefinitions.getOrThrow(id);
        val transmogrifiedId = player.getTransmogrifiedId(baseDefinitions, id);
        if (transmogrifiedId == -1) {
            return;
        }
        val transformedDefinitions = NPCDefinitions.getOrThrow(transmogrifiedId);
        val name = transformedDefinitions.getName();
        var op = transformedDefinitions.getOption(option);
        if (Constants.SPAWN_MODE) {
            if (option == 1) {
                op = "Teleport to me";
            } else if (option == 2) {
                op = "Set radius";
            } else if (option == 5) {
                op = "Remove spawn";
            }
        }

        val plugin = NPCPlugin.getHandler(transmogrifiedId, Utils.getOrDefault(op, "null"));
        val pluginName = plugin == null ? "Absent" : plugin.getPlugin() == null ? "Default" :
                plugin.getPlugin().getClass().getSimpleName();

        logger.info("[" + pluginName + "] " + name + "(base: " + id + (baseDefinitions.getTransmogrifiedIds() == null ?
                "" : (", visible" + ": " + transmogrifiedId)) + "), " + "option:" + " " + op + ", " +
                "index: " + npc.getIndex() + ", tile: [" + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane() +
                "]" + ", varbit: " + npc.getDefinitions().getVarbit());

		if (op == null || player.isLocked() || !player.isVisibleInViewport(npc) || player.isFullMovementLocked()) {
			return;
		}
		if (player.isStunned()) {
			player.sendFilteredMessage("You're stunned.");
			return;
		}
		player.stopAll();

		//TODO: Rewrite game locks into multiple types and create scrying as a possible type.
		if (player.getTemporaryAttributes().get("Scrying") != null) {
			return;
		}

		if (forcerun) {
		    if (player.getPrivilege().eligibleTo(Privilege.ADMINISTRATOR)) {
                player.sendMessage("NPC: <col=C22731>" + npc.getName(player) + "</col> - <col=C22731>" + transmogrifiedId + "</col>, coords: "
                        + npc.getX() + ", " + npc.getY() + ", " + npc.getPlane());
                player.setLocation(new Location(npc.getLocation()));
                return;
            }
            player.setRun(true);
        }
		if (plugin != null) {
            plugin.getOption().click(player, npc, new NPCPlugin.NPCOption(option, Utils.getOrDefault(op, "null")));
            return;
        }

        player.setRouteEvent(new EntityEvent(player, new EntityStrategy(npc), () -> {
            player.stopAll();
            player.setFaceEntity(npc);
            npc.setInteractingWith(player);
            player.sendMessage("Nothing interesting happens.");
        }, true));
	}

    public static void add(final Class<?> c) {
        try {
            if (c.isAnonymousClass()) {
                return;
            }
            final Object o = c.newInstance();
            if (!(o instanceof NPCPlugin)) {
                return;
            }
            final NPCPlugin action = (NPCPlugin) o;
            action.handle();
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

}
