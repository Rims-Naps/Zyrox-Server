package com.zenyte.plugins.drop.championscrolls;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.drop.matrix.DropProcessor;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Cresinkel
 */

public class GiantDropProcessor extends DropProcessor {
    @Override
    public void attach() {
        appendDrop(new DisplayedDrop(6800, 1, 1, 2500));
    }

    public void onDeath(final NPC npc, final Player killer) {
        if (Utils.random(2499) == 0) {
            npc.dropItem(killer, new Item(6800));
            killer.sendMessage(Colour.BRICK.wrap("A Champion's scroll falls to the ground as you slay your opponent."));
        }
    }

    @Override
    public int[] ids() {
        return new int[]{
                2097, 2137, 2138, 2139, 2140, 2141, 2142, 2235, 2236, 2463, 2464, 2465, 2466, 2467, 2468, 7270, 7271, 8195,
                2075, 2076, 2077, 2078, 2079, 2080, 2081, 2082, 2083, 2084, 7251, 7252, 2098, 2099, 2100, 2101, 2102, 2103,
                7261, 2085, 2086, 2087, 2088, 2089, 7878, 7879, 7880, 891, 2090, 2091, 2092, 2093, 3851, 3852, 6386, 7262,
                7416
        };
    }
}
