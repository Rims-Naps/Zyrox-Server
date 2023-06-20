package com.zenyte.game.world.entity.npc.impl.wilderness.revenants;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

/**
 * @author Tommeh | 7 aug. 2018 | 13:21:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public enum RevenantConstants {
	
	IMP(7881, 10, 35),
	GOBLIN(7931, 20, 20),
	PYREFIEND(7932, 25, 35),
	HOBGOBLIN(7933, 25, 35),
	CYCLOPS(7934, 50, 38),
	HELLHOUND(7935, 45, 20),
	DEMON(7936, 45, 30),
	ORK(7937, 45, 30),
	DARK_BEAST(7938, 40, 30),
	KNIGHT(7939, 30, 35),
	DRAGON(7940, 40, 30);
	
	@Getter private final int id, startHeight, delay;
	private static final RevenantConstants[] VALUES = values();
	
	public static final Map<Integer, RevenantConstants> REVENANTS = new HashMap<>();
	
	static {
		for (val revenant : VALUES) {
			REVENANTS.put(revenant.getId(), revenant);
		}
	}
}