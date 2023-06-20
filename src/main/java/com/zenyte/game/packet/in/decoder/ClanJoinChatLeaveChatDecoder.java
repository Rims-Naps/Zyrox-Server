package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.ClanJoinChatLeaveChatEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 2 dec. 2017 : 18:53:58
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ClanJoinChatLeaveChatDecoder implements ClientProtDecoder<ClanJoinChatLeaveChatEvent> {

	@Override
	public ClanJoinChatLeaveChatEvent decode(Player player, int opcode, RSBuffer buffer) {
		val name = buffer.readString();
		return new ClanJoinChatLeaveChatEvent(name);
	}
}
