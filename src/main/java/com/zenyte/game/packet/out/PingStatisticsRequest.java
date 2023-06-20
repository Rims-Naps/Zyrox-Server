package com.zenyte.game.packet.out;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 1. apr 2018 : 22:02.56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class PingStatisticsRequest implements GamePacketEncoder {
    @Override
    public void log(@NotNull final Player player) {
        log(player, Strings.EMPTY);
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.PING_STATISTICS_REQUEST;
		val buffer = new RSBuffer(prot);
		val time = System.currentTimeMillis();
		buffer.writeLong(time);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}
}