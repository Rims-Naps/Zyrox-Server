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

public class SkeletonDropProcessor extends DropProcessor {
    @Override
    public void attach() {
        appendDrop(new DisplayedDrop(6806, 1, 1, 2500));
    }

    public void onDeath(final NPC npc, final Player killer) {
        if (Utils.random(2499) == 0) {
            npc.dropItem(killer, new Item(6806));
            killer.sendMessage(Colour.BRICK.wrap("A Champion's scroll falls to the ground as you slay your opponent."));
        }
    }

    @Override
    public int[] ids() {
        return new int[]{
                680, 681, 6440, 6614, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 130, 924, 1537, 1538, 1539,
                1540, 1541, 1685, 1686, 1687, 1688, 2520, 2521, 2522, 2523, 2524, 2525, 2526, 3565, 3584, 3972, 3973, 3974,
                4312, 4319, 5054, 5237, 6387, 6441, 6442, 6443, 6444, 6445, 6446, 6447, 6448, 6467, 6468, 6613, 6614, 7265,
                8070, 8071, 8072, 8139, 8140
        };
    }
}
