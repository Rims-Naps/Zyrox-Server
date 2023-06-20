package com.zenyte.game.content.sandstorm;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.IntListUtils;
import it.unimi.dsi.fastutil.ints.IntLists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;

/**
 * @author Chris
 * @since August 20 2020
 */
@RequiredArgsConstructor
@Getter
public enum Sandstone {
    ONE_KG(ItemId.SANDSTONE_1KG, 2),
    TWO_KG(ItemId.SANDSTONE_2KG, 4),
    FIVE_KG(ItemId.SANDSTONE_5KG, 8),
    TEN_KG(ItemId.SANDSTONE_10KG, 16);

    public static final ImmutableSet<Sandstone> SANDSTONES = Sets.immutableEnumSet(EnumSet.allOf(Sandstone.class));
    public static final IntLists.UnmodifiableList SANDSTONE_IDS = IntListUtils.unmodifiable(SANDSTONES.stream().mapToInt(Sandstone::getItemId).toArray());
    private final int itemId;
    private final int bucketsOfSand;
}
