package com.zenyte.network.handshake.packet.inc;

import java.util.HashMap;
import java.util.Map;

import com.zenyte.network.handshake.packet.HandshakePacketIn;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Tommeh | 27 jul. 2018 | 21:43:23
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum HandshakeType implements HandshakePacketIn {
	
	GAME_CONNECTION(14),
	UPDATE_CONNECTION(15);
	
	private final int id;

	private static final HandshakeType[] VALUES = values();
	private static final Map<Integer, HandshakeType> TYPES = new HashMap<>();
	
	static {
		for (val type : VALUES) {
			TYPES.put(type.getId(), type);
		}
	}
	
	public static HandshakeType get(final int id) {
		return TYPES.get(id);
	}

}
