package com.zenyte.game.content.skills.runecrafting;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Player;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 24 jul. 2018 | 23:09:57
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum Tiara {
	
	AIR(5527, 1438, 14897, 25),
	MIND(5529, 1448, 14898, 27.5),
	WATER(5531, 1444, 14899, 30),
	EARTH(5535, 1440, 14900, 32.5),
	FIRE(5537, 1442, 14901, 35),
	BODY(5533, 1446, 14902, 37.5),
	COSMIC(5539, 1454, 14903, 40),
	CHAOS(5543, 1452, 14906, 42.5),
	NATURE(5541, 1462, 14905, 45),
	LAW(5545, 1458, 14904, 47.5),
	DEATH(5547, 1456, 14907, 50),
	WRATH(22121, 22118, 32492, 52.5);
	
	Tiara(final int id, final int talisman, final int altar, final double experience) {
		this.id = id;
		this.talisman = talisman;
		this.altar = altar;
		this.experience = experience;
	}
	
	public static final Tiara[] VALUES = values();
	public static final Map<Integer, Tiara> TIARAS = new HashMap<>();
	
	@Getter private int id, talisman, altar;
	@Getter private double experience;
	
	static {
		for (val tiara : VALUES) {
			TIARAS.put(tiara.getTalisman(), tiara);
		}
	}
	
	public static Tiara getTiara(final Player player, final Item item, final int objectId) {
		Tiara tiara;
		for (int slot = 0; slot < 28; slot++) {
			val i = player.getInventory().getItem(slot);
			if (i == null) {
				continue;
			}
			tiara = TIARAS.get(i.getId());
			if (tiara != null && tiara.getAltar() == objectId && item.getId() == 5525) {
				return tiara;
			}
		}
		return null;
	}

}
