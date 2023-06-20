package com.zenyte.game.packet.out;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 16:07:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class IfCloseSub implements GamePacketEncoder {
	
	private final int hash;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Id: " + (hash >> 16) + ", child: " + (hash & 0xFFFF));
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.IF_CLOSESUB;
		val buffer = new RSBuffer(prot);
		buffer.writeInt(hash);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
