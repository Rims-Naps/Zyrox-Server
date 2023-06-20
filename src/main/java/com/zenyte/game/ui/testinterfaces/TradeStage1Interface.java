package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Examine;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.TradeStatus;
import lombok.val;

import java.util.Optional;

/**
 * @author Tommeh | 10-3-2019 | 18:59
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class TradeStage1Interface extends Interface {

    @Override
    protected void attach() {
        put(10, "Accept");
        put(25, "Remove Item");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        if (!replacement.isPresent() || !replacement.get().equals(GameInterface.TRADE_STAGE2)) {
            player.getTrade().closeTrade(TradeStatus.CANCEL);
            player.getInterfaceHandler().closeInterfaces();
        }
    }

    @Override
    protected void build() {
        bind("Accept", player -> player.getTrade().accept(1));
        bind("Remove Item", (player, slotId, itemId, option) -> {
            if (option == 10) {
                Examine.sendItemExamine(player, itemId);
                return;
            }
            val trade = player.getTrade();
            val item = trade.getContainer().get(slotId);
            if (item == null) {
                return;
            }
            if (option == 5) {
                player.sendInputInt("Enter amount:", amount -> trade.removeItem(slotId, amount));
            } else {
                val amount = option == 1 ? 1 : option == 2 ? 5 : option == 3 ? 10 : trade.getContainer().getAmountOf(item.getId());
                trade.removeItem(slotId, amount);
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TRADE_STAGE1;
    }
}
