package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.ClickWorldMapEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 31 mrt. 2018 : 15:07:05
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ClickWorldMapDecoder implements ClientProtDecoder<ClickWorldMapEvent> {

	@Override
	public ClickWorldMapEvent decode(Player player, int opcode, RSBuffer buffer) {
		buffer.readIntV1();
		val y = buffer.readShortLE();
		val z = buffer.readByteC();
		val x = buffer.readShort();
		return new ClickWorldMapEvent(x, y, z);
	}
}
