package com.zenyte.game.content.skills.thieving;

import com.zenyte.game.item.ImmutableItem;
import com.zenyte.game.item.ItemId;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Corey
 * @since 01:02 - 16/08/2019
 */
@AllArgsConstructor
@Getter
public enum CoinPouch {
    MAN(ItemId.COIN_POUCH, new ImmutableItem(995, 3)),
    WOMAN(ItemId.COIN_POUCH, new ImmutableItem(995, 3)),
    FARMER(ItemId.COIN_POUCH_22522, new ImmutableItem(995, 9)),
    WARRIOR(ItemId.COIN_POUCH_22524, new ImmutableItem(995, 18)),
    WARRIOR_WOMAN(ItemId.COIN_POUCH_22524, new ImmutableItem(995, 18)),
    GUARD(ItemId.COIN_POUCH_22527, new ImmutableItem(995, 30)),
    FREMENNIK(ItemId.COIN_POUCH_22528, new ImmutableItem(995, 30)),
    KNIGHT_OF_ARDOUGNE(ItemId.COIN_POUCH_22531, new ImmutableItem(995, 50)),
    PALADIN(ItemId.COIN_POUCH_22535, new ImmutableItem(995, 80)),
    HAM_MEMBER(ItemId.COIN_POUCH_22523, new ImmutableItem(995, 1, 21)),
    ROGUE(ItemId.COIN_POUCH_22525, new ImmutableItem(995, 25, 40)),
    CAVE_GOBLIN(ItemId.COIN_POUCH_22526, new ImmutableItem(995, 11, 48)),
    KHARIDIAN_BANDIT(ItemId.COIN_POUCH_22532, new ImmutableItem(995, 40)),
    WATCHMAN(ItemId.COIN_POUCH_22533, new ImmutableItem(995, 60)),
    GNOME(ItemId.COIN_POUCH_22536, new ImmutableItem(995, 300)),
    HERO(ItemId.COIN_POUCH_22537, new ImmutableItem(995, 200, 300)),
    ELF(ItemId.COIN_POUCH_22538, new ImmutableItem(995, 280, 350));

    public static final CoinPouch[] VALUES = values();
    public static final Int2ObjectOpenHashMap<CoinPouch> ITEMS = new Int2ObjectOpenHashMap<>(VALUES.length);
    public static final int MAX_POUCHES_PER_STACK = 28;

    static {
        for (CoinPouch pouch : VALUES) {
            ITEMS.put(pouch.itemId, pouch);
        }
    }

    private final int itemId;
    private final ImmutableItem reward;

}
