package com.zenyte.game.content.chambersofxeric.map;

import com.zenyte.game.util.Direction;
import lombok.ToString;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 28/06/2019 14:28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@ToString
class MapChunk {

    MapChunk(@NotNull final RaidRoom room) {
        this.room = room;
    }

    MapChunk(@NotNull final LayoutRoom room) {
        this.room = room.getRoom();
        val dir = room.getDirection();
        direction = dir == Direction.NORTH ? ChunkDirection.NORTH : dir == Direction.SOUTH ? ChunkDirection.SOUTH : dir == Direction.WEST ? ChunkDirection.WEST : ChunkDirection.EAST;
    }

    @NotNull final RaidRoom room;
    int x;
    int y;
    ChunkDirection direction;
    int chunkDirection;

}
