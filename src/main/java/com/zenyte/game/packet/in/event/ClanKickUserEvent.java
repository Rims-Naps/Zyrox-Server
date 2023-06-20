package com.zenyte.game.packet.in.event;

import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 19:53
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class ClanKickUserEvent implements ClientProtEvent {

    private final String name;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Username: " + name);
    }

    @Override
    public void handle(Player player) {
        val target = World.getPlayerByUsername(name);
        if (target == null) {
            return;
        }
        ClanManager.kick(player, true, target, false);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
