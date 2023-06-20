package com.zenyte.game.content.kebos.alchemicalhydra.model;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import lombok.Data;

import java.util.Set;

/**
 * @author Kris | 10/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Data
public class FireWallBlock {
    private final Set<Location> tiles;
    private final Direction direction;
    private final Location movingFireLocation;
}
