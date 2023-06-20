package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.FriendListAddEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 feb. 2018 : 16:00:36
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class FriendListAddDecoder implements ClientProtDecoder<FriendListAddEvent> {

	@Override
	public FriendListAddEvent decode(Player player, int opcode, RSBuffer buffer) {
		val name = buffer.readString();
		return new FriendListAddEvent(name);
	}
}
