package com.zenyte.game.content.crystalchest;

import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.utils.StaticInitializer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Kris | 04/04/2019 12:53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
public enum CrystalLoot {

    SPINACH_ROLL(21.45, new Item(1969, 2), new Item(995, 4000)),
    EMPTY(16.77),
    SWORDFISH(5.91, new Item(995, 2000), new Item(371, 10)),
    RUNES(9.58, new Item(Magic.AIR_RUNE, 200), new Item(Magic.WATER_RUNE, 200), new Item(Magic.EARTH_RUNE, 200), new Item(Magic.FIRE_RUNE, 200),
            new Item(Magic.BODY_RUNE, 200), new Item(Magic.MIND_RUNE, 200), new Item(Magic.CHAOS_RUNE, 100), new Item(Magic.LAW_RUNE, 100),
            new Item(Magic.COSMIC_RUNE, 100), new Item(Magic.NATURE_RUNE, 100), new Item(Magic.DEATH_RUNE, 100)),
    COAL(7.84, new Item(454, 400)),
    GEMS(9.36, new Item(1603, 8), new Item(1601, 8)),
    TOOTH_HALF_OF_KEY(4.14, new Item(995, 1500), new Item(985, 2)),
    RUNITE_BARS(9.41, new Item(2363, 6)),
    LOOP_HALF_OF_KEY(4.00, new Item(995, 1500), new Item(987, 2)),
    IRON_ORE(7.92, new Item(441, 300)),
    ADAMANT_SQ_SHIELD(1.76, new Item(1183, 2)),
    PLATELEGS(0.86, (player, item) -> player.getAppearance().isMale() ? new Item(1079) : new Item(1093), (Item) null),

    INFINITY_GLOVES(0.1, new Item(6922)),
    INFINITY_HAT(0.1, new Item(6918)),
    INFINITY_TOP(0.1, new Item(6916)),
    INFINITY_BOTTOMS(0.1, new Item(6924)),
    INFINITY_BOOTS(0.1, new Item(6920)),
    BEGINNER_WAND(0.1, new Item(6908)),
    APPRENTICE_WAND(0.1, new Item(6910)),
    TEACHER_WAND(0.1, new Item(6912)),
    MASTER_WAND(0.1, new Item(6914)),
    MAGES_BOOK(0.1, new Item(6889));

    private final int weight;
    private final Item[] loot;
    private final BiFunction<Player, Item, Item> lootFunction;

    private static final CrystalLoot[] values = values();

    private static final int TOTAL_WEIGHT;

    static {
        int weight = 0;
        for (val value : values) {
            weight += value.weight;
        }
        TOTAL_WEIGHT = weight;
    }

    CrystalLoot(final double percentage, final Item... loot) {
        this(percentage, null, loot);
    }

    CrystalLoot(final double percentage, final BiFunction<Player, Item, Item> lootFunction, final Item... loot) {
        this.weight = (int) Math.floor(32767F * percentage / 100F);
        this.lootFunction = lootFunction;
        this.loot = loot;
    }

    public static final List<Item> get(@NotNull final Player player) {
        val list = new ObjectArrayList<Item>();
        list.add(new Item(1631, 2));
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
