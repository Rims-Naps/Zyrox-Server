package com.zenyte.plugins.renewednpc;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.game.item.degradableitems.RepairableItem;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.lumbridge.BobD;
import lombok.val;

import java.util.HashMap;

/**
 * @author Kris | 26/11/2018 19:37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Bob extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new BobD(player, npc)));
        bind("Repair", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                val inventory = player.getInventory();
                val data = new HashMap<Integer, RepairableItem>();
                int total = 0;
                for (int slot = 0; slot < Inventory.SIZE; slot++) {
                    val item = inventory.getItem(slot);
                    if (item == null) {
                        continue;
                    }
                    val repairable = RepairableItem.getItem(item);
                    if (repairable == null || item.getId() == repairable.getIds()[0]) {
                        continue;
                    }
                    val degradable = DegradableItem.ITEMS.get(item.getId());
                    if (degradable != null && item.getCharges() >= degradable.getMaximumCharges()) continue;

                    data.put(slot, repairable);
                    total += (int) repairable.getCost(player, item, false);
                }
                if (data.isEmpty()) {
                    npc("You currently have no items in your inventory that need repairing.");
                    return;
                }
                val cost = new Item(995, total);
                npc("Repairing all items will cost you " + Utils.format(total) + " gold.");
                options("Do you wish to proceed?", "Yes.", "No.").onOptionOne(() -> {
                    if (inventory.containsItem(cost)) {
                        data.forEach((slot, repairable) -> {
                            val item = inventory.getItem(slot);
                            inventory.deleteItem(item);
                            val repaired = new Item(repairable.getIds()[0], 1);//Do not add charges cus the default item has 0 for tradability and stackability reasons.
                            inventory.addItem(repaired);
                        });
                        inventory.deleteItem(cost);
                    } else {
                        setKey(5);
                    }
                });
                plain(5, "You don't have enough gold to repair your item(s).");
            }
        }));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                505
        };
    }
}
