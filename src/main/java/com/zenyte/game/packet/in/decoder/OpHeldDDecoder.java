package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpHeldDEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 20:14:09
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpHeldDDecoder implements ClientProtDecoder<OpHeldDEvent> {

	@Override
	public OpHeldDEvent decode(Player player, int opcode, RSBuffer buffer) {
		val toSlotId = buffer.readShort();
		val fromSlotId = buffer.readShortLE();
		val compressed = buffer.readInt();
		buffer.readByteC(); // Unknown field; seems to only register as @code { true } if component's type is 206, which is only true on an invisible logout tab component.
		val interfaceId = compressed >> 16;
		val componentId = compressed & 0xFFFF;
		return new OpHeldDEvent(interfaceId, componentId, fromSlotId, toSlotId);
	}
}
