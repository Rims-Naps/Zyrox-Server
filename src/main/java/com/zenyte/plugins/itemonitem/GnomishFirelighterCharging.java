package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.item.GnomishFirelighter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cresinkel
 */

public class GnomishFirelighterCharging implements ItemOnItemAction {

    private static final Map<Integer, String> LIGHTERS = new HashMap<Integer, String>() {{
        put(7329, "Red firelighter");
        put(7330, "Green firelighter");
        put(7331, "Blue firelighter");
        put(10326, "Purple firelighter");
        put(10327, "White firelighter");
    }};

    private static final Integer[] IDS = LIGHTERS.keySet().toArray(new Integer[LIGHTERS.size()]);

    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        if ((from.getId() != 20278 || to.getId() != 20278) && !(Arrays.asList(IDS).contains(from.getId()) || Arrays.asList(IDS).contains(to.getId()))) {
            return;
        }
        if (player.getInventory().containsItem(20278, 1)) {
            player.getGnomishFirelighter().fill();
        }
    }

    @Override
    public int[] getItems() {
        return new int[] {20278, 7329, 7330, 7331, 10326, 10327};
    }
}
