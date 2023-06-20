package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.IgnoreListAddEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 feb. 2018 : 16:01:20
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class IgnoreListAddDecoder implements ClientProtDecoder<IgnoreListAddEvent> {

	@Override
	public IgnoreListAddEvent decode(Player player, int opcode, RSBuffer buffer) {
		val name = buffer.readString();
		return new IgnoreListAddEvent(name);
	}
}
