package com.zenyte.plugins.itemonobject;

import com.zenyte.game.content.skills.smithing.SmeltableBar;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnObjectAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.skills.SmeltingD;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Kris | 11. nov 2017 : 0:46.21
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class SmeltingFurnaceObjectAction implements ItemOnObjectAction {

	@Override
	public void handleItemOnObjectAction(Player player, Item item, int slot, WorldObject object) {
		player.getDialogueManager().start(new SmeltingD(player, object));
	}

	@Override
	public Object[] getItems() {
		val list = new ArrayList<Object>();
		for (val bar : SmeltableBar.VALUES) {
			for (val material : bar.getMaterials()) {
				list.add(material.getId());
			}
		}
		return list.toArray(new Object[list.size()]);
	}

	@Override
	public Object[] getObjects() {
		return new Object[] { "Furnace", "Clay forge", "Lava furnace" };
	}

}
