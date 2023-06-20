package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpHeldTEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 16 dec. 2017 : 17:17:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpHeldTDecoder implements ClientProtDecoder<OpHeldTEvent> {

	@Override
	public OpHeldTEvent decode(Player player, int opcode, RSBuffer buffer) {
		val fromCompressed = buffer.readIntV1();
		val toCompressed = buffer.readInt();
		val toSlot = buffer.readShort();
		buffer.readShortLE();
		val fromSlot = buffer.readShortLE();
		val fromInterfaceId = fromCompressed >> 16;
		val fromComponentId = fromCompressed & 0xFFFF;
		val toInterfaceId = toCompressed >> 16;
		val toComponentId = toCompressed & 0xFFFF;
		return new OpHeldTEvent(fromInterfaceId, fromComponentId, toInterfaceId, toComponentId, fromSlot, toSlot);
	}
}
