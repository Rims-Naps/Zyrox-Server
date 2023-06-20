package com.zenyte.game.shop;

import com.zenyte.game.world.entity.player.Player;

/**
 * @author Kris | 24/11/2018 14:10
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
interface CurrencyPalette {

    int getAmount(final Player player);

    boolean isStackable();

    boolean isPhysical();

    int id();

    default int getMaximumAmount() {
        return Integer.MAX_VALUE;
    }

    void remove(final Player player, final int amount);

    void add(final Player player, final int amount);

}
