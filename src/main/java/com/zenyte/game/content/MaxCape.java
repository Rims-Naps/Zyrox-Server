package com.zenyte.game.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 19 aug. 2018 | 20:29:22
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@AllArgsConstructor
public enum MaxCape {

	ARDOUGNE(20760, 20764, 13124),
	ASSEMBLER(21898, 21900, 22109),
	ACCUMULATOR(13337, 13338, 10499),
	INFERNAL(21285, 21282, 21295),
	FIRE(13329, 13330, 6570),
	SARADOMIN(13331, 13332, 2412),
	ZAMORAK(13333, 13334, 2414),
	GUTHIX(13335, 13336, 2413),
	IMBUED_SARADMIN(21776, 21778, 21791),
	IMBUED_ZAMORAK(21780, 21782, 21795),
	IMBUED_GUTHIX(21784, 21786, 21793),
	MYTHICAL(32184, 32186, 22114);

	@Getter private int cape, hood, upgrade;
	public static final MaxCape[] values = values();
	private static final Map<Integer, MaxCape> CAPES = new HashMap<>(values.length);
	
	public static MaxCape get(final int value) {
		return CAPES.get(value);
	}
	
	static {
		for (val cape : values) {
			CAPES.put(cape.getUpgrade(), cape);
		}
	}

}
