package com.zenyte.game.item;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.events.player.EntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Kris | 11. mai 2018 : 00:45:13
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public interface ItemOnNPCAction {

	public void handleItemOnNPCAction(final Player player, final Item item, final int slot, final NPC npc);
	
	public Object[] getItems();
	
	public Object[] getObjects();
	
	public default void handle(final Player player, final Item item, final int slot, final NPC npc) {
		player.setRouteEvent(new EntityEvent(player, new EntityStrategy(npc), () -> {
			player.stopAll();
			player.faceEntity(npc);
			if (player.getInventory().getItem(slot) != item) {
				return;
			}
			handleItemOnNPCAction(player, item, slot, npc);
		}, true));
	}
	
}
