package com.zenyte.game.content.skills.construction;

import com.zenyte.game.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Tommeh | 29 sep. 2018 | 11:40:52
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@AllArgsConstructor
@Getter
public enum Plank {

	WOOD(100, new Item(1511), new Item(960)),
	OAK(250, new Item(1521), new Item(8778)),
	TEAK(500, new Item(6333), new Item(8780)),
	MAHOGANY(1500, new Item(6332), new Item(8782));

    private final int cost;
	private final Item base, product;
	public static final Plank[] values = values();
}
