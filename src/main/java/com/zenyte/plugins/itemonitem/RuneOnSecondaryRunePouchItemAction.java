package com.zenyte.plugins.itemonitem;

import com.zenyte.game.content.skills.magic.Rune;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.RunePouch;
import lombok.val;

/**
 * @author Tommeh | 22/07/2019 | 21:52
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class RuneOnSecondaryRunePouchItemAction implements PairedItemOnItemPlugin {

    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        final int id = to.getId() == RunePouch.SECONDARY_RUNE_POUCH.getId() ? from.getId() : to.getId();
        final Item rune = new Item(id, player.getInventory().getAmountOf(id));

        int amount = rune.getAmount();
        val inPouch = player.getSecondaryRunePouch().getAmountOf(id);
        if ((amount + (long) inPouch) >= 16000) {
            amount = 16000 - inPouch;
        }
        rune.setAmount(amount);
        if (amount <= 0) {
            player.sendMessage("You can't put that many runes in your pouch.");
            return;
        }

        val slot = to.getId() == RunePouch.SECONDARY_RUNE_POUCH.getId() ? fromSlot : toSlot;
        val r = Rune.getRune(rune);
        if (r == null) {
            player.sendMessage("You can only add runes to the rune pouch.");
            return;
        }
        if (player.getSecondaryRunePouch().getContainer().getSize() == 3) {
            if (player.getSecondaryRunePouch().getAmountOf(id) == 0) {
                player.sendMessage("You can only carry three different types of runes in your rune pouch at a time.");
                return;
            }
        }
        player.getSecondaryRunePouch().getContainer().deposit(player, player.getInventory().getContainer(), slot, rune.getAmount());
        player.getInventory().refreshAll();
        player.getSecondaryRunePouch().getContainer().refresh(player);
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        val pairs = new ItemPair[Rune.values.length];
        for (int i = 0; i < pairs.length; i++) {
            val rune = Rune.values[i];
            pairs[i] = new ItemPair(RunePouch.SECONDARY_RUNE_POUCH.getId(), rune.getId());
        }
        return pairs;
    }

    @Override
    public int[] getItems() {
        return null;
    }

}
