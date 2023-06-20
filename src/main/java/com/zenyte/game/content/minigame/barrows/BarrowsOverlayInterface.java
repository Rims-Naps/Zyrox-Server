package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 30/11/2018 16:44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BarrowsOverlayInterface extends Interface {

    @Override
    protected void attach() {

    }

    @Override
    public void open(Player player) {
        val barrows = player.getBarrows();
        if (barrows.isLooted()) {
            return;
        }
        player.getInterfaceHandler().sendInterface(getInterface());
        barrows.refreshInterface();
    }

    @Override
    protected void build() {

    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.BARROWS_OVERLAY;
    }
}
