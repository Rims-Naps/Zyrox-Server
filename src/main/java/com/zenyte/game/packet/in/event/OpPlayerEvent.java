package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerHandler;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:53
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class OpPlayerEvent implements ClientProtEvent {

    private final int index, option;
    private final boolean run;

    @Override
    public void log(@NotNull final Player player) {
        val target = World.getPlayers().get(index);
        if (target == null) {
            log(player,
                    "Index: " + index + ", option: " + option + ", run: " + run);
            return;
        }
        val tile = target.getLocation();
        log(player,
                "Index: " + index + ", option: " + option + ", run: " + run + "; name: " + target.getUsername() + ", location: x" + tile.getX() + ", y" + tile.getY() + ", z: " + tile.getPlane());
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    @Override
    public void handle(Player player) {
        val target = World.getPlayers().get(index);
        if (target == null || target == player || target.isFinished() || !target.isRunning()) {
            return;
        }
        PlayerHandler.handle(player, target, run, option);
    }
}
