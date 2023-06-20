package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.IgnoreListDelEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 20:10:10
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class IgnoreListDelDecoder implements ClientProtDecoder<IgnoreListDelEvent> {

	@Override
	public IgnoreListDelEvent decode(Player player, int opcode, RSBuffer buffer) {
		val name = buffer.readString();
		return new IgnoreListDelEvent(name);
	}
}
