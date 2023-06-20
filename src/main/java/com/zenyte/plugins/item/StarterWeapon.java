package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;

/**
 * @author Tommeh | 20/05/2019 | 18:32
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class StarterWeapon extends ItemPlugin {

    @Override
    public void handle() {
        bind("Check", (player, item, slotId) -> player.getChargesManager().checkCharges(item, false));
    }

    @Override
    public int[] getItems() {
        return new int[] { 22333, 22335 };
    }
}
