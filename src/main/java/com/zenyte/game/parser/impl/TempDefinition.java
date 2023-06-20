package com.zenyte.game.parser.impl;

import lombok.Getter;
import lombok.Setter;

public class TempDefinition {

	@Getter @Setter private int id;
	@Getter @Setter private int attackAnimation = -1;
	@Getter @Setter private int blockAnimation = -1;
	@Getter @Setter private int deathAnimation = -1;
	
}
