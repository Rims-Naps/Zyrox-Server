package com.zenyte.game.content.skills.agility.brimhavenminigame;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Cresinkel
 */
public class BrimhavenAgilityOverlay extends Interface {
    @Override
    public void attach() {
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
    }

    @Override
    public void build() {
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.BRIMHAVEN_AGILITY;
    }
}
