package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.ChatSetModeEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 19:27:01
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ChatSetModeDecoder implements ClientProtDecoder<ChatSetModeEvent> {

	@Override
	public ChatSetModeEvent decode(Player player, final int opcode, final RSBuffer buffer) {
		val publicFilter = buffer.readByte();
		val privateFilter = buffer.readByte();
		val tradeFilter = buffer.readByte();
		return new ChatSetModeEvent(publicFilter, privateFilter, tradeFilter);
	}
}
