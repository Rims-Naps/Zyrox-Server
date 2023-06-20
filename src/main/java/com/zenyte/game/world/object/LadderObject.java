package com.zenyte.game.world.object;

import com.zenyte.game.world.entity.Location;

import lombok.Getter;

public final class LadderObject {
	
	@Getter private final int id;
	@Getter private final Location location;
	
	public LadderObject(final int id, final Location location) {
		this.id = id;
		this.location = location;
	}
	
}