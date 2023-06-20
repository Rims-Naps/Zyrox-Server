package com.zenyte.network.handshake.packet.inc;

import com.zenyte.network.handshake.packet.HandshakePacketIn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 27 jul. 2018 | 21:44:25
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@AllArgsConstructor
public class HandshakeRequest implements HandshakePacketIn {
	
	private HandshakeType type;
	private int revision;

}
