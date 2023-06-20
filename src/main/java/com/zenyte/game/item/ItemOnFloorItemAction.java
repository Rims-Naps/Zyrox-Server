package com.zenyte.game.item;

import com.zenyte.game.world.entity.pathfinding.events.player.FloorItemEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.FloorItemStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.flooritem.FloorItem;

/**
 * @author Kris | 11. mai 2018 : 00:45:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public interface ItemOnFloorItemAction {

	public void handleItemOnFloorItemAction(final Player player, final Item item, final FloorItem floorItem);
	
	public Object[] getItems();
	
	public Object[] getFloorItems();
	
	public default void handle(final Player player, final Item item, final FloorItem floorItem) {
		player.setRouteEvent(new FloorItemEvent(player, new FloorItemStrategy(floorItem), () -> {	
			player.stopAll();
			if (!player.getInventory().containsItem(item)) {
				return;
			}
			handleItemOnFloorItemAction(player, item, floorItem);
		}));
	}
	
}
