package com.zenyte.plugins.item;

import com.zenyte.game.content.minigame.inferno.instance.Inferno;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import lombok.val;

/**
 * @author Kris | 14/06/2019 09:02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class NestBoxPlugin extends ItemPlugin {
    @Override
    public void handle() {
        bind("Check", (player, item, container, slotId) -> player.sendMessage("Your nest box currently holds " + item.getCharges() + " bird nests."));
        bind("Extract", (player, item, container, slotId) -> {
            if (!player.getInventory().hasFreeSlots()) {
                player.sendMessage("You need some free inventory space to extract the box.");
                return;
            }
            if (player.getArea() instanceof Inferno) {
                player.sendMessage("You can't open these nests in such heat, they'll burn.");
                return;
            }
            val amount = item.getCharges();
            val extractableAmount = player.getInventory().getFreeSlots();
            val extractedAmount = Math.min(amount, extractableAmount);
            if (extractedAmount <= 0) {
                player.sendFilteredMessage("Not enough space in your inventory.");
                return;
            }
            if (extractedAmount >= amount) {
                player.getInventory().deleteItem(slotId, item);
            } else {
                item.setCharges(amount - extractedAmount);
            }
            val id = item.getId();
            val nestId = id == 12792 ? 5075 : id == 12793 ? 5073 : 5074;
            player.getInventory().addOrDrop(new Item(nestId, extractedAmount));
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {
                12792, 12793, 12794
        };
    }
}
