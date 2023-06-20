package com.zenyte.game.content.skills.magic.spells.teleports;

import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 9. juuli 2018 : 02:29:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public interface Teleport {

	int WILDERNESS_LEVEL = 20;
	
	int DISTANCE = 2;
	
	boolean UNRESTRICTED = false;
	boolean RESTRICTED = true;

	TeleportType getType();

	Location getDestination();

	int getLevel();

	double getExperience();

	int getRandomizationDistance();

	Item[] getRunes();

	int getWildernessLevel();

	boolean isCombatRestricted();
	
	default void onUsage(final Player player) {
		
	}
	
	default void onArrival(final Player player) {

	}

	default void teleport(final Player player) {
		try {
			getType().getStructure().teleport(player, this);
		} catch (final Exception e) {
            Magic.logger.error(Strings.EMPTY, e);
		}
	}

}
