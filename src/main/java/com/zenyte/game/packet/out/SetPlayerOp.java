package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:51:10
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class SetPlayerOp implements GamePacketEncoder {

    private final Player player;
    private final int index;
    private final String option;
    private final boolean top;

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "Index: " + index + ", top: " + top + ", option: " + option);
    }

    @Override
    public GamePacketOut encode() {
        val prot = ServerProt.SET_PLAYER_OP;
        val buffer = new RSBuffer(prot);
        buffer.writeByte128(top ? 1 : 0);
        buffer.writeByte128(index);
        buffer.writeString(Utils.getOrDefault(option,"null"));
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }

}
