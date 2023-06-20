package com.zenyte.game.content.skills.construction;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Plugin;
import lombok.AllArgsConstructor;
import lombok.val;

/**
 * @author Kris | 27/03/2019 15:08
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Plugin
public class SawmillInterface extends Interface {

    @Override
    protected void attach() {
        put(106, "Buy 1 wood");
        put(104, "Buy 5 wood");
        put(103, "Buy 10 wood");
        put(102, "Buy x wood");
        put(93, "Buy all wood");

        put(111, "Buy 1 oak");
        put(110, "Buy 5 oak");
        put(109, "Buy 10 oak");
        put(108, "Buy x oak");
        put(94, "Buy all oak");

        put(116, "Buy 1 teak");
        put(115, "Buy 5 teak");
        put(114, "Buy 10 teak");
        put(113, "Buy x teak");
        put(95, "Buy all teak");

        put(121, "Buy 1 mahogany");
        put(120, "Buy 5 mahogany");
        put(119, "Buy 10 mahogany");
        put(118, "Buy x mahogany");
        put(96, "Buy all mahogany");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(this);
    }

    @Override
    protected void build() {
        for (val option : BuyOption.values()) {
            val optionName = Utils.formatString(option.name());
            for (val plank : Plank.values) {
                val plankName = plank.name().toLowerCase();
                bind(optionName + " " + plankName, player -> {
                    if (option.amount == -1) {
                        player.sendInputInt("Enter amount:", amt -> player.getActionManager().setAction(new Sawmill(plank, amt)));
                    } else {
                        player.getActionManager().setAction(new Sawmill(plank, option.amount));
                    }
                });
            }
        }

    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.SAWMILL;
    }

    @AllArgsConstructor
    private enum BuyOption {
        BUY_1(1),
        BUY_5(5),
        BUY_10(10),
        BUY_X(-1),
        BUY_ALL(28);

        private final int amount;
    }
}
