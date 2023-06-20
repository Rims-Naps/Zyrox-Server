package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.EventCameraPosEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Kris | 30. march 2018 : 3:20.31
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 *      profile</a>}
 */
public final class EventCameraPosDecoder implements ClientProtDecoder<EventCameraPosEvent> {

	@Override
	public EventCameraPosEvent decode(Player player, int opcode, RSBuffer buffer) {
		val y = buffer.readShort128();
		val x = buffer.readShortLE();
		return new EventCameraPosEvent(x, y);
	}
}
