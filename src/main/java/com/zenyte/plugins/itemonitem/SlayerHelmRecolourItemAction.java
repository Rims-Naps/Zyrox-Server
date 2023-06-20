package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.item.SlayerHelm;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

/**
 * @author Tommeh | 19 mei 2018 | 16:51:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class SlayerHelmRecolourItemAction implements ItemOnItemAction {

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		val helmet = from.getId() == 11864 || from.getId() == 11865 ? from : to;
		val recolour = from.getId() != 11864 && from.getId() != 11865 ? from : to;
		for (val r : SlayerHelm.SlayerHelmRecolour.values) {
			val reward = r.getSlayerReward();
			val base = r.getBase();
			val helm = r.getHelm();
			if (recolour.getId() == base && (helmet.getId() == 11864 || helmet.getId() == 11865)) {
				if(!reward.equals("")) {
					if(!player.getSlayer().isUnlocked(reward)) {
						player.sendMessage("You need to unlock the slayer ability <col=00080>" + reward + "</col> before you can do this recolour.");
						return;
					}
				}
				player.getInventory().deleteItemsIfContains(new Item[] { new Item(base), helmet }, () -> {
					int id = -1;
					if(base == SlayerHelm.SlayerHelmRecolour.TWISTED_SLAYER_HELMET.getBase()) {
						if(helmet.getId() == 11864) {
							id = 30778;
						} else if(helmet.getId() == 11865) {
							id = 32103;
						}
					}
					player.getInventory().addItem(new Item(id != -1 ? id : (helm + (helmet.getId() == 11864 ? 0 : 2))));
					player.sendMessage("You successfully recoloured your slayer helmet.");
				});
				return;
			}
		}
	}

	@Override
	public int[] getItems() {
		val list = new IntArrayList();
		for (val recolour : SlayerHelm.SlayerHelmRecolour.values) {
			list.add(recolour.getBase());
		}
		list.add(ItemId.SLAYER_HELMET);
		list.add(ItemId.SLAYER_HELMET_I);
		return list.toArray(new int[list.size()]);
	}

}
