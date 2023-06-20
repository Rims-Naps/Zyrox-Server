package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;

/**
 * @author Kris | 07/01/2019 15:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class FixedPaneInterface extends Interface {

    @Override
    protected void attach() {
        put(32, "Open Game Noticeboard");
        put(54, "Toggle spell filtering");
    }

    @Override
    public void open(Player player) {
        throw new IllegalStateException("Panes cannot be opened as interfaces.");
    }

    @Override
    protected void build() {
        bind("Toggle spell filtering", (player, slotId, itemId, option) -> {
            if (option == 2) {
                player.getSettings().toggleSetting(Setting.SPELL_FILTERING);
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.FIXED_PANE;
    }
}
