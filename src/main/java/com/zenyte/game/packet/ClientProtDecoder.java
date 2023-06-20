package com.zenyte.game.packet;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;

/**
 * @author Tommeh | 28 jul. 2018 | 13:08:56
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@FunctionalInterface
public interface ClientProtDecoder<E extends ClientProtEvent> {

	E decode(Player player, final int opcode, final RSBuffer buffer);

}