package com.zenyte.game.content.ogrechest;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;


@Getter
public enum OgreLoot {

    COINS(90, new Item(995, 1675)),
    BRONZE_AXE(54.56, new Item(1351, 1)),
    IRON_AXE(54.56, new Item(1349, 1)),
    STEEL_AXE(54.56, new Item(1353, 1)),
    BRONZE_PICKAXE(54.95, new Item(1265, 1)),
    IRON_PICKAXE(54.95, new Item(1267, 1)),
    STEEL_PICKAXE(54.95, new Item(1269, 1)),
    BRONZE_DAGGER(53.26, new Item(1205, 1)),
    IRON_DAGGER(53.26, new Item(1203, 1)),
    STEEL_DAGGER(53.26, new Item(1207, 1)),
    BRONZE_NAILS(52.61, new Item(4819, 21)),
    IRON_NAILS(52.61, new Item(4820, 18)),
    STEEL_NAILS(52.61, new Item(1539, 13)),
    BLACK_NAILS(52.61, new Item(4821, 13)),
    KNIFE(41.65, new Item(946, 1)),
    RUSTY_SWORD(43.52, new Item(686, 1)),
    DAMAGED_ARMOUR(43.62, new Item(697, 1)),
    LEATHER_BODY(43.21, new Item(1129, 1)),
    TINDERBOX(42.62, new Item(590, 1)),
    BUTTONS(22.50, new Item(688, 1)),
    GEMS(16.33, new Item(1625, 2), new Item(1627, 2), new Item( 1617, 2)),//opal, jade, diamond
    GRIMY_LANTADYME(16.89, new Item(2485, 1)),
    EASY_CLUE_SCROLL(2.10, new Item(2677, 1)),
    ZOGRE_BONES(52.16, new Item(4813, 1)),
    FAYRG_BONES(12.21, new Item(4831, 1)),
    RAURG_BONES(9.66, new Item(4833, 1)),
    OURG_BONES(5.88, new Item(4835, 1));

    private final int weight;
    private final Item[] loot;
    private final BiFunction<Player, Item, Item> lootFunction;

    private static final OgreLoot[] values = values();

    private static final int TOTAL_WEIGHT;

    static {
        int weight = 0;
        for (val value : values) {
            weight += value.weight;
        }
        TOTAL_WEIGHT = weight;
    }

    OgreLoot(final double percentage, final Item... loot) {
        this(percentage, null, loot);
    }

    OgreLoot(final double percentage, final BiFunction<Player, Item, Item> lootFunction, final Item... loot) {
        this.weight = (int) Math.floor(32767F * percentage / 100F);
        this.lootFunction = lootFunction;
        this.loot = loot;
    }

    public static final List<Item> get(@NotNull final Player player) {
        val list = new ObjectArrayList<Item>();
        list.add(new Item(4813));
        val roll = Utils.random(TOTAL_WEIGHT);
        int current = 0;
        for (val loot : values) {
            if ((current += loot.weight) >= roll) {
                for (val item : loot.loot) {
                    list.add(loot.lootFunction == null ? item : loot.lootFunction.apply(player, item));
     }
                break;
            }
        }
        return list;
    }


}
