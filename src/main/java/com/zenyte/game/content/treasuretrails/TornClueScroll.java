package com.zenyte.game.content.treasuretrails;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import lombok.val;
import lombok.var;

/**
 * @author Kris | 22/01/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class TornClueScroll extends ItemPlugin {
    @Override
    public void handle() {
        bind("Combine", (player, item, container, slotId) -> {
            val attributes = item.getAttributes();
            val inventory = player.getInventory();
            var hash = 0;
            for (val it : inventory.getContainer().getItems().values()) {
                if (it.getId() >= 19837 && it.getId() <= 19839) {
                    hash |= 1 << (it.getId() - 19837);
                }
            }
            if (hash != (0x1 | 0x2 | 0x4)) {
                player.sendMessage("You do not have all the pieces to combine a full clue scroll.");
                return;
            }
            for (int i = 19837; i <= 19839; i++) {
                inventory.deleteItem(new Item(i));
            }
            val clue = new Item(ItemId.CLUE_SCROLL_MASTER, 1, attributes);
            inventory.addItem(clue);
            TreasureTrail.progressTripleCryptic(player, clue);
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {
                ItemId.TORN_CLUE_SCROLL_PART_1, ItemId.TORN_CLUE_SCROLL_PART_2, ItemId.TORN_CLUE_SCROLL_PART_3
        };
    }
}
