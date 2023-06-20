package com.zenyte.game.content.minigame.duelarena;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import java.util.function.Function;

import static com.zenyte.game.util.Currency.MILLION;

/**
 * @author Tommeh | 30-11-2018 | 15:31
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@AllArgsConstructor
public enum Tax {
    SMALL_TAX(pot -> pot < MILLION.get(10), 0.25F),
    MEDIUM_TAX(pot -> pot >= MILLION.get(10) && pot < MILLION.get(100), 0.50F),
    LARGE_TAX(pot -> pot >= MILLION.get(100), 1F);
    private static final Tax[] VALUES = values();
    private final Function<Long, Boolean> function;
    private final float rate;

    public static Tax getTax(final int amount) {
        for (val tax : VALUES) {
            if (tax.getFunction().apply((long) amount)) {
                return tax;
            }
        }
        throw new RuntimeException("Unable to find tax rate for " + amount + " amount.");
    }

    @Override
    public String toString() {
        return String.format("%.2f", rate) + "%";
    }
}
