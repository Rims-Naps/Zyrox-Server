package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 19:18
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@RequiredArgsConstructor
public class ChatSetModeEvent implements ClientProtEvent {

    private final int publicFilter, privateFilter, tradeFilter;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Public: " + publicFilter + ", private: " + privateFilter + ", trade: " + tradeFilter);
    }

    @Override
    public void handle(Player player) {
        player.getSettings().setSetting(Setting.PUBLIC_FILTER, publicFilter);
        player.getSettings().setSetting(Setting.PRIVATE_FILTER, privateFilter);
        player.getSettings().setSetting(Setting.TRADE_FILTER, tradeFilter);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
