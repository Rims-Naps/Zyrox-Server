package com.zenyte.game.world.entity.masks;

import com.zenyte.game.world.entity.Location;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Tom
 */
public class ForceMovement {

	public static final int SOUTH = 0, WEST = 0x200, NORTH = 0x400, EAST = 0x600;

    @Getter @Setter private Location toFirstTile, toSecondTile;
    @Getter @Setter private int firstTileTicketDelay, secondTileTicketDelay, direction;

    public ForceMovement(final Location toFirstTile, final int firstTileTicketDelay, final int direction) {
        this(toFirstTile, firstTileTicketDelay, null, 0, direction);
    }

    public ForceMovement(final Location toFirstTile, final int firstTileTicketDelay, final Location toSecondTile, final int secondTileTicketDelay, final int direction) {
        this.toFirstTile = toFirstTile;
        this.firstTileTicketDelay = firstTileTicketDelay;
        this.toSecondTile = toSecondTile;
        this.secondTileTicketDelay = secondTileTicketDelay;
        this.direction = direction;
    }
}