package com.zenyte.network.game.packet;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.network.PacketIn;
import com.zenyte.network.io.RSBuffer;
import lombok.Getter;

/**
 * @author Tommeh | 28 jul. 2018 | 12:39:39
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
public class GamePacketOut implements PacketIn {

    public GamePacketOut(final ServerProt packet, final RSBuffer buffer) {
        this.packet = packet;
        this.buffer = buffer;
    }

	private ServerProt packet;
	private RSBuffer buffer;

	public boolean encryptBuffer() {
	    return false;
    }

}
