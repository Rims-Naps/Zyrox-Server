package com.zenyte.game.content.sailing;

import com.zenyte.game.util.TextUtils;
import com.zenyte.game.world.entity.Location;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 7 jul. 2018 | 21:04:47
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum CharterLocation {
	
	BRIMHAVEN(new Location(2763, 3238, 1), "Brimhaven", -1, 480, 680, 480, 3900, 400, 2900, 1600, 3200, 400),
	CATHERBY(new Location(2792, 3417, 1), "Catherby", 480, -1, 1000, 480, 3500, 1600, 3500, 1000, 3200, 1600),
	CORSAIR_COVE(new Location(2592, 2851, 1), "Corsair Cove", 680, 1000, -1, 800, 4080, 600, 4040, 1200, 3200, 800),
	MUSA_POINT(new Location(2957, 3158, 1), "Karamja", 200, 480, 800, -1, 1100, 400, 1100, -1, 3200, 200),
	MOS_LE_HARMLESS(new Location(3668, 2931, 1), "Mos LeHarmless", 3900, 2500, 2040, 4100, -1, 4100, 1280, 3200, 1600),
	PORT_KHAZARD(new Location(2674, 3141, 1), "Port Khazard", 1600, 1600, 600, 1600, 4100, -1, 4100, 1280, 3200, 1600),
	PORT_PHASMATYS(new Location(3705, 3503, 1), "Port Phasmatys", 2900, 3500, 4040, 1100, -1, 4100, -1, 1300, 3200, 3200),
	PORT_SARIM(new Location(3038, 3189, 1), "Port Sarim", 1600, 1000, 1200, -1, 1300, 1280, 1300, -1, 3200, 400),
	PORT_TYRAS(new Location(2142, 3125, 1), "Port Tyras", 3200, 3200, 3200, 3200, 3200, 3200, 3200, 3200, -1, 3200),
	SHIPYARD(new Location(2998, 3032, 1), "Ship Yard", 400, 1600, 800, 200, 1100, 720, 1100, 400, 3200, -1);
	
	@Getter private Location location;
	@Getter private final String shopPrefix;
	@Getter private int[] costs;
	
	private static final CharterLocation[] VALUES = values();
	public static final Map<String, CharterLocation> LOCATIONS = new HashMap<>();
	
	static {
		for (val location : VALUES) {
			LOCATIONS.put(location.toString(), location);
		}
	}
	
	CharterLocation(final Location location, final String shopPrefix, final int... costs) {
		this.location = location;
		this.shopPrefix = shopPrefix;
		this.costs = costs;
	}
	
	public static CharterLocation get(final String string) {
		return LOCATIONS.get(string);
	}
	
	public static CharterLocation getLocation(final Location location) {
		for (val charterLocation : VALUES) {
			if (charterLocation.getLocation().withinDistance(location.getX(), location.getY(), 15)) {
				return charterLocation;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return this.equals(MOS_LE_HARMLESS) ? "Mos Le'Harmless" :TextUtils.capitalize(name().toLowerCase().replace("_", " "));
	}

}
