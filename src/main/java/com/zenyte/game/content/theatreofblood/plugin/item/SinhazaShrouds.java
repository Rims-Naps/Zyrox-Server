package com.zenyte.game.content.theatreofblood.plugin.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

/**
 * @author Cresinkel
 */

public class SinhazaShrouds extends ItemPlugin {

    public static final Item tier1 = new Item(ItemId.SINHAZA_SHROUD_TIER_1);
    public static final Item tier2 = new Item(ItemId.SINHAZA_SHROUD_TIER_2);
    public static final Item tier3 = new Item(ItemId.SINHAZA_SHROUD_TIER_3);
    public static final Item tier4 = new Item(ItemId.SINHAZA_SHROUD_TIER_4);
    public static final Item tier5 = new Item(ItemId.SINHAZA_SHROUD_TIER_5);

    private final String CANCEL = "This is the highest cape you can have.";

    @Override
    public void handle() {
        bind("Change", (player, item, slotId) -> {
            final int killcount = player.getNumericAttribute("theatreofblood").intValue();
            if (killcount < 25) {
                if (item.getId() != tier1.getId()) {
                    player.getInventory().deleteItem(item);
                    player.getInventory().addOrDrop(tier1);
                    return;
                } else {
                    player.sendMessage(CANCEL);
                    return;
                }
            }
            if (killcount < 75) {
                if (item.getId() != tier2.getId()) {
                    player.getInventory().deleteItem(item);
                    player.getInventory().addOrDrop(tier2);
                    return;
                } else {
                    player.sendMessage(CANCEL);
                    return;
                }
            }
            if (killcount < 150) {
                if (item.getId() != tier3.getId()) {
                    player.getInventory().deleteItem(item);
                    player.getInventory().addOrDrop(tier3);
                    return;
                } else {
                    player.sendMessage(CANCEL);
                    return;
                }
            }
            if (killcount < 300) {
                if (item.getId() != tier4.getId()) {
                    player.getInventory().deleteItem(item);
                    player.getInventory().addOrDrop(tier4);
                    return;
                } else {
                    player.sendMessage(CANCEL);
                    return;
                }
            }
            if (item.getId() != tier5.getId()) {
                player.getInventory().deleteItem(item);
                player.getInventory().addOrDrop(tier5);
                return;
            } else {
                player.sendMessage(CANCEL);
                return;
            }
        });
    }

    @Override
    public int[] getItems() {
        return new int[]{ItemId.SINHAZA_SHROUD_TIER_1, ItemId.SINHAZA_SHROUD_TIER_2, ItemId.SINHAZA_SHROUD_TIER_3, ItemId.SINHAZA_SHROUD_TIER_4, ItemId.SINHAZA_SHROUD_TIER_5};
    }
}
