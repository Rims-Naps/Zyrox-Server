package com.zenyte.game.content.skills.farming;

import com.zenyte.game.content.skills.mining.MiningDefinitions;
import com.zenyte.game.item.Item;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public enum BasketData {

    APPLE(new Item(1955), new Item(5378), new Item(5386)),
    BANANA(new Item(1963), new Item(5408), new Item(5416)),
    ORANGE(new Item(2108), new Item(5388), new Item(5396)),
    STRAWBERRY(new Item(5504), new Item(5398), new Item(5406)),
    TOMATO(new Item(1982), new Item(5960), new Item(5968)),
    ;

    @Getter private final Item produce;
    @Getter private final Item basket;
    @Getter private final Item full;

    public static final Int2ObjectOpenHashMap<BasketData> baskets = new Int2ObjectOpenHashMap<>();
    public static final BasketData[] VALUES = values();

    BasketData(final Item produce, final Item basket, final Item full) {
        this.produce = produce;
        this.basket = basket;
        this.full = full;
    }

    static {
        for(final BasketData basket : VALUES) {
            baskets.put(basket.getProduce().getId(), basket);
            baskets.put(basket.getBasket().getId(), basket);
            baskets.put(basket.getBasket().getId()+2, basket);
            baskets.put(basket.getBasket().getId()+4, basket);
            baskets.put(basket.getBasket().getId()+6, basket);
            baskets.put(basket.getFull().getId(), basket);
        }
    }

    public static BasketData getBasket(final int id) {
        return baskets.get(id);
    }

}
