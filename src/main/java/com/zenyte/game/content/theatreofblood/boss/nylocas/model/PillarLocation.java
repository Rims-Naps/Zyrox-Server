package com.zenyte.game.content.theatreofblood.boss.nylocas.model;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import lombok.Getter;

/**
 * @author Tommeh | 6/9/2020 | 6:00 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
public enum PillarLocation {

    NORTH_WEST(1, new Location(3289, 4253, 0), new PillarCorner(new Location(3290, 4252, 0), new Location(3291, 4252, 0)), new PillarCorner(new Location(3292, 4254, 0), new Location(3292, 4253, 0))),
    SOUTH_WEST(0, new Location(3289, 4242, 0), new PillarCorner(new Location(3290, 4245, 0), new Location(3291, 4245, 0)), new PillarCorner(new Location(3292, 4243, 0), new Location(3292, 4244, 0))),
    NORTH_EAST(2, new Location(3300, 4253, 0), new PillarCorner(new Location(3301, 4252, 0), new Location(3300, 4252, 0)), new PillarCorner(new Location(3299, 4254, 0), new Location(3299, 4253, 0))),
    SOUTH_EAST(3, new Location(3300, 4242, 0), new PillarCorner(new Location(3301, 4245, 0), new Location(3300, 4245, 0)), new PillarCorner(new Location(3299, 4243, 0), new Location(3299, 4244, 0)));

    PillarLocation(final int rotation, final Location location, final PillarCorner... corners) {
        this.rotation = rotation;
        this.location = location;
        this.corners = corners;
    }

    private final int rotation;
    private final Location location;
    private final PillarCorner[] corners;

    public static final PillarLocation[] values = values();

    public static PillarLocation getRandom() {
        return values[Utils.random(values.length - 1)];
    }
}
