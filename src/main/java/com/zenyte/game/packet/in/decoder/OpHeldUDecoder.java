package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpHeldUEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 31 mrt. 2018 : 16:58:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpHeldUDecoder implements ClientProtDecoder<OpHeldUEvent> {

	@Override
	public OpHeldUEvent decode(Player player, int opcode, RSBuffer buffer) {
		buffer.readIntV1();
		buffer.readIntV2();
		val fromSlotId = buffer.readShortLE128();
		val fromItemId = buffer.readShortLE();
		val toSlotId = buffer.readShortLE();
		val toItemId = buffer.readShortLE128();
		return new OpHeldUEvent(fromSlotId, fromItemId, toSlotId, toItemId);
	}
}
