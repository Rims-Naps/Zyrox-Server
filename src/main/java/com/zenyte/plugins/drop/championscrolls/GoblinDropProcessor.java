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

public class GoblinDropProcessor extends DropProcessor {
    @Override
    public void attach() {
        appendDrop(new DisplayedDrop(6801, 1, 1, 2500));
    }

    public void onDeath(final NPC npc, final Player killer) {
        if (Utils.random(2499) == 0) {
            npc.dropItem(killer, new Item(6801));
            killer.sendMessage(Colour.BRICK.wrap("A Champion's scroll falls to the ground as you slay your opponent."));
        }
    }

    @Override
    public int[] ids() {
        return new int[] {
                655, 656, 657, 658, 659, 660, 661, 662, 663, 664, 665, 666, 667, 668, 674, 677, 678, 2245, 2246, 2247, 2248,
                2249, 2484, 2485, 2486, 2487, 2488, 2489, 3028, 3029, 3030, 3031, 3032, 3033, 3034, 3035, 3036, 3037, 3038,
                3039, 3040, 3041, 3042, 3043, 3044, 3045, 3046, 3047, 3048, 3051, 3052, 3053, 3054, 3073, 3074, 3075, 3076,
                5192, 5193, 5195, 5196, 5197, 5198, 5199, 5200, 5201, 5202, 5203, 5204, 5205, 5206, 5207, 5208, 5369, 6434,
                6435, 6436, 6437, 2216, 2217, 2218
        };
    }
}