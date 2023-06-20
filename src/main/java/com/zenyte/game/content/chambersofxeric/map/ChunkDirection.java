package com.zenyte.game.content.chambersofxeric.map;

import com.zenyte.utils.Ordinal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 28/06/2019 14:31
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@Ordinal
@AllArgsConstructor
public enum ChunkDirection {
    NORTH(0, 1),
    EAST(1, 0),
    SOUTH(0, -1),
    WEST(-1, 0);

    static final ChunkDirection[] values = values();
    private final int xOffset, yOffset;

}
