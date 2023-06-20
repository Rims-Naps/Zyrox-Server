package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.CommandEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 19:28:36
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class CommandDecoder implements ClientProtDecoder<CommandEvent> {

	@Override
	public CommandEvent decode(Player player, final int opcode, final RSBuffer buffer) {
		val command = buffer.readString();
		return new CommandEvent(command.toLowerCase());
	}
}
