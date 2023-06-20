package com.zenyte.plugins.item;

import com.zenyte.game.content.skills.magic.spells.teleports.TeleportCollection;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

/**
 * @author Kris | 23/04/2019 23:10
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MythicalCape extends ItemPlugin {
    @Override
    public void handle() {
        bind("Teleport", (player, item, slotId) -> TeleportCollection.MYTHICAL_CAPE.teleport(player));
    }

    @Override
    public int[] getItems() {
        return new int[] {
                22114, 32184
        };
    }
}
