package com.zenyte.network.update.packet.inc;

import com.zenyte.network.update.packet.UpdatePacketIn;

import lombok.Getter;

/**
 * @author Tommeh | 27 jul. 2018 | 21:24:25
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ConnectionUpdate implements UpdatePacketIn {
	
	@Getter private boolean connected;
	@Getter private int value;
	
	public ConnectionUpdate(final boolean connected, final int value) {
		this.connected = connected;
		this.value = value;
	}

}
