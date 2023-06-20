package com.zenyte.plugins.item;

import com.google.common.collect.ImmutableMap;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

import java.util.Map;

/**
 * @author Kris | 25. aug 2018 : 18:32:35
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class GemBag extends ItemPlugin {

	public static final Map<Integer, String> GEMS = ImmutableMap.<Integer, String>builder().put(1623, "Sapphires").put(1621, "Emeralds")
			.put(1619, "Rubies").put(1617, "Diamonds").put(1631, "Dragonstones").build();

	@Override
	public void handle() {
		bind("Fill", (player, item, slotId) -> {
			player.getGemBag().fill();
			/*val charges = item.getCharges();
			val data = new HashMap<Integer, Integer>();
			val size = (charges & 0x3F) + (charges >> 6 & 0x3F) + (charges >> 12 & 0x3F) + (charges >> 18 & 0x3F) + (charges >> 24 & 0x3F);
			val keys = GEMS.keySet().toArray(new Integer[GEMS.size()]);
			GEMS.forEach((id, name) -> data.put(id,
					id == keys[0] ? charges & 0x3F
							: id == keys[1] ? charges >> 6 & 0x3F
									: id == keys[2] ? charges >> 12 & 0x3F : id == keys[3] ? charges >> 18 & 0x3F : charges >> 24 & 0x3F));

			if (size == 300) {
				player.sendMessage("You cannot more than 300 gems in your gem bag.");
				return;
			}
			for (int slot = 0; slot < 28; slot++) {
				val i = player.getInventory().getItem(slot);
				if (i == null) {
					continue;
				}
				if (GEMS.keySet().contains(i.getId())) {
					val gems = item.getCharges();
					val amount = i.getId() == keys[0] ? gems & 0x3F
							: i.getId() == keys[1] ? gems >> 6 & 0x3F
									: i.getId() == keys[2] ? gems >> 12 & 0x3F
											: i.getId() == keys[3] ? gems >> 18 & 0x3F : gems >> 24 & 0x3F;
					if (amount == 60) {
						player.sendMessage("You cannot store anymore " + GEMS.get(i.getId()).toLowerCase() + ".");
						return;
					}
					val packed = (gems & 0x3F) + (i.getId() == keys[0] ? 1 : 0) | (gems >> 6 & 0x3F) + (i.getId() == keys[1] ? 1 : 0) << 6
							| (gems >> 12 & 0x3F) + (i.getId() == keys[2] ? 1 : 0) << 12
							| (gems >> 18 & 0x3F) + (i.getId() == keys[3] ? 1 : 0) << 18
							| (gems >> 24 & 0x3F) + (i.getId() == keys[4] ? 1 : 0) << 24;
					item.setCharges(packed);
					player.getInventory().deleteItem(i);
				}
			}*/
		});

		bind("Check", (player, item, slotId) -> {
			player.getGemBag().check();
			/*val charges = item.getCharges();
			val data = new HashMap<Integer, Integer>();
			val keys = GEMS.keySet().toArray(new Integer[GEMS.size()]);
			GEMS.forEach((id, name) -> data.put(id,
					id == keys[0] ? charges & 0x3F
							: id == keys[1] ? charges >> 6 & 0x3F
									: id == keys[2] ? charges >> 12 & 0x3F : id == keys[3] ? charges >> 18 & 0x3F : charges >> 24 & 0x3F));
			val builder = new StringBuilder();
			GEMS.forEach((id, name) -> {
				val amount = data.getOrDefault(id, 0);
				builder.append(name + ": " + amount + (id == GEMS.keySet().toArray()[2] ? "<br>" : " / "));
			});
			player.getDialogueManager().start(new ItemChat(player, item, builder.toString().substring(0, builder.toString().length() - 2)));*/
		});

		bind("Empty", (player, item, slotId) -> {
			player.getGemBag().empty(player.getInventory().getContainer());
			/*val charges = item.getCharges();
			val data = new HashMap<Integer, Integer>();
			val keys = GEMS.keySet().toArray(new Integer[GEMS.size()]);
			GEMS.forEach((id, name) -> data.put(id,
					id == keys[0] ? charges & 0x3F
							: id == keys[1] ? charges >> 6 & 0x3F
									: id == keys[2] ? charges >> 12 & 0x3F : id == keys[3] ? charges >> 18 & 0x3F : charges >> 24 & 0x3F));
			for (val id : data.keySet()) {
				val amount = data.get(id);
				for (int i = 0; i < amount; i++) {
					if (player.getInventory().hasFreeSlots()) {
						val gems = item.getCharges();
						player.getInventory().addItem(new Item(id));
						val packed = (gems & 0x3F) - (id == keys[0] ? 1 : 0) | (gems >> 6 & 0x3F) - (id == keys[1] ? 1 : 0) << 6
								| (gems >> 12 & 0x3F) - (id == keys[2] ? 1 : 0) << 12 | (gems >> 18 & 0x3F) - (id == keys[3] ? 1 : 0) << 18
								| (gems >> 24 & 0x3F) - (id == keys[4] ? 1 : 0) << 24;
						item.setCharges(packed);
					} else {
						player.sendMessage("Not enough space in your inventory.");
						return;
					}
				}
			}*/
		});
	}

	@Override
	public int[] getItems() {
		return new int[]{com.zenyte.game.item.containers.GemBag.GEM_BAG.getId()};
	}

}
