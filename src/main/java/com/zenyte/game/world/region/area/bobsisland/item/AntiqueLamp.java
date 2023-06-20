package com.zenyte.game.world.region.area.bobsisland.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import lombok.val;

import static com.zenyte.game.constants.GameInterface.EXPERIENCE_LAMP;

public class AntiqueLamp extends ItemPlugin
{
    @Override
    public void handle()
    {
        bind("Rub", (player, item, container, slotId) -> {
            val args = new Object[] {750, 1, slotId, item };
            player.getTemporaryAttributes().put("experience_lamp_info", args);
            EXPERIENCE_LAMP.open(player);
        });
    }

    @Override
    public int[] getItems()
    {
        return new int[]
                {
                  7498
                };
    }
}