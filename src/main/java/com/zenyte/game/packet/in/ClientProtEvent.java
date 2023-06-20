package com.zenyte.game.packet.in;

import com.zenyte.game.packet.LoggableEvent;
import com.zenyte.game.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 19:12
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public interface ClientProtEvent extends LoggableEvent {

    void handle(final Player player);

    default String name() {
         return "[Client prot: " + getClass().getSimpleName() + "] ";
    }

    default void log(@NotNull final Player player, final String text) {
        player.log(level(), name() + text);
    }

}
