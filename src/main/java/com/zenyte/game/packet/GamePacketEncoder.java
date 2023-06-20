package com.zenyte.game.packet;

import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 13:46:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public interface GamePacketEncoder extends LoggableEvent {

    default boolean prioritized() {
        return false;
    }

	GamePacketOut encode();

    default String name() {
        return "[Server prot: " + getClass().getSimpleName() + "] ";
    }

    default void log(@NotNull final Player player, final String text) {
        player.log(level(), name() + text);
    }


}
