package com.zenyte.game.content.area.abandonedmine.npc;

import com.zenyte.game.world.entity.Location;
import lombok.Getter;
import lombok.Setter;

public enum Minecart {


    MINECART_3621(3621, new Location(2727, 4491, 0), 0, 18),
    MINECART_3622(3622, new Location(2697, 4498, 0), 14, 0),
    MINECART_3623(3623, new Location(2715, 4516, 0), 0, 14),
    MINECART_3624(3624, new Location(2739, 4528, 0), 0, 3);

    @Getter
    private final int id;
    @Getter
    private final Location spawnLocation;
    @Getter
    private final Location destination;
    @Getter
    private final int xDist;
    @Getter
    private final int yDist;
    @Getter @Setter private int yDir;
    @Getter @Setter private int xDir;
    @Getter @Setter private int stopTicks;
    Minecart(final int id, final Location spawnLocation, final int xDist, final int yDist) {
        this.id = id;
        this.spawnLocation = spawnLocation;
        this.xDist = xDist;
        this.yDist = yDist;
        if(xDist > 0) {
            xDir = 1;
        } else if(yDist > 0) {
            yDir = 1;
        }
        this.destination = new Location(spawnLocation.getX() + xDist, spawnLocation.getY() + yDist);
    }

    public static Minecart forId(int id) {
        for(Minecart cart : Minecart.values()) {
            if(cart.getId() == id) {
                return cart;
            }
        }
        return null;
    }
}
