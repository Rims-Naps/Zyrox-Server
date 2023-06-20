package com.zenyte.game.content.minigame.tithefarm;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import lombok.Getter;

import java.util.HashMap;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public enum TithePlantType {

    GOLOVANOVA(ItemId.GOLOVANOVA_SEED, new Item(ItemId.GOLOVANOVA_FRUIT), 27384, 34, 6),
    BOLOGANO(ItemId.BOLOGANO_SEED, new Item(ItemId.BOLOGANO_FRUIT), 27395, 54, 14),
    LOGAVANO(ItemId.LOGAVANO_SEED, new Item(ItemId.LOGAVANO_FRUIT), 27406, 74, 23),
    ;

    @Getter private final int seed;
    @Getter private final Item fruit;
    @Getter private final int seedling;
    @Getter private final int farmingLevel;
    @Getter private final int baseXp;

    public static final TithePlantType[] VALUES = values();
    public static final HashMap<Integer, TithePlantType> OBJECT_MAP = new HashMap<>();
    public static final HashMap<Integer, TithePlantType> SEEDS_MAP = new HashMap<>();

    TithePlantType(final int seed, final Item fruit, final int baseObject, final int farmingLevel, final int baseXp) {
        this.seed = seed;
        this.fruit = fruit;
        this.seedling = baseObject;
        this.farmingLevel = farmingLevel;
        this.baseXp = baseXp;
    }

    static {
        for(final TithePlantType tithe : VALUES) {
            // for each type of plant, add that seedling id + 29 ids to the list, with that object
            // so that we can easily find the object no matter what transformation it's taken
            for(int i=0; i < 21; i++) {
                OBJECT_MAP.put(tithe.getSeedling() + i, tithe);
            }

            // also map the seed ids to these
            SEEDS_MAP.put(tithe.getSeed(), tithe);
        }
    }


}
