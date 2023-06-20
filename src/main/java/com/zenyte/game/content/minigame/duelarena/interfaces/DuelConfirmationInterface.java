package com.zenyte.game.content.minigame.duelarena.interfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.minigame.duelarena.DuelStage;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;

import java.util.Optional;

/**
 * @author Tommeh | 27-10-2018 | 20:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class DuelConfirmationInterface extends Interface {

    @Override
    protected void attach() {
        put(72, "Confirm");
        put(74, "Decline");
    }

    @Override
    public void open(Player player) {
        player.getInterfaceHandler().sendInterface(getInterface());
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        Optional.ofNullable(player.getDuel()).ifPresent(duel -> duel.close(true));
    }

    @Override
    protected void build() {
        bind("Confirm", player -> {
            if (!player.inArea("Duel Arena")) {
                return;
            }
            player.getDuel().confirm(DuelStage.CONFIRMATION);
        });
        bind("Decline", player -> {
            if (!player.inArea("Duel Arena")) {
                return;
            }
            player.getDuel().close(true);
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.DUEL_CONFIRMATION;
    }
}
