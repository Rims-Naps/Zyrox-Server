package com.zenyte.game.content.skills.slayer;

import org.apache.commons.lang3.StringUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Kris | 5. nov 2017 : 21:22.44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public enum SlayerMaster {

	TURAEL(401, 1, 1, 0, "in Burthorpe"),
	KRYSTILIA(7663, 1, 1, 25, "in Edgeville"),
	MAZCHNA(402, 1, 20, 2, "in Canifis"),
	VANNAKA(403, 1, 40, 4, "within the Edgeville dungeon"),
	CHAELDAR(404, 1, 70, 10, "in Zanaris"),
	NIEVE(490, 1, 85, 12, "in Tree Gnome Stronghold"),
	DURADEL(405, 50, 100, 15, "in Shilo Village"),
	ELLEN(8038, 95, 85, 10, "in Myth's Guild"),
	KONAR_QUO_MATEN(8623, 1, 75, 18, "On Mount Karuulm");

	@Getter
	private final int npcId, slayerRequirement, combatRequirement, pointsPerTask;
	@Getter private final String location;

	public static final SlayerMaster[] VALUES = values();
	public static final Int2ObjectOpenHashMap<SlayerMaster> MAPPED_MASTERS = new Int2ObjectOpenHashMap<SlayerMaster>(VALUES.length);

	static {
		for (val master : VALUES) {
			MAPPED_MASTERS.put(master.npcId, master);
		}
	}

	public static final boolean isMaster(final int id) {
		return MAPPED_MASTERS.containsKey(id);
	}
	
	public final int getMultiplier(final int taskNum) {
		if (taskNum % 1000 == 0) {
			return 50;
		} else if (taskNum % 250 == 0) {
			return 35;
		} else if (taskNum % 100 == 0) {
			return 25;
		} else if (taskNum % 50 == 0) {
			return 15;
		} else if (taskNum % 10 == 0) {
			return 5;
		}
		return 1;
	}

	@Override
	public String toString() {
		return StringUtils.capitalize(name().toLowerCase());
	}
}
