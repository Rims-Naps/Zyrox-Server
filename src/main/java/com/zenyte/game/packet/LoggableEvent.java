package com.zenyte.game.packet;

import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 24/05/2019 00:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public interface LoggableEvent {

    default void log(@NotNull final Player player) {
        //Empty by default, need to delegate the packets to log.
    }

    LogLevel level();

}
