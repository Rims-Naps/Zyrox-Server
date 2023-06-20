package com.zenyte.game.world.region.area.bobsisland;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import lombok.AllArgsConstructor;

import java.util.function.Predicate;

@AllArgsConstructor
public enum FishSpot {
    NORTH(p -> p.getY() >= 4788, new Location(2526, 4780, 0), new Location(2526, 4790, 0)),
    WEST(p -> p.getX() <= 2513, new Location(2522, 4777, 0), new Location(2512, 4777, 0)),
    EAST(p -> p.getX() >= 2540, new Location(2530, 4777, 0), new Location(2539, 4777, 0)),
    SOUTH(p -> p.getY() <= 4767, new Location(2525, 4774, 0), new Location(2525, 4767, 0));
    private static final FishSpot[] values = values();

    private final Predicate<Player> predicate;
    private final Location cameraPosition;
    private final Location cameraLook;

    public Location getCameraPosition()
    {
        return cameraPosition;
    }

    public Location getCameraLook()
    {
        return cameraLook;
    }

    public Predicate<Player> getPredicate()
    {
        return predicate;
    }

}