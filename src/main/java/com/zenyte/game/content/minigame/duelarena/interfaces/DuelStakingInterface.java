package com.zenyte.game.content.minigame.duelarena.interfaces;

import com.google.common.base.Preconditions;
import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.minigame.duelarena.DuelStage;
import com.zenyte.game.item.Item;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Kris | 13/03/2019 16:47
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DuelStakingInterface extends Interface {

    @Override
    protected void attach() {
        put(14, "Load Previous Stake");
        put(19, "Platinum quantity");
        put(20, "GP quantity");
        put(24, "Target name");
        put(74, "Confirm");
        put(75, "Close");
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        if (!replacement.isPresent() || !replacement.get().equals(GameInterface.DUEL_CONFIRMATION)) {
            Optional.ofNullable(player.getDuel()).ifPresent(duel -> duel.close(true));
        }
    }

    @Override
    public void open(Player player) {
        val duel = player.getDuel();
        Preconditions.checkArgument(duel != null, "Cannot open the interface manually.");
        val opponent = duel.getOpponent();
        Preconditions.checkArgument(opponent != null, "Opponent is null.");
        val dispatcher = player.getPacketDispatcher();
        player.getInterfaceHandler().sendInterface(this);
        player.getInterfaceHandler().sendInterface(InterfacePosition.SINGLE_TAB, 421);
        dispatcher.sendClientScript(149, 421 << 16 | 1, 93, 4, 7, 0, -1, "Use", "", "", "", "");
        dispatcher.sendComponentSettings(421, 1, 0, 27, AccessMask.CLICK_OP1, AccessMask.CLICK_OP10);
        dispatcher.sendComponentSettings(getInterface(), getComponent("Platinum quantity"), 0, 5, AccessMask.CLICK_OP1);
        dispatcher.sendComponentSettings(getInterface(), getComponent("GP quantity"), 0, 5, AccessMask.CLICK_OP1);
        dispatcher.sendComponentText(getInterface(), getComponent("Target name"), opponent.getName() + "'s stake:");
        dispatcher.sendUpdateItemContainer(duel.getContainer(player));
        dispatcher.sendUpdateItemContainer(duel.getContainer(opponent));
        duel.updateInventory();
    }

    @Override
    protected void build() {
        bind("Load Previous Stake", player -> {
            val duel = player.getDuel();
            val attr = player.getAttributes().get("lastDuelStake");
            if (!(attr instanceof Item[])) {
                return;
            }
            val stake = (Item[]) attr;
            val coins = stake[0];
            val tokens = stake[1];
            for (val item : stake) {
                if (!player.getInventory().containsItem(item)) {
                    player.sendMessage("You don't have enough coins/platinum tokens to load your previous stake.");
                    break;
                }
            }
            duel.setItem(995, coins.getAmount());
            duel.setItem(13204, tokens.getAmount());
        });
        bind("Platinum quantity", (player, slotId, itemId, option) -> {
            val duel = Objects.requireNonNull(player.getDuel());
            if (player.isIronman()) {
                player.sendMessage("You're an Iron Man. You stand alone.");
                return;
            }
            if (duel.getOpponent().isIronman()) {
                player.sendMessage(duel.getOpponent().getName() + " is an Iron Man. He stands alone.");
                return;
            }
            if (slotId == 0) {
                duel.removeItem(13204, 1);
            } else if (slotId >= 1 && slotId <= 4) {
                if (!player.getInventory().containsItem(13204, 1)) {
                    player.sendMessage("Not enough platinum left to do that.");
                    return;
                }
                val amount = slotId == 1 ? 1 : slotId == 2 ? 10 : slotId == 3 ? 100 : 1000;
                duel.addItem(13204, amount);
            } else if (slotId == 5) {
                player.sendInputInt("How many coins would you like to stake?", amt -> {
                    val d = Objects.requireNonNull(player.getDuel());
                    d.setItem(13204, amt);
                });
            }
        });

        bind("GP quantity", (player, slotId, itemId, option) -> {
            val duel = Objects.requireNonNull(player.getDuel());
            if (player.isIronman()) {
                player.sendMessage("You're an Iron Man. You stand alone.");
                return;
            }
            if (duel.getOpponent().isIronman()) {
                player.sendMessage(duel.getOpponent().getName() + " is an Iron Man. He stands alone.");
                return;
            }
            if (slotId == 0) {
                duel.removeItem(995, 1);
            } else if (slotId >= 1 && slotId <= 4) {
                if (!player.getInventory().containsItem(995, 1)) {
                    player.sendMessage("Not enough coins left to do that.");
                    return;
                }
                val amount = slotId == 1 ? 1 : slotId == 2 ? 100000 : slotId == 3 ? 1000000 : 10000000;
                duel.addItem(995, amount);
            } else if (slotId == 5) {
                player.sendInputInt("How many coins would you like to stake?", amt -> {
                    val d = Objects.requireNonNull(player.getDuel());
                    d.setItem(995, amt);
                });
            }
        });

        bind("Confirm", player -> Objects.requireNonNull(player.getDuel()).confirm(DuelStage.STAKE));
        bind("Close", player -> Objects.requireNonNull(player.getDuel()).close(true));
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.DUEL_STAKING;
    }
}