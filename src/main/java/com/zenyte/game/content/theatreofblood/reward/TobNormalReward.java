package com.zenyte.game.content.theatreofblood.reward;

import com.zenyte.game.content.chambersofxeric.rewards.RaidReward;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Cresinkel
 */

@Getter
@AllArgsConstructor
public enum TobNormalReward implements RaidReward {

    CRYSTAL_SHARDS(ItemId.CRYSTAL_SHARD,300,500),
    VIAL_OF_BLOOD(22447,100,120),
    DEATH_RUNE(ItemId.DEATH_RUNE,500,600),
    BLOOD_RUNE(ItemId.BLOOD_RUNE,500,600),
    GOLD_ORE(445,300,360),
    ADAMANTITE_BAR(2362,130,150),
    RUNITE_BAR(2364,60,72),
    BLACK_DHIDE(1748,60,90),
    WINE_OF_ZAMORAK(246,50,60),
    GRIMY_SNAKE_WEED(2679,50,60),
    POTATO_CACTUS(3139,50,60),
    RED_SPIDERS_EGGS(224,50,60),
    SNAPE_GRASS_SEEDS(22879,3,5),
    MORT_MYRE_FUNGUS(2971,50,60),
    BLUE_DRAGON_SCALE(244,50,60),
    BIRD_NEST(20783,50,60),
    LAVA_SCALES(11993,50,60),
    BATTLE_STAFF(1392,20,30),
    FIRE_ORB(570,20,30),
    RUNE_BATTLEAXE(1374,5,8),
    RUNE_PLATEBODY(1128,5,8),
    RUNE_CHAINBODY(1374,5,8),
    ONYX_BOLTS(9342,25,50),
    DRAGON_BOLTS(21930,200,300),
    PALM_TREE_SEED(5289,3,3),
    YEW_SEED(5315,3,3),
    MAGIC_SEED(5316,3,3),
    MAHOGANY_SEED(21488,10,12);

    private static final TobNormalReward[] values = values();
    private final int id;
    private final int minimumAmount;
    private final int maximumAmount;

    /**
     * Selects a random reward out of the values of this enum.
     * @return a random raid reward out of the lot.
     */
    public static TobNormalReward random() {
        return values[Utils.random(values.length - 1)];
    }

}
