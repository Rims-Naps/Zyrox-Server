package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:55
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class PingStatisticsEvent implements ClientProtEvent {

    private final int gc, fps;
    private final long ms;

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "GC: " + gc + ", FPS: " + fps + ", MS: " + ms);
    }

    @Override
    public void handle(Player player) {
        val currentTime = System.currentTimeMillis();
        player.sendMessage("Time since last cycle: " + (currentTime - ms) + "ms, FPS: " + fps + ", GC: " + gc);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
