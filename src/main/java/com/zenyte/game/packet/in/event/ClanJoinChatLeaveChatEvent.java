package com.zenyte.game.packet.in.event;

import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ClanJoinChatLeaveChatEvent implements ClientProtEvent {

    private final String name;
    private final String formattedName;

    public ClanJoinChatLeaveChatEvent(final String name) {
        this.name = name;
        this.formattedName = name.toLowerCase().replaceAll(" ", "_");
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Requested name: " + name + ", formatted name: " + formattedName);
    }

    @Override
    public void handle(Player player) {
        val current = player.getSettings().getChannel();
        if (current == null) {
            if (formattedName == null || formattedName.isEmpty()) {
                return;
            }
            ClanManager.join(player, formattedName);
        } else {
            ClanManager.leave(player, true);
        }
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
