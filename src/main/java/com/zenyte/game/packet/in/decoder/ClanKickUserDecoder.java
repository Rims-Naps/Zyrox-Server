package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.ClanKickUserEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 19:52:41
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ClanKickUserDecoder implements ClientProtDecoder<ClanKickUserEvent> {

	@Override
	public ClanKickUserEvent decode(Player player, int opcode, RSBuffer buffer) {
		val name = buffer.readString().toLowerCase().replaceAll(" ", "_");
		return new ClanKickUserEvent(name);
	}
}
