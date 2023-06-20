package com.zenyte.game.shop;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Kris | 26. sept 2018 : 02:32:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
@AllArgsConstructor
@Data
class JsonShop {

	private final String shopName;
	private final ShopCurrency currency;
	private final ShopPolicy sellPolicy;
	private final float sellMultiplier;
	private final List<Item> items;

    static class Item {
        int id, amount, sellPrice, buyPrice, restockTimer;
        boolean ironmanRestricted;

    }
	
}
