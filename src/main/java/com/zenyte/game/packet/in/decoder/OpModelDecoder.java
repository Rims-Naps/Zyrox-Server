package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpModelEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 7 feb. 2018 : 18:11:51
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpModelDecoder implements ClientProtDecoder<OpModelEvent> {

	@Override
	public OpModelEvent decode(Player player, int opcode, RSBuffer buffer) {
		val compressed = buffer.readInt();
		val interfaceId = compressed >> 16;
		val componentId = compressed & 0xFFFF;
		return new OpModelEvent(interfaceId, componentId);
	}
}
