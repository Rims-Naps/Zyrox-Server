package com.zenyte.game.content.skills.construction;

import com.google.gson.annotations.Expose;
import com.zenyte.game.content.skills.construction.constants.Furniture;
import com.zenyte.game.content.skills.construction.constants.FurnitureSpace;
import com.zenyte.game.world.entity.Location;

import lombok.Getter;
import lombok.Setter;

public class FurnitureData {

	@Expose
	@Getter
	@Setter
	private FurnitureSpace space;
	@Expose
	@Getter
	@Setter
	private Furniture furniture;
	@Expose
	@Getter
	@Setter
	private Location location;
	@Expose
	@Getter
	@Setter
	private int type, rotation;

	public FurnitureData(FurnitureSpace space, Furniture furniture, Location location, int type, int rotation) {
		this.space = space;
		this.furniture = furniture;
		this.location = location;
		this.type = type;
		this.rotation = rotation;
	}

}
