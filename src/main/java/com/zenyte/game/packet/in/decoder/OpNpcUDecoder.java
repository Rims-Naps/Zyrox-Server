package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpNpcUEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 19:48:59
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpNpcUDecoder implements ClientProtDecoder<OpNpcUEvent> {

	@Override
	public OpNpcUEvent decode(Player player, int opcode, RSBuffer buffer) {
		val itemId = buffer.readShortLE128();
		val slotId = buffer.readShort();
		val index = buffer.readShortLE128();
		val run = buffer.readByteC() == 1;
		val compressed = buffer.readInt();
		val interfaceId = compressed >> 16;
		val componentId = compressed & 0xFFFF;
		return new OpNpcUEvent(interfaceId, componentId, slotId, itemId, index, run);
	}
}
