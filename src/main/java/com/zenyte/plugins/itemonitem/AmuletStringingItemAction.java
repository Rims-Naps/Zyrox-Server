package com.zenyte.plugins.itemonitem;

import java.util.ArrayList;

import com.zenyte.game.content.skills.crafting.CraftingDefinitions.AmuletStringingData;
import com.zenyte.game.content.skills.crafting.actions.AmuletStringingCrafting;
import com.zenyte.game.content.skills.crafting.dialogues.AmuletStringingD;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;

import lombok.val;

/**
 * @author Tommeh | 27 mei 2018 | 00:41:21
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class AmuletStringingItemAction implements ItemOnItemAction {

	@Override
	public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
		val string = from.getId() == 1759 || from.getId() == 6038 ? from : to;
		AmuletStringingData data = string.getId() == 6038 ? AmuletStringingData.PRE_NATURE_AMULET
				: AmuletStringingData.VALUES.get(from.getId());
		if (data == null) {
			data = AmuletStringingData.VALUES.get(to.getId());
		}
		if (data == null) {
			player.sendMessage("Nothing interesting happens.");
			return;
		}
		player.getDialogueManager().start(new AmuletStringingD(player, data));
	}

	@Override
	public int[] getItems() {
		return null;
	}

	@Override
	public ItemPair[] getMatchingPairs() {
		val list = new ArrayList<ItemPair>();
		for (val stringing : AmuletStringingData.VALUES_ARR) {
			list.add(new ItemPair(stringing.getMaterials()[0].getId(), stringing.getMaterials()[1].getId()));
		}
		return list.toArray(new ItemPair[list.size()]);
	}

}
