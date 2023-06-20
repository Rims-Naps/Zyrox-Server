package com.zenyte.network.game.packet;

/**
 * @author Tommeh | 29 jul. 2018 | 12:49:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum DecodeState {
	READ_OPCODE, 
	READ_SIZE, 
	READ_PAYLOAD
}
