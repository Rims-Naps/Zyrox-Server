package com.zenyte.game.item;

import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 10. nov 2017 : 23:59.43
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
@Slf4j
public final class ItemOnItemHandler {

	private static final Long2ObjectOpenHashMap<ItemOnItemAction> INT_ACTIONS = new Long2ObjectOpenHashMap<>();

	private static final Int2ObjectOpenHashMap<ItemOnItemAction> useOnAny = new Int2ObjectOpenHashMap<>();
	
	public static final void add(final Class<?> c) {
		try {
			if (c.isAnonymousClass() || c.isInterface()) {
				return;
			}
			final Object o = c.newInstance();
			if (!(o instanceof ItemOnItemAction)) {
				return;
			}
			final ItemOnItemAction action = (ItemOnItemAction) o;
			val include = action.includeEquivalentItems();
			val pairs = action.getMatchingPairs();
			if (pairs != null) {
				for (val pair : pairs) {
					val left = pair.getLeft();
					val right = pair.getRight();
					val first = Math.max(left, right);
					val second = Math.min(left, right);
					INT_ACTIONS.put((((long) (first)) << 32) | (second & 0xffffffffL), action);
				}
				return;
			}
			val items = action.getItems();
			val length = items.length;
			if (action.allItems()) {
			    for (val item : items) {
			        useOnAny.put(item, action);
                }
			} else {
				for (int i = length - 1; i >= 0; i--) {
					val itemUsed = items[i];
					for (int a = length - 1; a >= 0; a--) {
						val usedWith = items[a];
						if (itemUsed == usedWith && !include) {
							continue;
						}
						val first = Math.max(itemUsed, usedWith);
						val second = Math.min(itemUsed, usedWith);
						INT_ACTIONS.put((((long) (first)) << 32) | (second & 0xffffffffL), action);
					}
				}
			}
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}
	
	public static final void handleItemOnItem(final Player player, final Item used, final Item usedWith, final int fromSlot, final int toSlot) {
		//player.stopAll();
        player.stop(Player.StopType.INTERFACES);
		final int itemId = used.getId();
		final int usedWithId = usedWith.getId();
		
		val first = Math.max(itemId, usedWithId);
		val second = Math.min(itemId, usedWithId);
		
		final long hash = (((long) first) << 32) | (second & 0xffffffffL);
		ItemOnItemAction action = useOnAny.get(itemId);
		if (action == null) {
		    action = useOnAny.get(usedWithId);
		    if (action == null) {
                action = INT_ACTIONS.get(hash);
            }
        }
		if (action != null) {
		    log.info("[" + action.getClass().getSimpleName() + "] " + used.getName() + "(" + (used.getId() + " x " + used.getAmount())
                    + ") -> " + usedWith.getName() + "(" + (usedWith.getId() + " x " + usedWith.getAmount()) + ") | Slots: " + fromSlot + " -> " + toSlot);
			action.handleItemOnItemAction(player, used, usedWith, fromSlot, toSlot);
			return;
		}
		player.sendMessage("Nothing interesting happens.");
	}
	
}
