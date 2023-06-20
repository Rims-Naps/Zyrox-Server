package com.zenyte.plugins.item;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import lombok.val;

public class InfernoSkipScroll extends ItemPlugin {
    @Override
    public void handle() {
        bind("Read", (player, item, container, slotId) -> {
            val waveSkips = player.incrementNumericAttribute("inferno_wave_skip",1);
            player.getInventory().deleteItem(new Item(23550));
            player.sendMessage("You now have " + waveSkips + " wave 32 Inferno starts.");
        });
    }

    @Override
    public int[] getItems() {
        return new int[] {23550};
    }
}
