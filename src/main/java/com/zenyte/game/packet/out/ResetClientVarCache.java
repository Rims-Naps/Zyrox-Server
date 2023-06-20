package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 03/03/2019 23:29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ResetClientVarCache implements GamePacketEncoder {

    @Override
    public void log(@NotNull final Player player) {
        log(player, Strings.EMPTY);
    }


    @Override
    public GamePacketOut encode() {
        val prot = ServerProt.RESET_CLIENT_VARCACHE;
        val buffer = new RSBuffer(prot);
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
