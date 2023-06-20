package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpLocTEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 9 jan. 2018 : 20:07:16
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpLocTDecoder implements ClientProtDecoder<OpLocTEvent> {

	@Override
	public OpLocTEvent decode(Player player, int opcode, RSBuffer buffer) {
		val run = buffer.readByte() == 1;
		val x = buffer.readShort();
		val objectId = buffer.readShort128() & 0xFFFF;
		val compressed = buffer.readIntV2();
		val slotId = buffer.readShort();
		val y = buffer.readShortLE();
		val interfaceId = compressed >> 16;
		val componentId = compressed & 0xFFFF;
		return new OpLocTEvent(interfaceId, componentId, slotId, objectId, x, y, run);
	}
}
