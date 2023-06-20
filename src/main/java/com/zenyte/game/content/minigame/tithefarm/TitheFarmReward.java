package com.zenyte.game.content.minigame.tithefarm;

import com.zenyte.game.item.ItemId;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public enum TitheFarmReward {

    COMPOST(ItemId.COMPOST, 1),
    SUPERCOMPOST(ItemId.SUPERCOMPOST, 5),
    GRAPE_SEED(ItemId.GRAPE_SEED, 2),
    BOLOGAS_BLESSING(ItemId.BOLOGAS_BLESSING, 1),
    ;

    @Getter private final int itemId;
    @Getter private final int cost;

    public static final Map<Integer, TitheFarmReward> REWARD_MAP = new HashMap<>();

    TitheFarmReward(final int itemId, final int cost) {
        this.itemId = itemId;
        this.cost = cost;
    }

    static {
        for(final TitheFarmReward reward : values()) {
            REWARD_MAP.put(reward.getItemId(), reward);
        }
    }

}
