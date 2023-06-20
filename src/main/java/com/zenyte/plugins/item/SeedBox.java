package com.zenyte.plugins.item;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

/**
 * @author Tommeh | 2-1-2019 | 17:30
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class SeedBox extends ItemPlugin {

    @Override
    public void handle() {
        bind("Fill", (player, item, slotId) -> player.getSeedBox().fill());
        bind("Check", (player, item, slotId) -> GameInterface.SEED_BOX.open(player));
        bind("Empty", (player, item, slotId) -> player.getSeedBox().empty());
    }

    @Override
    public int[] getItems() {
        return new int[] { 13639 };
    }
}
