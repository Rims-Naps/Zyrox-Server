package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.InterfaceOnFloorItemEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Kris | 1. apr 2018 : 21:15.54
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class OpObjTDecoder implements ClientProtDecoder<InterfaceOnFloorItemEvent> {

	@Override
	public InterfaceOnFloorItemEvent decode(Player player, int opcode, RSBuffer buffer) {
		val compressed = buffer.readIntV1();
		buffer.readShort();
		val y = buffer.readShort128();
		val x = buffer.readShort128();
		buffer.readByte128();
		val itemId = buffer.readShortLE();
		val interfaceId = compressed >> 16;
		val componentId = compressed & 0xFFFF;
		return new InterfaceOnFloorItemEvent(interfaceId, componentId, itemId, x, y);
	}
}
