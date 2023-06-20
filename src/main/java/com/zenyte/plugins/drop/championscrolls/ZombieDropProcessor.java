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

public class ZombieDropProcessor extends DropProcessor {
    @Override
    public void attach() {
        appendDrop(new DisplayedDrop(6807, 1, 1, 2500));
    }

    public void onDeath(final NPC npc, final Player killer) {
        if (Utils.random(2499) == 0) {
            npc.dropItem(killer, new Item(6807));
            killer.sendMessage(Colour.BRICK.wrap("A Champion's scroll falls to the ground as you slay your opponent."));
        }
    }

    @Override
    public int[] ids() {
        return new int[]{
              561, 562, 69, 4421, 5342, 5343, 5344, 5345, 5346, 5347, 5348, 5349, 5350, 5351, 5648, 5649, 5650, 5651, 5652,
                5653, 5654, 5655, 5656, 5657, 5658, 5659, 5660, 5661, 5662, 5663, 5665, 5666, 5667, 5668, 5669, 5670, 5671,
                5672, 5673, 5674, 5675, 5676, 5677, 5678, 5679, 5680, 5681, 5682, 5683, 5684, 5685, 5686, 5687, 5688, 5689,
                5690, 5691, 5692, 5693, 5694, 5695, 5696, 5697, 5698, 5699, 5700, 5701, 5702, 5703, 5704, 5705, 5706, 5707,
                5708, 5709, 5710, 5711, 5712, 5713, 5714, 5715, 5716, 5717, 5718, 5719, 5720, 866, 867, 868, 869, 870, 871,
                873, 874, 875, 876, 877, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46,
                47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 563, 564, 565, 566,
                567, 568, 569, 570, 571, 572, 573, 574, 575, 576, 577, 578, 579, 580, 581, 582, 583, 584, 585, 586, 587, 588,
                589, 590, 591, 592, 593, 594, 595, 596, 597, 598, 599, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622, 623,
                624, 880, 2501, 2502, 2503, 2504, 2505, 2506, 2507, 2508, 2509, 3980, 3981, 5647, 6449, 6450, 6451, 6452,
                6453, 6454, 6455, 6456, 6457, 5458, 6459, 6460, 6461, 6462, 6463, 6464, 6465, 6466, 6596, 6597,6598, 6741,
                7485, 7486, 7487, 7488, 8067, 8068, 8069
        };
    }
}
