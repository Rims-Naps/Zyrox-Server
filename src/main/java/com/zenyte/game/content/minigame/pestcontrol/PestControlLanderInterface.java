package com.zenyte.game.content.minigame.pestcontrol;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.minigame.pestcontrol.area.AbstractLanderArea;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 13/12/2018 17:57
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PestControlLanderInterface extends Interface {

    @Override
    protected void attach() {
        put(4, "Next departure");
        put(5, "Players ready");
        put(6, "Pest points");
        put(8, "Lander flag");
        put(21, "Lander type");
    }

    @Override
    public void open(Player player) {
        val area = player.getArea();
        if (!(area instanceof AbstractLanderArea)) {
            player.sendMessage("You cannot open the lander overlay outside of landers.");
            return;
        }
        player.getInterfaceHandler().sendInterface(getInterface());
        PestControlUtilities.updateLanderInformation(player, (AbstractLanderArea) area);
    }

    @Override
    protected void build() {

    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.PEST_CONTROL_LANDER_OVERLAY;
    }
}
