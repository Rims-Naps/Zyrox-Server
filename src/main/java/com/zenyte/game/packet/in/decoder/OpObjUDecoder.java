package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpObjUEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Kris | 1. apr 2018 : 21:15.54
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class OpObjUDecoder implements ClientProtDecoder<OpObjUEvent> {

	@Override
	public OpObjUEvent decode(Player player, int opcode, RSBuffer buffer) {
		val compressed = buffer.readIntV1();
		val itemId = buffer.readShortLE128();
		val run = buffer.readByte128() == 1;
		val slotId = buffer.readShortLE();
		val floorItemId = buffer.readShortLE128();
		val x = buffer.readShortLE128();
		val y = buffer.readShortLE();
		val interfaceId = compressed >> 16;
		val componentId = compressed & 0xFFFF;
		return new OpObjUEvent(interfaceId, componentId, slotId, itemId, floorItemId, x, y, run);
	}
}
