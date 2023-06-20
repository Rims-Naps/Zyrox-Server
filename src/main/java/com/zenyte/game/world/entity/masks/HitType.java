package com.zenyte.game.world.entity.masks;

/**
 * @author Kris | 28. march 2018 : 0:37.40
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 *
 * Mark id must be a positive integer!
 */
public enum HitType {
	MISSED(0),
    //Regular does not invoke special effects such as vengeance.
	REGULAR(1),
    //Default invokes special effects such as vengeance.
	DEFAULT(1),
	MELEE(1),
	MAGIC(1),
	RANGED(1),
	POISON(2),
	DISEASED(3),
	DISEASED_LARGE(4),
	VENOM(5),
	HEALED(6),
	VERZIK_SHIELD(7);

	private final int mark;

	public static final HitType[] values = values();

	HitType(int mark) {
		this.mark = mark;
	}

	public int getMark() {
		return mark;
	}
}
