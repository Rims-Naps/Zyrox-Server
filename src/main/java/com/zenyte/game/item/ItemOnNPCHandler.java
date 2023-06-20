package com.zenyte.game.item;

import com.zenyte.game.content.rottenpotato.plugin.RottenPotatoItemOnNpc;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.pathfinding.events.player.EntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.items.ItemDefinitions;
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
public final class ItemOnNPCHandler {

    private static final Long2ObjectOpenHashMap<ItemOnNPCAction> INT_ACTIONS = new Long2ObjectOpenHashMap<ItemOnNPCAction>();
    private static final Long2ObjectOpenHashMap<ItemOnNPCAction> ITEM_STRING_ACTIONS = new Long2ObjectOpenHashMap<ItemOnNPCAction>();
    private static final Long2ObjectOpenHashMap<ItemOnNPCAction> OBJECT_STRING_ACTIONS = new Long2ObjectOpenHashMap<ItemOnNPCAction>();
    private static final Long2ObjectOpenHashMap<ItemOnNPCAction> STRING_ACTIONS = new Long2ObjectOpenHashMap<ItemOnNPCAction>();
    private static final Map<Object, ItemOnNPCAction> ALL_ITEMS_USABLE = new HashMap<Object, ItemOnNPCAction>();
    private static final RottenPotatoItemOnNpc potatoNpcPlugin = new RottenPotatoItemOnNpc();

    public static final void add(final Class<?> c) {
        try {
            if (c.isAnonymousClass()) {
                return;
            }
            final Object o = c.newInstance();
            if (!(o instanceof ItemOnNPCAction)) {
                return;
            }
			final ItemOnNPCAction action = (ItemOnNPCAction) o;
			if (action.getItems() == null) {
				for (final Object object : action.getObjects()) {
					if (object instanceof String) {
						ALL_ITEMS_USABLE.put(object.toString().toLowerCase(), action);
					} else {
						ALL_ITEMS_USABLE.put(object, action);
					}
				}
				return;
			}
			for (final Object item : action.getItems()) {
				for (final Object object : action.getObjects()) {
					if (item instanceof Integer) {
						if ((Integer) item == -1) {
							for (int id = 0; id < ItemDefinitions.definitions.length; id++) {
								if (object instanceof Integer) {
									INT_ACTIONS.put((((long) ((Integer) id)) << 32) | (((Integer) object).intValue() & 0xffffffffL), action);
								} else if (object instanceof String) {
									OBJECT_STRING_ACTIONS.put((((long) ((Integer) id).intValue()) << 32) | (((String) object).toLowerCase().hashCode() & 0xffffffffL), action);
								}
							}
						} else {
							if (object instanceof Integer) {
								INT_ACTIONS.put((((long) ((Integer) item)) << 32) | (((Integer) object).intValue() & 0xffffffffL), action);
							} else if (object instanceof String) {
								OBJECT_STRING_ACTIONS.put((((long) ((Integer) item).intValue()) << 32) | (((String) object).toLowerCase().hashCode() & 0xffffffffL), action);
							}
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
	
	public static final void handleItemOnNPC(final Player player, final Item item, final int slotId, final NPC npc) {
    	if (player.isLocked() || player.isFullMovementLocked()) {
    		return;
		}
		player.stopAll(false, true, true);
		final int itemId = item.getId();
		final int objectId = npc.getId();
		final String npcName = npc.getName(player).toLowerCase();
		ItemOnNPCAction action;
		if ((action = ALL_ITEMS_USABLE.get(objectId)) != null || (action = ALL_ITEMS_USABLE.get(npcName)) != null) {
			action.handle(player, item, slotId, npc);
			return;
		}
		final int itemHash = item.getName().toLowerCase().hashCode();
		final int objectHash = npcName.hashCode();
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
        if (itemId == ItemId.ROTTEN_POTATO) {
            potatoNpcPlugin.handleItemOnNPCAction(player, item, slotId, npc);
            return;
        }
        if (action != null) {
            log.info("[" + action.getClass().getSimpleName() + "] " + item.getName() + "(" + item.getId() + " x " + item.getAmount() + ") -> " + npc.getName(player) + "(" + npc.getId() + ", Index: " + npc.getIndex() + ")");
            action.handle(player, item, slotId, npc);
            return;
        } else {
            player.setRouteEvent(new EntityEvent(player, new EntityStrategy(npc), () -> {
                player.stopAll();
                player.faceEntity(npc);
                player.sendMessage("Nothing interesting happens.");
            }, true));
		}
	}
	
}
