package com.zenyte.game.world.entity.player;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.degradableitems.DegradableItem;
import com.zenyte.plugins.item.TomeOfFire;
import lombok.val;

public class BankPreset {

    private static final Item[][] ITEMS = new Item[][]{
            new Item[]{new Item(995, 10000000)}, new Item[]{
            new Item(2619, 1), new Item(2627, 1), new Item(2657, 1), new Item(2665, 1), new Item(2673, 1),
            new Item(12476, 1), new Item(12486, 1), new Item(12466, 1), new Item(2615, 1), new Item(2623, 1),
            new Item(2653, 1), new Item(2661, 1), new Item(2669, 1), new Item(12470, 1), new Item(12480, 1),
            new Item(12460, 1), new Item(2617, 1), new Item(2625, 1), new Item(2655, 1), new Item(2663, 1),
            new Item(2671, 1), new Item(12472, 1), new Item(12482, 1), new Item(12462, 1), new Item(3476, 1),
            new Item(3477, 1), new Item(3478, 1), new Item(3479, 1), new Item(3480, 1), new Item(12474, 1),
            new Item(12484, 1), new Item(12464, 1), new Item(2621, 1), new Item(2629, 1), new Item(2659, 1),
            new Item(2667, 1), new Item(2675, 1), new Item(12478, 1), new Item(12488, 1), new Item(12468, 1),
            new Item(21298, 1), new Item(21301, 1), new Item(21304, 1), new Item(20050, 1), new Item(11335, 1),
            new Item(3140, 1), new Item(4087, 1), new Item(4585, 1), new Item(1187, 1), new Item(4587, 1),
            new Item(20000, 1), new Item(1434, 1), new Item(3204, 1), new Item(1249, 1), new Item(1215, 1),
            new Item(1377, 1), new Item(10350, 1), new Item(10348, 1), new Item(10346, 1), new Item(10352, 1),
            new Item(12426, 1), new Item(11832, 1), new Item(11834, 1), new Item(11836, 1), new Item(11802, 1),
            new Item(11804, 1), new Item(11806, 1), new Item(11808, 1), new Item(4753, 1), new Item(4755, 1),
            new Item(4757, 1), new Item(4759, 1), new Item(4745, 1), new Item(4747, 1), new Item(4749, 1),
            new Item(4751, 1), new Item(4724, 1), new Item(4726, 1), new Item(4728, 1), new Item(4730, 1),
            new Item(4716, 1), new Item(4718, 1), new Item(4720, 1), new Item(4722, 1), new Item(6735, 1),
            new Item(11772, 1), new Item(6737, 1), new Item(11773, 1), new Item(7462, 1), new Item(6570, 1),
            new Item(21295, 1), new Item(10551, 1), new Item(3753, 1), new Item(3751, 1), new Item(10828, 1),
            new Item(6524, 1), new Item(12931, 1), new Item(11283, 1), new Item(13239, 1), new Item(19553, 1),
            new Item(13576, 1), new Item(22981, 1),
    }, new Item[]{
            new Item(6918, 1), new Item(6916, 1), new Item(6924, 1), new Item(6922, 1), new Item(6920, 1),
            new Item(12419, 1), new Item(12420, 1), new Item(12421, 1), new Item(12457, 1), new Item(12458, 1),
            new Item(12459, 1), new Item(12422, 1), new Item(10342, 1), new Item(10338, 1), new Item(10340, 1),
            new Item(10344, 1), new Item(4708, 1), new Item(4712, 1), new Item(4714, 1), new Item(4710, 1),
            new Item(21018, 1), new Item(21021, 1), new Item(21024, 1), new Item(19544, 1), new Item(11905, 1),
            new Item(12899, 1), new Item(11791, 1), new Item(12904, 1), new Item(TomeOfFire.TOME_OF_FIRE, 1), new Item(6731, 1),
            new Item(11770, 1), new Item(3755, 1), new Item(2412, 1), new Item(2413, 1), new Item(2414, 1),
            new Item(6889, 1), new Item(11924, 1), new Item(12002, 1), new Item(13235, 1)
    }, new Item[]{
            new Item(10334, 1), new Item(1065, 1), new Item(1099, 1), new Item(1135, 1), new Item(7370, 1),
            new Item(7378, 1), new Item(7372, 1), new Item(7380, 1), new Item(10330, 1), new Item(2487, 1),
            new Item(2493, 1), new Item(2499, 1), new Item(7374, 1), new Item(7382, 1), new Item(7376, 1),
            new Item(7384, 1), new Item(10332, 1), new Item(2489, 1), new Item(2495, 1), new Item(2501, 1),
            new Item(12327, 1), new Item(12329, 1), new Item(12331, 1), new Item(12333, 1), new Item(10336, 1),
            new Item(2491, 1), new Item(2497, 1), new Item(2503, 1), new Item(12381, 1), new Item(12383, 1),
            new Item(12385, 1), new Item(12387, 1), new Item(4732, 1), new Item(4734, 1), new Item(4736, 1),
            new Item(4738, 1), new Item(11785, 1), new Item(11826, 1), new Item(11828, 1), new Item(11830, 1),
            new Item(6733, 1), new Item(11771, 1), new Item(10498, 1), new Item(10499, 1), new Item(22109, 1),
            new Item(2581, 1), new Item(12596, 1), new Item(2577, 1), new Item(19994, 1), new Item(3749, 1),
            new Item(20997, 1), new Item(21000, 1), new Item(13237, 1), new Item(19547, 1), new Item(12924, 1),
            new Item(811, 10000), new Item(11230, 10000), new Item(12934, 10000), new Item(11212, 10000)
    }, new Item[]{
            new Item(12817, 1), new Item(12821, 1), new Item(12825, 1), new Item(12829, 1), new Item(12831, 1),
            new Item(3840, 1), new Item(3842, 1), new Item(3844, 1), new Item(11663, 1), new Item(11665, 1),
            new Item(11664, 1), new Item(8839, 1), new Item(8840, 1), new Item(13072, 1), new Item(13073, 1),
            new Item(8842, 1), new Item(8841, 1),
    }, new Item[]{
            new Item(2428, 10000), new Item(121, 10000), new Item(123, 10000), new Item(125, 10000),
            new Item(113, 10000), new Item(115, 10000), new Item(117, 10000), new Item(119, 10000),
            new Item(2432, 10000), new Item(133, 10000), new Item(135, 10000), new Item(137, 10000),
            new Item(3040, 10000), new Item(3042, 10000), new Item(3044, 10000), new Item(3046, 10000),
            new Item(2444, 10000), new Item(169, 10000), new Item(171, 10000), new Item(173, 10000),
            new Item(9739, 10000), new Item(9741, 10000), new Item(9743, 10000), new Item(9745, 10000),
            new Item(2436, 10000), new Item(145, 10000), new Item(147, 10000), new Item(149, 10000),
            new Item(2440, 10000), new Item(157, 10000), new Item(159, 10000), new Item(161, 10000),
            new Item(2442, 10000), new Item(163, 10000), new Item(165, 10000), new Item(167, 10000),
            new Item(9021, 10000), new Item(9022, 10000), new Item(9023, 10000), new Item(9024, 10000),
            new Item(12695, 10000), new Item(12697, 10000), new Item(12699, 10000), new Item(12701, 10000),
            new Item(2452, 10000), new Item(2454, 10000), new Item(2456, 10000), new Item(2458, 10000),
            new Item(11951, 10000), new Item(11953, 10000), new Item(11955, 10000), new Item(11957, 10000),
            new Item(2450, 10000), new Item(189, 10000), new Item(191, 10000), new Item(193, 10000),
            new Item(6685, 10000), new Item(6687, 10000), new Item(6689, 10000), new Item(6691, 10000),
            new Item(11722, 10000), new Item(11723, 10000), new Item(11724, 10000), new Item(11725, 10000),
            new Item(11726, 10000), new Item(11727, 10000), new Item(11728, 10000), new Item(11729, 10000),
            new Item(11730, 10000), new Item(11731, 10000), new Item(11732, 10000), new Item(11733, 10000),
            new Item(11734, 10000), new Item(11735, 10000), new Item(11736, 10000), new Item(11737, 10000),
            new Item(2430, 10000), new Item(127, 10000), new Item(129, 10000), new Item(131, 10000),
            new Item(3008, 10000), new Item(3010, 10000), new Item(3012, 10000), new Item(3014, 10000),
            new Item(2434, 10000), new Item(139, 10000), new Item(141, 10000), new Item(143, 10000),
            new Item(3016, 10000), new Item(3018, 10000), new Item(3020, 10000), new Item(3022, 10000),
            new Item(3024, 10000), new Item(3026, 10000), new Item(3028, 10000), new Item(3030, 10000),
            new Item(10925, 10000), new Item(10927, 10000), new Item(10929, 10000), new Item(10931, 10000),
            new Item(12625, 10000), new Item(12627, 10000), new Item(12629, 10000), new Item(12631, 10000),
            new Item(4842, 10000), new Item(4844, 10000), new Item(4846, 10000), new Item(4848, 10000),
            new Item(2446, 10000), new Item(175, 10000), new Item(177, 10000), new Item(179, 10000),
            new Item(2448, 10000), new Item(181, 10000), new Item(183, 10000), new Item(185, 10000),
            new Item(5943, 10000), new Item(5945, 10000), new Item(5947, 10000), new Item(5949, 10000),
            new Item(5952, 10000), new Item(5954, 10000), new Item(5956, 10000), new Item(5958, 10000),
            new Item(12905, 10000), new Item(12907, 10000), new Item(12909, 10000), new Item(12911, 10000),
            new Item(12913, 10000), new Item(12915, 10000), new Item(12917, 10000), new Item(12919, 10000),
            new Item(11936, 10000), new Item(13441, 10000), new Item(397, 10000), new Item(391, 10000),
            new Item(3144, 10000), new Item(385, 10000)
    }
    };

    public static final void setBank(final Player player) {
        int index = 9;
        val bank = player.getBank();
        for (final Item[] it : ITEMS) {
            for (final Item i : it) {
                val item = new Item(i);
                final int charges = DegradableItem.getDefaultCharges(item.getId(), 0);
                if (charges > 0) {
                    item.setCharges(charges);
                    item.setAmount(1);
                }
                bank.add(item, index);
            }
            index++;
            if (index > 9) {
                index = 0;
            }
        }
        player.getBank().refreshContainer();
        player.sendMessage("Bank tabs from 1 to " + ITEMS.length + " preset to default bank.");
    }

}