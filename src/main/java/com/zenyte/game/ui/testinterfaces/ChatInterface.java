package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;

/**
 * @author Tommeh | 27-10-2018 | 19:33
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ChatInterface extends Interface {

    @Override
    protected void attach() {
        put(8, "Set game filter");
        put(18, "Set private filter");
        put(19, "Set clan filter");
        put(33, "Report button");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    protected void build() {
        bind("Set game filter", (player, slotId, itemId, option) -> {
            if (option == 2) {
                player.getSettings().toggleSetting(Setting.GAME_FILTER);
            }
        });
        bind("Set private filter", (player, slotId, itemId, option) -> player.getSocialManager().updateStatus());
        bind("Set clan filter", (player, slotId, itemId, option) -> {
            if (option >= 3 && option <= 5) {
                player.getSettings().setSetting(Setting.CLAN_FILTER, option - 3);
            }
        });
        bind("Report button", (player, slotId, itemId, option) -> {
            if (option == 4) {
                player.getPacketDispatcher().sendURL("https://forums.zenyte.com/forum/51-report-a-bug/");
            } else if (option == 2) {
                player.getPacketDispatcher().sendURL("https://forums.zenyte.com/forum/49-report-a-player/");
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.CHAT;
    }
}
