package com.zenyte.game.content.quest;

import lombok.Getter;

/**
 * @author Kris | 23. veebr 2018 : 1:27.57
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum Miniquest {

	ENTER_THE_ABYSS(492, 4, true),
	ARCHITECTURAL_ALLIANCE(4982, 3, false),
	BEAR_YOUR_SOUL(5078, 3, false),
	ALFRED_GRIMHANDS_BARCRAWL(77, 2, true),
	CURSE_OF_THE_EMPTY_LORD(821, 1, false),
	ENCHANTED_KEY(1391, 2047, false),
	THE_GENERALS_SHADOW(3330, 30, false),
	SKIPPY_AND_THE_MOGRES(1344, 3, false),
	THE_MAGE_ARENA(267, 8, true),
	LAIR_OF_TARN_RAZORLOR(3290, 3, false),
	FAMILY_PEST(5347, 3, false),
	THE_MAGE_ARENA_II(6067, 4, false);
	
	@Getter private final int varbit, finishedValue;
	@Getter private final boolean varp;
	
	public static final Miniquest[] VALUES = values();
	
	private Miniquest(final int varbit, final int finishedValue, final boolean varp) {
		this.varbit = varbit;
		this.finishedValue = finishedValue;
		this.varp = varp;
	}
	
}
