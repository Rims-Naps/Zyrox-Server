package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpPlayerUEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 19:50:58
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class OpPlayerUDecoder implements ClientProtDecoder<OpPlayerUEvent> {

	@Override
	public OpPlayerUEvent decode(Player player, int opcode, RSBuffer buffer) {
		val slotId = buffer.readShortLE128();
		val run = buffer.readByte128();
        val interfaceId = buffer.readInt();
        val targetIndex = buffer.readUnsignedShort();
        val itemId = buffer.readUnsignedShort();
		return new OpPlayerUEvent(targetIndex, slotId, itemId, interfaceId, run);
	}
}
