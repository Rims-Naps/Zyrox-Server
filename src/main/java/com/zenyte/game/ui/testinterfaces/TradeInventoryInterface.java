package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Tommeh | 10-3-2019 | 19:08
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class TradeInventoryInterface extends Interface {

    @Override
    protected void attach() {
        put(0, "Add Item");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    protected void build() {
        bind("Add Item", (player, slotId, itemId, option) -> {
            if (option == 10) {
                Examine.sendItemExamine(player, itemId);
                return;
            }
            val trade = player.getTrade();
            val item = player.getInventory().getItem(slotId);
            if (item == null) {
                return;
            }
            if (option == 5) {
                player.sendInputInt("Enter amount:", amount -> trade.addItem(slotId, amount));
            } else {
                val amount = option == 1 ? 1 : option == 2 ? 5 : option == 3 ? 10 : player.getInventory().getAmountOf(item.getId());
                trade.addItem(slotId, amount);
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TRADE_INVENTORY;
    }
}
