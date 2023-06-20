package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.PingStatisticsEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Kris | 1. apr 2018 : 22:05.58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class PingStatisticsDecoder implements ClientProtDecoder<PingStatisticsEvent> {

	@Override
	public PingStatisticsEvent decode(Player player, int opcode, RSBuffer buffer) {
		val val1 = buffer.readInt() & 0xFFFFFFFFL;
		val val2 = buffer.readInt() & 0xFFFFFFFFL;
		val gc = buffer.readByteC();
		val fps = buffer.readByte();
		val ms = (val1 << 32) + val2;
		return new PingStatisticsEvent(gc, fps, ms);
	}
}
