package com.zenyte.game.world.region;

import lombok.Getter;

public class XTEA {

	@Getter private int region;
	@Getter private int[] keys;
	@Getter private String name;
	@Getter private int nameHash;
	@Getter private int groupId;
	@Getter private int archive;
	
	public XTEA(final int region, final int[] keys) {
		this.region = region;
		this.keys = keys;
	}

	public XTEA(int archive, int groupid, int nameHash, String name, final int region, final int[] keys) {
		this.archive = archive;
		this.groupId = groupid;
		this.nameHash = nameHash;
		this.name = name;
		this.region = region;
		this.keys = keys;
	}
}
