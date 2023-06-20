package com.zenyte.game.packet.in.event;

import com.zenyte.game.item.ItemActionHandler;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:38
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpHeldEvent implements ClientProtEvent {

    private final int slotId, itemId, option;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Item: " + itemId + ", slot: " + slotId + ", option: " + option);
    }

    @Override
    public void handle(Player player) {
        ItemActionHandler.handle(player, itemId, slotId, option);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
