package com.zenyte.game.item;

import com.zenyte.game.world.entity.pathfinding.events.player.EntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 15/06/2019 10:03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class ItemOnPlayerHandler {

    private static final Int2ObjectMap<ItemOnPlayerPlugin> plugins = new Int2ObjectOpenHashMap<>();

    public static final void add(final Class<? extends ItemOnPlayerPlugin> clazz) {
        try {
            if (clazz.isAnonymousClass()) {
                return;
            }
            if (clazz.isMemberClass()) {
                return;
            }
            val o = clazz.newInstance();
            for (val item : o.getItems()) {
                plugins.put(item, o);
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public static final void handleItemOnPlayer(final Player player, final Item item, final int slotId, final Player target) {
        if (player.isLocked() || player.isFullMovementLocked()) {
            return;
        }
        player.stopAll(false, true, true);
        if (player.isFrozen()) {
            player.sendMessage("A magical force stops you from moving.");
            return;
        }
        if (player.isStunned()) {
            player.sendMessage("You're stunned.");
            return;
        }
        if (player.isMovementLocked(true)) {
            return;
        }
        val action = plugins.get(item.getId());
        if (action != null) {
            log.info("[" + action.getClass().getSimpleName() + "] " + item.getName() + "(" + item.getId() + " x " + item.getAmount() + ") -> " + target);
            action.handle(player, item, slotId, target);
        } else {
            player.setRouteEvent(new EntityEvent(player, new EntityStrategy(target), () -> {
                player.stopAll();
                player.faceEntity(target);
                player.sendMessage("Nothing interesting happens.");
            }, true));
        }
    }

}
