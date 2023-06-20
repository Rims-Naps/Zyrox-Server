package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;

/**
 * @author Tommeh | 07/06/2019 | 17:18
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class CustomItemSet extends ItemPlugin {

    @Override
    public void handle() {
        bind("Open", (player, item, slotId) -> {
            val set = ItemSet.get(item.getId());
            if (set == null) {
                return;
            }
            if (!player.getInventory().checkSpace(set.getItems().length)) {
                return;
            }
            player.getInventory().deleteItem(item);
            for (val id : set.getItems()) {
                player.getInventory().addItem(id, 1);
            }
        });
    }

    @Override
    public int[] getItems() {
        return ItemSet.SETS.keySet().toIntArray();
    }

    @Getter
    public enum ItemSet {

        INFINITY(2724, 6918, 6916, 6924, 6922, 6920),
        VOID_KNIGHT(2726, 8839, 8840, 8842, 11663, 11664, 11665),
        ELITE_VOID_KNIGHT(2728, 13072, 13073, 8842, 11663, 11664, 11665),
        THIRD_AGE_RANGE(2730, 10334, 10330, 10332, 10336),
        THIRD_AGE_MELEE(2732, 10350, 10348, 10346, 10352),
        THIRD_AGE_MAGE(2734, 10342, 10338, 10340),
        THIRD_AGE_DRUIDIC(2736, 23336, 23339, 23342, 23345),
        CORRUPTED(2738, 20838, 20840, 20842, 20846),
        RANGER(2740, 2581, 12596, 23249, 2577),
        SANTA(2742, 12887, 12888, 12889, 12890, 12891),
        BUNNY(2744, 13663, 13664, 13182, 13665);

        private final int id;
        private final int[] items;

        public static final ItemSet[] all = values();
        public static final Int2ObjectOpenHashMap<ItemSet> SETS = new Int2ObjectOpenHashMap(all.length);

        static {
            for (val set : all) {
                SETS.put(set.id, set);
            }
        }

        ItemSet(final int id, final int... items) {
            this.id = id;
            this.items = items;
        }

        public static ItemSet get(final int id) {
            return SETS.get(id);
        }
    }
}
