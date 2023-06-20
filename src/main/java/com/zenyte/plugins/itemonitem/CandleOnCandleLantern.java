package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;

/**
 * @author Kris | 10/05/2019 14:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CandleOnCandleLantern implements PairedItemOnItemPlugin {
    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        val lantern = from.getId() == 4527 ? from : to;
        val candle = from == lantern ? to : from;
        val id = candle.getId();
        val product = id == 36 ? 4529 : id == 33 ? 4531 : id == 38 ? 4532 : 4534;
        player.getInventory().deleteItem(lantern);
        player.getInventory().deleteItem(candle);
        player.getInventory().addOrDrop(new Item(product, 1));
        player.sendMessage("You carefully place the candle inside the lantern.");
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {
                ItemPair.of(36, 4527), ItemPair.of(33, 4527),
                ItemPair.of(38, 4527), ItemPair.of(32, 4527)
        };
    }
}
