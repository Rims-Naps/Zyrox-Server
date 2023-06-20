package com.zenyte.plugins.item;

import com.zenyte.game.item.enums.ImbueableItem;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import mgi.types.config.items.ItemDefinitions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.val;

import java.util.function.IntPredicate;

/**
 * @author Tommeh | 21-3-2019 | 13:32
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ImbuedItem extends ItemPlugin {

    @Override
    public void handle() {
        bind("Uncharge", (player, item, container, slotId) -> {
            val imbueable = ImbueableItem.get(item.getId());
            if (imbueable == null) {
                return;
            }
            val name = ItemDefinitions.getOrThrow(imbueable.getImbued()).getName();
            val uncharged = new Item(imbueable.getNormal());
            player.getDialogueManager().start(new Dialogue(player) {

                @Override
                public void buildDialogue() {
                    item(new Item(imbueable.getImbued()), "Uncharging your <col=00080>" + name + "</col> will not grant you back the imbue scroll.");
                    options("Are you sure you want to uncharge your <col=00080>" + name + "</col>?", "Yes, I'm sure.", "No.")
                            .onOptionOne(() -> {
                                player.getInventory().set(slotId, uncharged);
                                setKey(5);
                            });
                    item(5, uncharged, "Your <col=00080>" + name + "</col> was successfully uncharged.");
                }
            });
        });
    }

    @Override
    public int[] getItems() {
        val list = new IntArrayList();
        for (val item : ImbueableItem.values) {
            if (item.name().toLowerCase().contains("black_mask")) {
                continue;
            }
            val def = ItemDefinitions.get(item.getImbued());
            if (def != null && def.containsOption("Uncharge")) {
                list.add(item.getImbued());
            }
        }
        list.removeIf((IntPredicate) i -> i == 19710 || i == 20657);
        return list.toArray(new int[0]);
    }
}
