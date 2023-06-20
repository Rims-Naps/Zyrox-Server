package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpNpcTEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 19:44:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpNpcTDecoder implements ClientProtDecoder<OpNpcTEvent> {

	@Override
	public OpNpcTEvent decode(Player player, int opcode, RSBuffer buffer) {
		val index = buffer.readShortLE128();
		val run = buffer.readByteC() == 1;
		val compressed = buffer.readIntLE();
		buffer.readShort128();
		val interfaceId = compressed >> 16;
		val componentId = compressed & 0xFFFF;
		return new OpNpcTEvent(interfaceId, componentId, index, run);
	}
}
