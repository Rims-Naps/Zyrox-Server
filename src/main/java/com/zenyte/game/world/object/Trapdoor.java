package com.zenyte.game.world.object;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 28. aug 2018 : 02:24:30
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@AllArgsConstructor
@Getter
public enum Trapdoor {

	EDGEVILLE_TRAPDOOR(1581, 1579, tile(3100, 3487, 0), tile(3096, 9867, 0)),
	DRAYNOR_TRAPDOOR_A(6435, 6434, tile(3084, 3272, 0), tile(3084, 9673, 0)),
    DRAYNOR_TRAPDOOR_B(6435, 6434, tile(3118, 3244, 0), tile(3118, 9644, 0)),
	ROGUES_DEN_TRAPDOOR(7257, -1, tile(2905, 3537, 0), tile(3061, 4985, 1)),
    PATERDOMUS_TEMPLE_TRAPDOOR(1581, 1579, tile(3405, 3507, 0), tile(3405, 9906, 0)),
    PATERDOMUS_TRAPDOOR_OTHERSIDE(3433, 3432, tile(3422, 3485, 0), tile(3440, 9887, 0)),
    APE_ATOLL(4880, 4879, tile(2807, 2785, 0), tile(2807, 9201, 0)),
	VARROCK_GUILD(10559, 10558, tile(3190, 3355, 0), tile(3189, 9758, 0)),
	CASTLEWARS_MIDDLE_SOUTH(1581, 1580, tile(2399, 3099, 0), tile(2399, 9500, 0)),
	CASTLEWARS_MIDDLE_NORTH(1581, 1580, tile(2400, 3108, 0), tile(2400, 9507, 0)),
	;

	public static final Trapdoor[] values = values();
	public static final Int2ObjectOpenHashMap<Trapdoor> trapdoors;

	static {
        Utils.populateMap(values, trapdoors = new Int2ObjectOpenHashMap<>(values.length),
                trapdoor -> trapdoor.trapdoorLocation.getPositionHash());
    }

	public static final Animation CLIMB_DOWN = new Animation(827);
	private int open, closed;
	private Location trapdoorLocation, destination;
	
	private static final Location tile(final int x, final int y, final int z) {
		return new Location(x, y, z);
	}
	
	
}
