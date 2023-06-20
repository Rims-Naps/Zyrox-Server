package com.zenyte.network.update.packet.inc;

import com.zenyte.network.update.packet.UpdatePacketIn;

import lombok.Getter;

/**
 * @author Tommeh | 27 jul. 2018 | 20:50:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class EncryptionKeyUpdate implements UpdatePacketIn {

	@Getter private int key;
	
	public EncryptionKeyUpdate(final int key) {
		this.key = key;
	}
}
