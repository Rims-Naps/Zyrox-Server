package com.zenyte.network.handshake.packet;

import com.zenyte.network.ClientResponse;
import com.zenyte.network.handshake.packet.inc.HandshakeType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 27 jul. 2018 | 21:42:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@AllArgsConstructor
public class HandshakePacketOut {
	
	private HandshakeType type;
	private ClientResponse response;

}
