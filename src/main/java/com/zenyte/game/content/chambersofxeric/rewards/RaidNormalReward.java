package com.zenyte.game.content.chambersofxeric.rewards;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 12/09/2019 19:47
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@AllArgsConstructor
public enum RaidNormalReward implements RaidReward {

    CRYSTAL_SHARDS(ItemId.CRYSTAL_SHARD, 500),
    DEATH_RUNE(560, 3100),
    BLOOD_RUNE(565, 4095),
    SOUL_RUNE(566, 6554),
    RUNE_ARROW(892, 9437),
    DRAGON_ARROW(11212, 926),
    GRIMY_TOADFLAX(3049, 354),
    GRIMY_RANARR_WEED(207, 164),
    GRIMY_IRIT_LEAF(209, 668),
    GRIMY_AVANTOE(211, 354),
    GRIMY_KWUARM(213, 323),
    GRIMY_SNAPDRAGON(3051, 323),
    GRIMY_CADANTINE(215, 319),
    GRIMY_LANTADYME(2485, 446),
    GRIMY_DWARF_WEED(217, 616),
    GRIMY_TORSTOL(219, 153),
    SILVER_ORE(442, 6553),
    COAL(453, 6553),
    GOLD_ORE(444, 2892),
    MITHRIL_ORE(447, 2892),
    ADAMANTITE_ORE(449, 729),
    RUNITE_ORE(451, 87),
    UNCUT_SAPPHIRE(1623, 642),
    UNCUT_EMERALD(1621, 923),
    UNCUT_RUBY(1619, 524),
    UNCUT_DIAMOND(1617, 253),
    LIZARDMAN_FANG(13391, 4898),
    PURE_ESSENCE(7936, 65535),
    SALTPETRE(13421, 5461),
    TEAK_PLANK(8780, 1310),
    MAHOGANY_PLANK(8782, 550),
    DYNAMITE(13573, 2390),
    TORN_PRAYER_SCROLL(21047, 1),
    DARK_RELIC(21027, 1);

    private static final RaidNormalReward[] values = values();
    private final int id;
    private final int maximumAmount;

    /**
     * Selects a random reward out of the values of this enum.
     *
     * @return a random raid reward out of the lot.
     */
    static RaidNormalReward random() {
        return values[Utils.random(values.length - 1)];
    }

}
