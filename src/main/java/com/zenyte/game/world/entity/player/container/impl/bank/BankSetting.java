package com.zenyte.game.world.entity.player.container.impl.bank;

import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import static com.zenyte.game.ui.testinterfaces.BankInterface.VAR_CURRENT_TAB;

/**
 * @author Kris | 20. mai 2018 : 16:41:03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum BankSetting {

    REARRANGE_MODE(3959),
    WITHDRAW_MODE(3958),
    ALWAYS_PLACEHOLDER(3755),
    INCINERATOR(5102),
    DEPOSIT_ITEMS(5364),
    FILL_BANK(-1);

    private static final BankSetting[] VALUES = values();

    private final int varbitId;

    BankSetting(final int varbitId) {
        this.varbitId = varbitId;
    }

    public void updateVar(final Player player) {
        if (varbitId == -1)
             return;
        val bank = player.getBank();
        player.getVarManager().sendBit(varbitId, this == BankSetting.DEPOSIT_ITEMS ? (bank.getSetting(BankSetting.DEPOSIT_ITEMS) == 1 ? 0 : 1) : bank.getSetting(this));
        if (this == WITHDRAW_MODE) {
            player.getVarManager().sendBit(VAR_CURRENT_TAB, (bank.getCurrentTab() + 1) % 10);
        }
    }

    public static final void update(final Player player) {
        val bank = player.getBank();
        for (final BankSetting setting : BankSetting.VALUES) {
            if (setting.varbitId == -1)
                continue;
            player.getVarManager().sendBit(setting.varbitId, setting == BankSetting.DEPOSIT_ITEMS ? (bank.getSetting(BankSetting.DEPOSIT_ITEMS) == 1 ? 0 : 1) : bank.getSetting(setting));
        }
        bank.refreshQuantity();
    }

}
