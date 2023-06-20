package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpLocUEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 19:49:43
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpLocUDecoder implements ClientProtDecoder<OpLocUEvent> {

	@Override
	public OpLocUEvent decode(Player player, int opcode, RSBuffer buffer) {
		val y = buffer.readShortLE128();
		val slotId = buffer.readShortLE();
		val run = buffer.readByte128() == 1;
		val compressed = buffer.readIntLE();
		val x = buffer.readShortLE128();
		val objectId = buffer.readShort() & 0xFFFF;
		val itemId = buffer.readShortLE();
		val interfaceId = compressed >> 16;
		val componentId = compressed & 0xFFFF;
		return new OpLocUEvent(interfaceId, componentId, slotId, itemId, objectId, x, y, run);
	}
}
