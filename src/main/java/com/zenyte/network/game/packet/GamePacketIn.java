package com.zenyte.network.game.packet;

import com.zenyte.network.PacketIn;
import com.zenyte.network.io.RSBuffer;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tommeh | 28 jul. 2018 | 12:39:39
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@AllArgsConstructor
public class GamePacketIn implements PacketIn {
	
	private int opcode;
	private RSBuffer buffer;

}
