package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.DoubleClickWorldMapEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 31. march 2018 : 19:52.25
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class DoubleClickWorldMapDecoder implements ClientProtDecoder<DoubleClickWorldMapEvent> {

	@Override
	public DoubleClickWorldMapEvent decode(Player player, int opcode, RSBuffer buffer) {
		val compressed = buffer.readIntV1();
		return new DoubleClickWorldMapEvent(compressed);
	}
}
