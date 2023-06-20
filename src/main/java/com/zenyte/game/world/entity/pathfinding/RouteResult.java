package com.zenyte.game.world.entity.pathfinding;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 26/02/2019 22:04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
@Getter
public class RouteResult {

    private final int steps;
    private final int[] xBuffer;
    private final int[] yBuffer;
    private final boolean alternative;

    public static final RouteResult ILLEGAL = new RouteResult(-1, null, null, true);

}
