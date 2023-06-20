package com.zenyte.game.world.entity.masks;

import lombok.Getter;

/**
 * @author Kris | 6. nov 2017 : 14:34.26
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public enum UpdateFlag {

	/**
	 * Appearance update.
	 */
	APPEARANCE(0x1, -1),

	/**
	 * Graphics update.
	 */
	GRAPHICS(0x200, 0x8),

	/**
	 * Animation update.
	 */
	ANIMATION(0x80, 0x20),

	/**
	 * Forced chat update.
	 */
	FORCED_CHAT(0x20, 0x2),

	/**
	 * Interacting entity update.
	 */
	FACE_ENTITY(0x2, 0x1),

	/**
	 * Face coordinate entity update.
	 */
	FACE_COORDINATE(0x4, 0x40),

	/**
	 * Hit update.
	 */
	HIT(0x40, 0x10),

	/**
	 * Update flag used to define player's current movement type (walk or run)
	 */
	MOVEMENT_TYPE(0x800, -1),

	/**
	 * Update flag used to force move player.
	 */
	FORCE_MOVEMENT(0x400, -1),

	/**
	 * Update flag used to set player's movement type for one tick (teleport or walk - supports run as well but never used)
	 */
	TEMPORARY_MOVEMENT_TYPE(0x1000, -1),

	/**
	 * Update flag used to set player's right-click strings (before name, after name and after combat)
	 */
	NAMETAG(0x100, -1),
	
	/**
	 * Update flag used for chat messages.
	 */
	CHAT(0x10, -1),

	/**
	 * Update flag used to transform a npc to a different one.
	 */
	TRANSFORMATION(-1, 0x4),

    /**
     * Update flag used to set an option on an npc.
     */
    OPTION(-1, 0x80);
	
	@Getter private final int playerMask, npcMask;
	
	UpdateFlag(final int playerMask, final int npcMask) {
		this.playerMask = playerMask;
		this.npcMask = npcMask;
	}
	
	public static final UpdateFlag[] VALUES = values();

}
