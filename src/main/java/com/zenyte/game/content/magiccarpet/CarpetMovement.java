package com.zenyte.game.content.magiccarpet;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.ForceMovement;

import lombok.Getter;

/**
 * @author Kris | 21. aug 2018 : 12:35:21
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class CarpetMovement {

	public CarpetMovement(final Location location) {
		steps = location;
		forceMovement = null;
	}
	
	public CarpetMovement(final Location location, final int delay, final int direction) {
		forceMovement = new ForceMovement(location, delay, direction);
		steps = null;
	}
	
	@Getter private final ForceMovement forceMovement;
	@Getter private final Location steps;
	
}
