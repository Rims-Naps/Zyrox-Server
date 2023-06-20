package com.zenyte.game.content.theatreofblood.boss.verzikvitur.object;

import com.zenyte.game.world.entity.Location;

public enum VerzikPillarLocation {

    NORTHWEST(3161, 4318, new Location(3161, 4317), new Location(3162, 4317), new Location(3160, 4318), new Location(3160, 4317), new Location(3160, 4316), new Location(3159, 4317), new Location(3159, 4316)),
    MIDWEST(3161, 4312, new Location(3160, 4313), new Location(3161, 4311), new Location(3162,4311), new Location(3160, 4312), new Location(3160, 4311), new Location(3160, 4310), new Location(3161, 4310)),
    SOUTHWEST(3161, 4306, new Location(3161, 4305), new Location(3162, 4305), new Location(3161, 4304), new Location(3160, 4306), new Location(3160, 4305), new Location(3160, 4304)),
    NORTHEAST(3173, 4318, new Location(3174, 4317), new Location(3175, 4317), new Location(3176, 4318), new Location(3176, 4317), new Location(3176, 4316), new Location(3177, 4317), new Location(3177, 4316)),
    MIDEAST(3173, 4312, new Location(3176, 4313), new Location(3175, 4311), new Location(3174, 4311), new Location(3176, 4312), new Location(3176, 4311), new Location(3176, 4310), new Location(3175, 4310)),
    SOUTHEAST(3173, 4306, new Location(3174, 4305), new Location(3175, 4305), new Location(3175, 4304), new Location(3176, 4306), new Location(3176, 4305), new Location(3176, 4304));

    int x, y;
    Location[] safespots;
    VerzikPillarLocation(int x, int y, Location... safespots) {
        this.x = x;
        this.y = y;
        this.safespots = safespots;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Location getLocation() {
        return new Location(getX(), getY(), 0);
    }

    public Location[] getSafespots() {
        return safespots;
    }
}
