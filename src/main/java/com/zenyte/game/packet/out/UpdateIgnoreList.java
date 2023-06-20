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
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Tommeh | 28 jul. 2018 | 18:56:47
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class UpdateIgnoreList implements GamePacketEncoder {
	
    private final List<IgnoreEntry> list;

    @Override
    public void log(@NotNull final Player player) {
        log(player, Strings.EMPTY);
    }

    @AllArgsConstructor
    public static final class IgnoreEntry {
        private final String username;
        private final boolean added;
    }

	@Override
	public GamePacketOut encode() {
        val prot = ServerProt.UPDATE_IGNORELIST;
		val buffer = new RSBuffer(prot);
		for (val entry : list) {
		    buffer.writeByte(entry.added ? 1 : 0);
		    buffer.writeString(Utils.formatString(entry.username));
		    buffer.writeString(Strings.EMPTY);
		    buffer.writeString(Strings.EMPTY);
        }
		return new GamePacketOut(prot, buffer);
	}

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }

}
