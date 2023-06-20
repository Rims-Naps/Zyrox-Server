package com.zenyte.plugins.itemonflooritem;

import com.zenyte.game.content.skills.firemaking.Firemaking;
import com.zenyte.game.content.skills.firemaking.FiremakingAction;
import com.zenyte.game.content.skills.firemaking.FiremakingTool;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnFloorItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.flooritem.FloorItem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Kris | 11. mai 2018 : 15:12:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class FiremakingItemOnFIA implements ItemOnFloorItemAction {

	@Override
	public void handleItemOnFloorItemAction(final Player player, final Item item, final FloorItem floorItem) {
		val val = Firemaking.MAP.get(floorItem.getId());
		if (val == null) {
			return;
		}
		val tool = FiremakingTool.getTool(item.getId());
		if (!tool.isPresent()) {
			player.sendMessage("You cannot light a fire without the proper tool.");
			return;
		}
		player.getActionManager().setAction(new FiremakingAction(val, -1, true, tool.get(), Optional.of(floorItem)));
	}

	@Override
	public Object[] getItems() {
	    val list = new IntArrayList();
	    for (val tool : FiremakingTool.values) {
	        for (val id : tool.getBowIds())
	            list.add(id);
        }
        return list.toArray();
	}

	@Override
	public Object[] getFloorItems() {
		val list = new ArrayList<Integer>(Firemaking.VALUES.length);
		for (final Firemaking fm : Firemaking.VALUES) {
			list.add(fm.getLogs().getId());
		}
		return list.toArray(new Object[list.size()]);
	}

}
