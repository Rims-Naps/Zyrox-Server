package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.PlayerReportEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Kris | 1. apr 2018 : 22:57.16
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class PlayerReportDecoder implements ClientProtDecoder<PlayerReportEvent> {

	@Override
	public PlayerReportEvent decode(Player player, int opcode, RSBuffer buffer) {
		val name = buffer.readString();
		val rule = buffer.readByte();
		val mute = buffer.readByte() == 1;
		return new PlayerReportEvent(name, rule, mute);
	}
}
