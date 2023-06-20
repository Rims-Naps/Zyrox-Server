package com.zenyte.plugins.item;

import org.apache.commons.lang3.ArrayUtils;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import lombok.AllArgsConstructor;
import lombok.val;

/**
 * @author Kris | 26. aug 2018 : 23:43:56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class BagFullOfGems extends ItemPlugin {

	@AllArgsConstructor
	private enum Gem {
		UNCUT_SAPPHIRE(1624, 49.91F),
		UNCUT_EMERALD(1622, 34.62F),
		UNCUT_RUBY(1620, 11.82F),
		UNCUT_DIAMOND(1618, 3.09F),
		UNCUT_DRAGONSTONE(1632, 0.56F),
		UNCUT_ONYX(6572, 0.000001F);

		private final int notedId;
		private final float percentage;
		private static final Gem[] VALUES = values();
		
		static {
			ArrayUtils.reverse(VALUES);
		}
	}

	@Override
	public void handle() {
		bind("Open", (player, item, slotId) -> {
			val inventory = player.getInventory();
			int spaceRequired = 5;
			for (val gem : Gem.VALUES) {
				if (inventory.containsItem(gem.notedId, 1)) {
					spaceRequired--;
				}
			}
			if (inventory.getFreeSlots() < spaceRequired) {
				player.sendMessage("You need some more free space to open the bag.");
				return;
			}
			
			inventory.deleteItem(item);
			val map = new Int2IntOpenHashMap(6);
			for (int i = 0; i < 40; i++) {
				val roll = Utils.getRandomDouble(100);
				float percentage = 0;
				for (val gem : Gem.VALUES) {
					if (roll <= (percentage += gem.percentage)) {
						map.put(gem.notedId, map.get(gem.notedId) + 1);
						break;
					}
				}
			}
			val iterator = map.int2IntEntrySet().fastIterator();
			while(iterator.hasNext()) {
				val next = iterator.next();
				val id = next.getIntKey();
				val amount = next.getIntValue();
				inventory.addItem(new Item(id, amount)).onFailure(i -> World.spawnFloorItem(i, player));
			}
		});
	}

	@Override
	public int[] getItems() {
		return new int[] { 19473 };
	}

}
