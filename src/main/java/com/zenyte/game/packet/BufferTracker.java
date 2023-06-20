package com.zenyte.game.packet;

import com.zenyte.Constants;
import com.zenyte.network.game.packet.GamePacketOut;
import lombok.Getter;

/**
 * @author Kris | 02/01/2019 02:49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
class BufferTracker {

    @Getter private int writtenBytes;

    boolean canWrite(final GamePacketOut packet) {
        return writtenBytes + packet.getBuffer().readableBytes() < Constants.MAX_SERVER_BUFFER_SIZE;
    }

    void appendBytes(final GamePacketOut packet) {
        writtenBytes += packet.getBuffer().readableBytes();
    }

    void reset() {
        writtenBytes = 0;
    }

}
