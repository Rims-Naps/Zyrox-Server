package com.zenyte.game.content.supplycaches;

import com.zenyte.game.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author Kris | 03/05/2019 16:08
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
@Getter
public enum SupplyCache {

    //Potions
    SUPER_COMBAT_POTION(12696, 10, 30),
    PRAYER_POTION(2435, 15, 35),
    SUPER_RESTORE(3025, 15, 35),
    SARADOMIN_BREW(6686, 15, 35),
    SUPER_ANTIFIRE(21978, 10, 20),
    STAMINA(12626, 10, 30),
    ANTI_VENOM_PLUS(12914, 10, 15),
    SANFEW_SERUM(10926, 10, 30),
    //Ranging weapons
    GREY_CHINCHOMPA(10033, 100, 500),
    RED_CHINCHOMPA(10034, 100, 500),
    BLACK_CHINCHOMPA(11959, 100, 500),
    //Ammunition
    RUBY_BOLTS(9242, 100, 400),
    DIAMOND_BOLTS(9243, 100, 400),
    DRAGONSTONE_BOLTS(9244, 100, 400),
    //Herblore secondaries
    EYE_OF_NEWT(222, 100, 250),
    LIMPWURT_ROOT(226, 100, 250),
    SWAMP_TAR(1939, 100, 250),
    SNAPE_GRASS(232, 100, 250),
    CHOCOLATE_DUST(1976, 100, 250),
    JANGERBERRIES(248, 100, 250),
    MORT_MYRE_FUNGUS(2971, 100, 250),
    POTATO_CACTUS(3139, 100, 250),
    RED_SPIDERS_EGGS(224, 100, 250),
    WHITE_BERRIES(240, 100, 250),
    DRAGON_SCALE_DUST(242, 100, 250),
    GOAT_HORN_DUST(9737, 100, 250),
    UNICORN_HORN_DUST(236, 100, 250),
    //Miscellaneous
    CRYSTAL_KEYS(990, 5, 15),
    //Food
    SHARK(386, 100, 300),
    SEA_TURTLE(398, 100, 300),
    ANGLERFISH(13442, 100, 300),
    DARK_CRAB(11937, 100, 300),
    MANTA_RAY(392, 100, 300),
    TUNA_POTATO(7061, 100, 300),
    //Uncut gems
    OPAL(1626, 10, 75),
    JADE(1628, 10, 70),
    RED_TOPAZ(1630, 10, 50),
    SAPPHIRE(1624, 10, 40),
    EMERALD(1622, 10, 30),
    RUBY(1620, 10, 20),
    DIAMOND(1618, 5, 10),
    DRAGONSTONE(1632, 1, 3)

    ;

    private final int id;
    private final int min, max;

    private static final SupplyCache[] values = values();

    /**
     * Picks a random element from the supply caches list.
     * @return the random element from the supply caches list.
     */
    public static final Optional<SupplyCache> random() {
        return Optional.ofNullable(Utils.getRandomElement(values));
    }

}
