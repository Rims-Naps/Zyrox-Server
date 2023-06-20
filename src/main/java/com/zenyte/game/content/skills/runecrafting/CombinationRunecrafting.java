package com.zenyte.game.content.skills.runecrafting;

import lombok.Getter;

/**
 * @author Kris | 22. okt 2017 : 19:34.22
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public enum CombinationRunecrafting {
	MIST_RUNE_AIR(4695, 555, 6, 8, 14897, 1444),
	MIST_RUNE_WATER(4695, 556, 6, 8.5, 14899, 1438),
	DUST_RUNE_AIR(4696, 557, 10, 8.3, 14897, 1440),
	DUST_RUNE_EARTH(4696, 556, 10, 9, 14900, 1438),
	MUD_RUNE_WATER(4698, 557, 13, 9.3, 14899, 1440),
	MUD_RUNE_EARTH(4698, 555, 13, 9.5, 14900, 1444),
	SMOKE_RUNE_AIR(4697, 554, 15, 8.5, 14897, 1442),
	SMOKE_RUNE_FIRE(4697, 556, 15, 9, 14901, 1438),
	STEAM_RUNE_WATER(4694, 554, 19, 9.5, 14899, 1442),
	STEAM_RUNE_FIRE(4694, 555, 19, 10, 14901, 1444),
	LAVA_RUNE_EARTH(4699, 554, 23, 10, 14900, 1442),
	LAVA_RUNE_FIRE(4699, 557, 23, 10.5, 14901, 1440);

	@Getter private final int runeId, requiredRuneId, levelRequired, objectId, talismanId;
	@Getter private final double experience;
	
	public static final CombinationRunecrafting[] VALUES = values();

	private CombinationRunecrafting(final int runeId, final int requiredRuneId, final int requiredLevel, final double experience, final int objectId, final int talismanId) {
		this.runeId = runeId;
		this.requiredRuneId = requiredRuneId;
		levelRequired = requiredLevel;
		this.experience = experience;
		this.objectId = objectId;
		this.talismanId = talismanId;
	}
	
}
