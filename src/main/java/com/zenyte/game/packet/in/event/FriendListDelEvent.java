package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 20:22
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class FriendListDelEvent implements ClientProtEvent {

    private String name;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Name: " + name);
    }

    @Override
    public void handle(Player player) {
        name = name.toLowerCase().replaceAll(" ", "_");
        player.getSocialManager().removeFriend(name);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
