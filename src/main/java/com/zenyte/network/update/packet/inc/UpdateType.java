package com.zenyte.network.update.packet.inc;

/**
 * @author Tommeh | 27 jul. 2018 | 20:42:18
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum UpdateType {
	
	NORMAL_FILE_REQUEST,
	PRIORITY_FILE_REQUEST,
	CLIENT_LOGGED_IN,
	CLIENT_LOGGED_OUT,
	ENCRYPTION_KEY_UPDATE,
	CLIENT_CONNECTED,
	CLIENT_DECONNECTED;
	
	public static final UpdateType[] VALUES = values();
}
