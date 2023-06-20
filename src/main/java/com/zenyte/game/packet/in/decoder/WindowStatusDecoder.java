package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.WindowStatusEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jan. 2018 : 21:27:09
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class WindowStatusDecoder implements ClientProtDecoder<WindowStatusEvent> {

	@Override
	public WindowStatusEvent decode(Player player, final int opcode, final RSBuffer buffer) {
		val mode = buffer.readUnsignedByte();
		val width = buffer.readShort();
		val height = buffer.readShort();
		return new WindowStatusEvent(mode, width, height);
	}
}
