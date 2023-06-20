package com.zenyte.game.item;

import com.zenyte.game.world.entity.pathfinding.events.player.FloorItemEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.FloorItemStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.flooritem.FloorItem;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kris | 10. nov 2017 : 23:21.19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
@Slf4j
public final class ItemOnFloorItemHandler {

	private static final Long2ObjectOpenHashMap<ItemOnFloorItemAction> INT_ACTIONS = new Long2ObjectOpenHashMap<ItemOnFloorItemAction>();
	private static final Long2ObjectOpenHashMap<ItemOnFloorItemAction> ITEM_STRING_ACTIONS = new Long2ObjectOpenHashMap<ItemOnFloorItemAction>();
	private static final Long2ObjectOpenHashMap<ItemOnFloorItemAction> OBJECT_STRING_ACTIONS = new Long2ObjectOpenHashMap<ItemOnFloorItemAction>();
	private static final Long2ObjectOpenHashMap<ItemOnFloorItemAction> STRING_ACTIONS = new Long2ObjectOpenHashMap<ItemOnFloorItemAction>();
	private static final Map<Object, ItemOnFloorItemAction> ALL_ITEMS_USABLE = new HashMap<Object, ItemOnFloorItemAction>();
	
	public static final void add(final Class<?> c) {
		try {
			if (c.isAnonymousClass()) {
				return;
			}
			if (c.isMemberClass()) {
				return;
			}
			final Object o = c.newInstance();
			if (!(o instanceof ItemOnFloorItemAction)) {
				return;
			}
			final ItemOnFloorItemAction action = (ItemOnFloorItemAction) o;
			if (action.getItems() == null) {
				for (final Object object : action.getFloorItems()) {
					if (object instanceof String) {
						ALL_ITEMS_USABLE.put(object.toString().toLowerCase(), action);
					} else {
						ALL_ITEMS_USABLE.put(object, action);
					}
				}
				return;
			}
			for (final Object item : action.getItems()) {
				for (final Object object : action.getFloorItems()) {
					if (item instanceof Integer) {
						if (object instanceof Integer) {
							INT_ACTIONS.put((((long) ((Integer) item)) << 32) | (((Integer) object).intValue() & 0xffffffffL), action);
						} else if (object instanceof String) {
							OBJECT_STRING_ACTIONS.put((((long) ((Integer) item).intValue()) << 32) | (((String) object).toLowerCase().hashCode() & 0xffffffffL), action);
						}
					} else if (item instanceof String) {
						if (object instanceof String) {
							STRING_ACTIONS.put((((long) ((String) item).toLowerCase().hashCode()) << 32) | (((String) object).toLowerCase().hashCode() & 0xffffffffL), action);
						} else if (object instanceof Integer) {
							ITEM_STRING_ACTIONS.put((((long) ((String) item).toLowerCase().hashCode()) << 32) | (((Integer) object).intValue() & 0xffffffffL), action);
						}
					}
				}
			}
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}
	
	public static final void handleItemOnFloorItem(final Player player, final Item item, final FloorItem floorItem) {
		if (player.isLocked() || player.isFullMovementLocked()) {
			return;
		}
		player.stopAll(false, true, true);
		final int itemId = item.getId();
		final int objectId = floorItem.getId();
		final String objectName = floorItem.getDefinitions().getName().toLowerCase();
		ItemOnFloorItemAction action;
		if ((action = ALL_ITEMS_USABLE.get(objectId)) != null || (action = ALL_ITEMS_USABLE.get(objectName)) != null) {
			action.handle(player, item, floorItem);
			return;
		}
		final int itemHash = item.getName().toLowerCase().hashCode();
		final int objectHash = objectName.hashCode();
		long hash = (((long) itemId) << 32) | (objectId & 0xffffffffL);
		action = INT_ACTIONS.get(hash);
		if (action == null) {
			hash = (((long) itemId) << 32) | (objectHash & 0xffffffffL);
			action = OBJECT_STRING_ACTIONS.get(hash);
			if (action == null) {
				hash = (((long) itemHash) << 32) | (objectId & 0xffffffffL);
				action = ITEM_STRING_ACTIONS.get(hash);
				if (action == null) {
					hash = (((long) itemHash) << 32) | (objectHash & 0xffffffffL);
					action = STRING_ACTIONS.get(hash);
				}
			}
		}
		if (action != null) {
			action.handle(player, item, floorItem);
			return;
		} else {
			player.setRouteEvent(new FloorItemEvent(player, new FloorItemStrategy(floorItem), () -> {
				player.stopAll();
				player.sendMessage("Nothing interesting happens.");
			}));
		}
	}
	
}
