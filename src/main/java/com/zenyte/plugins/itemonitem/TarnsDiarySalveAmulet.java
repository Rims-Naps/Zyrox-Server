package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 20/06/2019 22:47
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class TarnsDiarySalveAmulet implements PairedItemOnItemPlugin {

    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        val book = from.getId() == 10587 ? from : to;
        val salve = from == book ? to : from;
        player.getInventory().deleteItem(salve);
        player.getInventory().addOrDrop(new Item(salve.getId() == 4081 ? 10588 : 12018));
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {
                ItemPair.of(4081, 10587),
                ItemPair.of(12017, 10587)
        };
    }
}
