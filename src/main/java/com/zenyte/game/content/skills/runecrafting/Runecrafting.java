package com.zenyte.game.content.skills.runecrafting;

import com.zenyte.game.content.follower.impl.SkillingPet;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static com.zenyte.game.content.follower.impl.SkillingPet.*;

/**
 * @author Kris | 22. okt 2017 : 19:15.05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 */
@Getter
public enum Runecrafting {
	
	AIR_RUNE(11827, 556, 1, 5, 11, 1438, 1, 25378, 14989, 14841, 14897, new Location(2841, 4829, 0), new Location(2985, 3294, 0), RIFT_GUARDIAN_AIR, new Location(2985, 3292, 0)),
	MIND_RUNE(11830, 558, 2, 5.5, 14, 1448, 1, 25379, 14990, 14842, 14898, new Location(2793, 4828, 0), new Location(2980, 3514, 0), RIFT_GUARDIAN_MIND, new Location(2982, 3514, 0)),
	WATER_RUNE(12593, 555, 5, 6, 19, 1444, 1, 25376, 14991, 14843, 14899, new Location(2725, 4832, 0), new Location(3183, 3165, 0), RIFT_GUARDIAN_WATER, new Location(3185, 3165, 0)),
	EARTH_RUNE(13110, 557, 9, 6.5, 26, 1440, 1, 24972, 14992, 14844, 14900, new Location(2655, 4830, 0), new Location(3304, 3474, 0), RIFT_GUARDIAN_EARTH, new Location(3306, 3474, 0)),
	FIRE_RUNE(13106, 554, 14, 7, 35, 1442, 1, 24971, 14993, 14845, 14901, new Location(2576, 4846, 0), new Location(3311, 3255, 0), RIFT_GUARDIAN_FIRE, new Location(3313, 3255, 0)),
	BODY_RUNE(12085, 559, 20, 7.5, 46, 1446, 1, 24973, 14994, 14846, 14902, new Location(2521, 4834, 0), new Location(3055, 3446, 0), RIFT_GUARDIAN_BODY, new Location(3053, 3445, 0)),
	COSMIC_RUNE(9540, 564, 27, 8, 59, 1454, 2, 24974, 15608, 14847, 14903, new Location(2162, 4833, 0), new Location(2407, 4379, 0), RIFT_GUARDIAN_COSMIC, new Location(2408, 4377, 0)),
	LAW_RUNE(11316, 563, 54, 9.5, 95, 1458, 2, 25034, 15609, 14848, 14904, new Location(2464, 4817, 0), new Location(2857, 3379, 0), RIFT_GUARDIAN_LAW, new Location(2858, 3381, 0)),
	NATURE_RUNE(11311, 561, 44, 9, 91, 1462, 2, 24975, 15610, 14892, 14905, new Location(2400, 4835, 0), new Location(2869, 3017, 0), RIFT_GUARDIAN_NATURE, new Location(2869, 3019, 0)),
	CHAOS_RUNE(12088, 562, 35, 8.5, 74, 1452, 2, 24976, 15611, 14893, 14906, new Location(2281, 4837, 0), new Location(3059, 3589, 0), RIFT_GUARDIAN_CHAOS, new Location(3060, 3591, 0)),
	DEATH_RUNE(7496, 560, 65, 10, 99, 1456, 2, 25035, 15612, 14894, 14907, new Location(2208, 4830, 0), new Location(1863, 4639, 0), RIFT_GUARDIAN_DEATH, new Location(1860, 4639, 0)),
	BLOOD_RUNE(6715, 565, 77, 23, -1, -1, 3, 25380, -1, -1, 27978, new Location(1721, 3827, 0), new Location(1721, 3827, 0), RIFT_GUARDIAN_BLOOD, new Location(1716, 3829, 0)),
	ASTRAL_RUNE(9075, 40, 8.7, 82, -1, 2, -1, 14911, RIFT_GUARDIAN_ASTRAL, new Location(2158, 3864, 0)),
	SOUL_RUNE(7228, 566, 90, 29.7, -1, -1, 3, 25377, -1, -1, 27980, new Location(1819, 3859, 0), new Location(1819, 3859, 0), RIFT_GUARDIAN_SOUL, new Location(1814, 3854, 0)),
	WRATH_RUNE(9291, 21880, 95, 8, -1, 22118, 2, -1, 31607, 32491, 32492, new Location(2335, 4826, 0), new Location(2447, 2823, 0), RIFT_GUARDIAN_WRATH, new Location(2446, 2825, 0)),
	ZMI(-1, 0, -1, -1, -1, 2, -1, 29631, RIFT_GUARDIAN_DEFAULT, new Location(3060, 5579, 0));

	private final int runeId, level, doubleRunes, talismanId, essenceType, regionId, abyssEntranceId, ruinsObjectId, portalObjectId, altarObjectId;
	private final double experience;
	private final Location portalCoords, ruinsCoords, ruinsCenter;
	private final SkillingPet pet;

	public static final Runecrafting[] VALUES = values();
	
	private static final Map<Integer, Runecrafting> RUNES = new HashMap<Integer, Runecrafting>(VALUES.length);
	private static final Map<Integer, Runecrafting> ABYSS = new HashMap<Integer, Runecrafting>(VALUES.length);
	private static final Map<Integer, Runecrafting> RUINS = new HashMap<Integer, Runecrafting>(VALUES.length);
	private static final Map<Integer, Runecrafting> PORTALS = new HashMap<Integer, Runecrafting>(VALUES.length);
	private static final Map<Integer, Runecrafting> ALTARS = new HashMap<Integer, Runecrafting>(VALUES.length);

	static {
		for (val r : VALUES) {
			RUNES.put(r.ordinal(), r);
			ABYSS.put(r.getAbyssEntranceId(), r);
			RUINS.put(r.getRuinsObjectId(), r);
			PORTALS.put(r.getPortalObjectId(), r);
			ALTARS.put(r.getAltarObjectId(), r);
		}
	}

	public static final Runecrafting getRune(final int id) {
		return RUNES.get(id);
	}
	
	public static final Runecrafting getRuneByAbyssEntrance(final int id) {
		return ABYSS.get(id);
	}
	
	public static final Runecrafting getRuneByRuinsObject(final int id) {
		return RUINS.get(id);
	}
	
	public static final Runecrafting getRuneByPortalObject(final int id) {
		return PORTALS.get(id);
	}
	
	public static final Runecrafting getRuneByAltarObject(final int id) {
		return ALTARS.get(id);
	}

	Runecrafting(final int runeId, final int level, final double experience, final int doubleRunes, final int talismanId, final int essenceType, final int abyssEntranceId, final int altarObjectId,
				 final SkillingPet pet, final Location ruinsCenter) {
		this(-1, runeId, level, experience, doubleRunes, talismanId, essenceType, abyssEntranceId, -1, -1, altarObjectId, null, null, pet, ruinsCenter);
	}
	
	Runecrafting(final int regionId, final int runeId, final int level, final double experience, final int doubleRunes, final int talismanId, final int essenceType, final int abyssEntranceId,
				 final int ruinsObjectId, final int portalObjectId, final int altarObjectId, final Location portalCoords, final Location ruinsCoords, final SkillingPet pet,
				 final Location ruinsCenter) {
		this.regionId = regionId;
		this.runeId = runeId;
		this.level = level;
		this.experience = experience;
		this.doubleRunes = doubleRunes;
		this.talismanId = talismanId;
		this.essenceType = essenceType;
		this.abyssEntranceId = abyssEntranceId;
		this.ruinsObjectId = ruinsObjectId;
		this.portalObjectId = portalObjectId;
		this.altarObjectId = altarObjectId;
		this.portalCoords = portalCoords;
		this.ruinsCoords = ruinsCoords;
		this.pet = pet;
		this.ruinsCenter = ruinsCenter;
	}
}
