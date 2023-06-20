package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 27-10-2018 | 19:10
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ClanChatInterface extends Interface {

    @Override
    protected void attach() {
        put(24, "Open clan chat set-up");//Component updated.
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    protected void build() {
        bind("Open clan chat set-up", player -> {
            if (player.getInterfaceHandler().containsInterface(InterfacePosition.CENTRAL)) {
                player.sendMessage("Please close the interface you have open before using 'Clan Setup'.");
                return;
            }
            if (player.isUnderCombat()) {
                player.sendMessage("You can't do this while in combat.");
                return;
            }
            GameInterface.CLAN_CHAT_SETUP.open(player);
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.CLAN_CHAT_TAB;
    }
}
