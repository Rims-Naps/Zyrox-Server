package com.zenyte.game.packet.in.event;

import com.zenyte.game.item.ItemOnItemHandler;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 20:57
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpHeldUEvent implements ClientProtEvent {

    private final int fromSlotId, fromItemId, toSlotId, toItemId;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Item: " + fromItemId + " -> " + toItemId + ", slot: " + fromSlotId + " -> " + toSlotId);
    }

    @Override
    public void handle(Player player) {
        val from = player.getInventory().getItem(fromSlotId);
        val to = player.getInventory().getItem(toSlotId);
        if (from == null || to == null || player.isLocked()) {
            return;
        }
        ItemOnItemHandler.handleItemOnItem(player, from, to, fromSlotId, toSlotId);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
