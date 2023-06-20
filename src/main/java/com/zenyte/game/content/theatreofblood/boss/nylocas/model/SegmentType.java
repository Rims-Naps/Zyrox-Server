package com.zenyte.game.content.theatreofblood.boss.nylocas.model;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import lombok.Getter;

/**
 * @author Tommeh | 6/7/2020 | 3:44 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
public enum SegmentType {

    WEST(Direction.EAST, new Location(3280, 4249, 0), new Location(3280, 4248, 0)),
    SOUTH(Direction.NORTH, new Location(3295, 4233, 0), new Location(3296, 4233, 0)),
    EAST(Direction.WEST, new Location(3311, 4248, 0), new Location(3311, 4249, 0));

    SegmentType(final Direction direction, final Location... locations) {
        this.direction = direction;
        this.locations = locations;
    }

    private final Direction direction;
    private final Location[] locations;

    public Location getSingleNylocasSpawn(final WaveDefinition wave) {
        return locations[wave.getWave() % 2 == 0 ? 0 : 1];
    }

    public Location getBigNylocasSpawn() {
        if (this == SOUTH || this == EAST) {
            return locations[0];
        }
        return locations[1];
    }

}
