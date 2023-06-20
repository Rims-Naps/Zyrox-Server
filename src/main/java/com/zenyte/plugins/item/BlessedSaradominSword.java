package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.ItemChat;
import lombok.val;

/**
 * @author Kris | 24/03/2019 13:18
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BlessedSaradominSword extends ItemPlugin implements PairedItemOnItemPlugin {
    @Override
    public void handle() {
        bind("Revert", (player, item, container, slotId) -> {
           player.getDialogueManager().start(new Dialogue(player) {
               @Override
               public void buildDialogue() {
                   item(item, "Reverting the Blessed Saradomin sword will consume the sword and only return you the Saradomin's tear.");
                   options("Revert the sword?", new DialogueOption("Revert it.", () -> {
                       player.getInventory().ifDeleteItem(item, () -> {
                           player.getDialogueManager().finish();
                           val tear = new Item(12804);
                           player.getInventory().addItem(tear);
                           player.getDialogueManager().start(new ItemChat(player, tear, "You revert the sword and reclaim the tear from it; the blade is destroyed in the process."));
                       });
                   }), new DialogueOption("Keep it."));
               }
           });
        });
    }

    @Override
    public void handleItemOnItemAction(Player player, Item from, Item to, int fromSlot, int toSlot) {
        player.getDialogueManager().start(new Dialogue(player) {
            @Override
            public void buildDialogue() {
                val blessedSword = new Item(12808);
                item(blessedSword, "A blessed Saradomin sword is untradeable, with an Attack requirement of 75. After 10,000 hits, the sword crumbles to dust, and you get the Tear " +
                        "back.");
                options("Combine the tear with the sword?",
                        new DialogueOption("Combine them.", () -> {
                            val inventory = player.getInventory();
                            val a = inventory.getItem(fromSlot);
                            val b = inventory.getItem(toSlot);
                            if (a != from || b != to) {
                                return;
                            }
                            inventory.deleteItem(fromSlot, from);
                            inventory.deleteItem(toSlot, to);
                            inventory.addItem(new Item(12808, 1, DegradableItem.getDefaultCharges(12808, 0)));
                        }),
                        new DialogueOption("No."));
            }
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {
                12808, 12809
        };
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        return new ItemPair[] {
                ItemPair.of(11838, 12804)
        };
    }
}
