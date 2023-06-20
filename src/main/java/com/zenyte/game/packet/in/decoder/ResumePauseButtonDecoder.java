package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.ResumePauseButtonEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 19:29:41
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ResumePauseButtonDecoder implements ClientProtDecoder<ResumePauseButtonEvent> {

	@Override
	public ResumePauseButtonEvent decode(Player player, final int opcode, final RSBuffer buffer) {
		val compressed = buffer.readIntV2();
		val slotId = buffer.readShort128();
		val interfaceId = compressed >> 16;
		val componentId = compressed & 0xFFFF;
		return new ResumePauseButtonEvent(interfaceId, componentId, slotId);
	}
}
