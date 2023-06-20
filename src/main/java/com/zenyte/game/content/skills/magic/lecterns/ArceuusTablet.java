package com.zenyte.game.content.skills.magic.lecterns;

import com.zenyte.game.item.Item;
import lombok.Getter;

import static com.zenyte.game.content.skills.magic.spells.MagicSpell.*;
import static com.zenyte.game.content.skills.magic.lecterns.TabletCreation.DARK_ESSENCE_BLOCK;

/**
 * @author Kris | 03/09/2019 08:10
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
public enum ArceuusTablet implements LecternTablet {

    LUMBRIDGE_GRAVEYARD(6, 10F, new Item(19613), new Item(EARTH_RUNE, 2), new Item(LAW_RUNE, 1), new Item(DARK_ESSENCE_BLOCK)),
    DRAYNOR_MANOR(17, 16F, new Item(19615), new Item(WATER_RUNE, 1), new Item(EARTH_RUNE, 1), new Item(LAW_RUNE, 1), new Item(DARK_ESSENCE_BLOCK)),
    BATTLEFRONT(23, 19F, new Item(22949), new Item(EARTH_RUNE, 1), new Item(FIRE_RUNE, 1), new Item(LAW_RUNE, 1), new Item(DARK_ESSENCE_BLOCK)),
    MIND_ALTAR(28, 22F, new Item(19617), new Item(LAW_RUNE, 1), new Item(MIND_RUNE, 2), new Item(DARK_ESSENCE_BLOCK)),
    SALVE_GRAVEYARD(40, 30F, new Item(19619), new Item(LAW_RUNE, 1), new Item(SOUL_RUNE, 2), new Item(DARK_ESSENCE_BLOCK)),
    FENKENSTRAINS_CASTLE(48, 50F, new Item(19621), new Item(EARTH_RUNE, 1), new Item(LAW_RUNE, 1), new Item(SOUL_RUNE, 1), new Item(DARK_ESSENCE_BLOCK)),
    WEST_ARDOUGNE(61, 68F, new Item(19623), new Item(LAW_RUNE, 2), new Item(SOUL_RUNE, 2), new Item(DARK_ESSENCE_BLOCK)),
    HARMONY_ISLAND(65, 74F, new Item(19625), new Item(LAW_RUNE, 1), new Item(SOUL_RUNE, 1), new Item(NATURE_RUNE, 1), new Item(DARK_ESSENCE_BLOCK)),
    WILDERNESS_CEMETERY(71, 82F, new Item(19627), new Item(LAW_RUNE, 1), new Item(SOUL_RUNE, 1), new Item(BLOOD_RUNE, 1), new Item(DARK_ESSENCE_BLOCK)),
    BARROWS(83, 90F, new Item(19629), new Item(LAW_RUNE, 2), new Item(SOUL_RUNE, 2), new Item(BLOOD_RUNE, 1), new Item(DARK_ESSENCE_BLOCK)),
    APE_ATOLL(90, 100F, new Item(19631), new Item(LAW_RUNE, 2), new Item(SOUL_RUNE, 2), new Item(BLOOD_RUNE, 2), new Item(DARK_ESSENCE_BLOCK));
    private final int level;
    private final float experience;
    private final Item[] runes;
    private final Item tab;

    ArceuusTablet(final int level, final float experience, final Item tab, final Item... runes) {
        this.level = level;
        this.experience = experience;
        this.tab = tab;
        this.runes = runes;
    }
}
