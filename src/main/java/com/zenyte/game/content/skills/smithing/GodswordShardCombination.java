package com.zenyte.game.content.skills.smithing;

import java.util.Arrays;
import java.util.Collection;

import com.zenyte.game.item.Item;

import lombok.Getter;
import lombok.val;

/**
 * @author Tommeh | 21 jul. 2018 | 23:47:37
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum GodswordShardCombination {
	
	SHARDS_1_AND_2(new Item(11794), new Item(11818), new Item(11820)),
	SHARDS_1_AND_3(new Item(11796), new Item(11818), new Item(11822)),
	SHARDS_2_AND_3(new Item(11800), new Item(11820), new Item(11822)),
	BLADE_1(new Item(11798), new Item(11794), new Item(11822)),
	BLADE_2(new Item(11798), new Item(11796), new Item(11820)),
	BLADE_3(new Item(11798), new Item(11800), new Item(11818));
	
	GodswordShardCombination(final Item item, final Item... materials) {
		this.item = item;
		this.materials = materials;
	}
	
	private static final GodswordShardCombination[] VALUES = values();
	
	@Getter private final Item item;
	@Getter private final Item[] materials;
	
	public static GodswordShardCombination getCombination(final Collection<Item> items) {
		for (val combination : VALUES) {
			if (items.containsAll(Arrays.asList(combination.getMaterials()))) {
				return combination;
			}
		}
		return null;
	}

}
