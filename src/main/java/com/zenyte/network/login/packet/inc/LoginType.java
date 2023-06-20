package com.zenyte.network.login.packet.inc;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 09:43:43
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum LoginType {
	
	NEW_LOGIN_CONNECTION(16),
	RECONNECT_LOGIN_CONNECTION(18);
	
	private final int id;

	private static final LoginType[] VALUES = values();
	private static final Map<Integer, LoginType> TYPES = new HashMap<>();
	
	static {
		for (val type : VALUES) {
			TYPES.put(type.getId(), type);
		}
	}
	
	public static LoginType get(final int id) {
		return TYPES.get(id);
	}

}
