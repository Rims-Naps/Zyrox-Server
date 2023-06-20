package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 03/03/2019 23:27
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
public class IfSetScrollPos implements GamePacketEncoder {

    private final int interfaceId, componentId, height;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", height: " + height);
    }

    @Override
    public GamePacketOut encode() {
        val prot = ServerProt.IF_SETSCROLLPOS;
        val buffer = new RSBuffer(prot);
        buffer.writeInt(interfaceId << 16 | componentId);
        buffer.writeShort128(height);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
