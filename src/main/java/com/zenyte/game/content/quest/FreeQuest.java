package com.zenyte.game.content.quest;

import lombok.Getter;

/**
 * @author Kris | 23. veebr 2018 : 1:14.07
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum FreeQuest {

	BLACK_KNIGHTS_FORTRESS(130, 4, true),
	COOKS_ASSISTANT(29, 2, true),
	DEMON_SLAYER(2561, 3, false),
	DORICS_QUEST(31, 100, true),
	DRAGON_SLAYER(176, 10, true),
	ERNEST_THE_CHICKEN(32, 3, true),
	GOBLIN_DIPLOMACY(2378, 6, false),
	IMP_CATCHER(160, 2, true),
	THE_KNIGHTS_SWORD(122, 7, true),
	PIRATES_TREASURE(71, 4, true),
	PRINCE_ALI_RESCUE(273, 110, true),
	THE_RESTLESS_GHOST(107, 5, true),
	ROMEO_AND_JULIET(144, 100, true),
	RUNE_MYSTERIES(63, 6, true),
	SHEEP_SHEARER(179, 21, true),
	SHIELD_OF_ARRAV(145, 7, true),
	VAMPIRE_SLAYER(178, 3, true),
	WITCHS_POTION(67, 3, true),
	MISTHALIN_MYSTERY(3468, 135, false),
	THE_CORSAIR_CURSE(6071, 60, false);

	@Getter private final int varbit, finishedValue;
	@Getter private final boolean varp;
	
	public static final FreeQuest[] VALUES = values();
	
	private FreeQuest(final int varbit, final int finishedValue, final boolean varp) {
		this.varbit = varbit;
		this.finishedValue = finishedValue;
		this.varp = varp;
	}
	
	
}
