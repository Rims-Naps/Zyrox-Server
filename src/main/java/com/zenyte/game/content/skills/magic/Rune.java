package com.zenyte.game.content.skills.magic;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import mgi.types.config.enums.EnumDefinitions;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Tommeh | 26 mrt. 2018 : 17:36:21
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Slf4j
public enum Rune {
    
    AIR(ItemId.AIR_RUNE),
    WATER(ItemId.WATER_RUNE),
    EARTH(ItemId.EARTH_RUNE),
    FIRE(ItemId.FIRE_RUNE),
    MIND(ItemId.MIND_RUNE),
    CHAOS(ItemId.CHAOS_RUNE),
    DEATH(ItemId.DEATH_RUNE),
    BLOOD(ItemId.BLOOD_RUNE),
    COSMIC(ItemId.COSMIC_RUNE),
    NATURE(ItemId.NATURE_RUNE),
    LAW(ItemId.LAW_RUNE),
    BODY(ItemId.BODY_RUNE),
    SOUL(ItemId.SOUL_RUNE),
    ASTRAL(ItemId.ASTRAL_RUNE),
    MIST(ItemId.MIST_RUNE),
    MUD(ItemId.MUD_RUNE),
    DUST(ItemId.DUST_RUNE),
    LAVA(ItemId.LAVA_RUNE),
    STEAM(ItemId.STEAM_RUNE),
    SMOKE(ItemId.SMOKE_RUNE),
    WRATH(ItemId.WRATH_RUNE);
    
    @Getter
    private final int id;
    
    public static final Rune[] values = values();
    
    Rune(final int id) {
        this.id = id;
    }
    
    static {
        val runeEnum = EnumDefinitions.getIntEnum(982).getValues();
        for (Rune rune : values) {
            if (!runeEnum.containsValue(rune.getId())) {
                log.error(Strings.EMPTY, new IllegalArgumentException("Defined Rune with id " + rune.getId() + " not found within rune cache enum."));
            }
        }
        if (values.length != runeEnum.size()) {
            log.error(Strings.EMPTY, new RuntimeException("Defined Rune enum size does not match rune cache enum size."));
        }
    }
    
    public static Rune getRune(final Item item) {
        for (Rune rune : values)
            if (item.getId() == rune.getId())
                return rune;
        return null;
    }
    
}
