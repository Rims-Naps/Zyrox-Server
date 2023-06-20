package com.zenyte.game.world.entity.npc.impl.wilderness.revenants;

import com.zenyte.game.world.entity.masks.Graphics;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tommeh | 7 aug. 2018 | 13:20:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public enum RevenantAttackType {
	
	MAGIC(1415, new Graphics(-1), new Graphics(1454, 0, 92)),
	RANGED(1452, new Graphics(1451), new Graphics(-1));
	
	@Getter private final int projectile;
	@Getter private final Graphics castGraphics, hitGraphics;
	private static final RevenantAttackType[] VALUES = values();
}
