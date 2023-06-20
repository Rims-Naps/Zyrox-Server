package com.zenyte.plugins.item;

import com.google.common.collect.ImmutableMap;
import com.zenyte.game.item.pluginextensions.ItemPlugin;

import java.util.Map;

/**
 * @author Cresinkel
 */
public class GnomishFirelighter extends ItemPlugin {

    public static final Map<Integer, String> LIGHTERS = ImmutableMap.<Integer, String>builder().put(7329, "Red firelighter").put(7330, "Green firelighter")
            .put(7331, "Blue firelighter").put(10326, "Purple firelighter").put(10327, "White firelighter").build();
    @Override
    public void handle() {
        bind("Check", (player, item, slotId) -> {
            player.getGnomishFirelighter().check();
        });

        bind("Fill", (player, item, slotId) -> {
            player.getGnomishFirelighter().fill();
        });

        bind("Empty", (player, item, slotId) -> {
            player.getGnomishFirelighter().empty(player.getInventory().getContainer());
        });
    }

    @Override
    public int[] getItems() {
        return new int[]{com.zenyte.game.item.containers.GnomishFirelighter.GNOMISH_FIRELIGHTER.getId()};
    }

}
