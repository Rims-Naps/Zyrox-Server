package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.perk.PerkWrapper;

/**
 * @author Tommeh | 07/06/2019 | 23:58
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PerkScrollItem extends ItemPlugin {

    @Override
    public void handle() {
        /*bind("Redeem", (player, item, slotId) -> {
            val perk = PerkWrapper.get(item.getId());
            if (perk == null) {
                return;
            }
            if (player.getPerkManager().unlock(perk)) {
                player.getInventory().deleteItem(item);
            }
        });*/
    }

    @Override
    public int[] getItems() {
        return new int[0];
    }
}
