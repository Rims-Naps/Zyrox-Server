package com.zenyte.network.update.packet.inc;

import com.zenyte.network.update.packet.UpdatePacketIn;

import lombok.Getter;

/**
 * @author Tommeh | 27 jul. 2018 | 20:31:21
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class FileRequest implements UpdatePacketIn {
	
	@Getter private boolean priority;
	@Getter private int index, file;
	
	public FileRequest(final boolean priority, final int index, final int file) {
		this.priority = priority;
		this.index = index;
		this.file = file;
	}

}
