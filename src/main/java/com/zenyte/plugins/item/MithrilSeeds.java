package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.action.misc.MithrilSeedPlanting;
import lombok.val;

/**
 * @author Tommeh | 28-4-2019 | 14:47
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MithrilSeeds extends ItemPlugin {

    @Override
    public void handle() {
        bind("Plant", (player, item, slotId) -> {
            val flowers = MithrilSeedPlanting.Flowers.random();
            player.getActionManager().setAction(new MithrilSeedPlanting(flowers));
        });
    }

    @Override
    public int[] getItems() {
        return new int[] { 299 };
    }
}
