package com.zenyte.game.content.pyramidplunder;

import lombok.Data;

/**
 * @author Christopher
 * @since 4/4/2020
 */
@Data
public class WeightedPlunderRewardTier {
    private final PlunderRewardTier tier;
    private final int weight;
}
