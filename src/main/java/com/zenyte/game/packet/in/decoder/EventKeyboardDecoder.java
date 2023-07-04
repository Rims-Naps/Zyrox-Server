package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.EventKeyboardEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;


/**
 * @author Tommeh | 28 jul. 2018 | 19:52:33
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class EventKeyboardDecoder implements ClientProtDecoder<EventKeyboardEvent> {

	@Override
	public EventKeyboardEvent decode(Player player, int opcode, RSBuffer buffer) {
		var key = -1;
		var msLastKeyStroke = -1;
		while(buffer.isReadable()) {
			key = buffer.readByteC();
			msLastKeyStroke = buffer.readMedium();
		}
		return new EventKeyboardEvent(key, msLastKeyStroke);
	}
}
