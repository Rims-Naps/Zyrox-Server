package com.zenyte.game.world.entity.player;

import com.zenyte.game.item.Item;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 8-11-2018 | 19:55
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
public enum Lamp {

    EASY_DIARY_LAMP(30, new Item(13145), 2500),
    MEDIUM_DIARY_LAMP(40, new Item(13146), 7500),
    HARD_DIARY_LAMP(50, new Item(13147), 15000),
    ELITE_DIARY_LAMP(70, new Item(13148), 50000),
    EASY_CA_LAMP(20, new Item(32262), 25000),
    MEDIUM_CA_LAMP(30, new Item(32263), 50000),
    HARD_CA_LAMP(40, new Item(32264), 75000),
    ELITE_CA_LAMP(50, new Item(32261), 125000),
    MASTER_CA_LAMP(60, new Item(32265), 175000),
    GRANDMASTER_CA_LAMP(70, new Item(32266), 250000);

    Lamp(final int minimumLevel, final Item item, final double experience) {
        this.minimumLevel = minimumLevel;
        this.item = item;
        this.experience = experience / 5;
    }

    private final int minimumLevel;
    private final Item item;
    private final double experience;

    public static final Lamp[] all = values();
    private static final Map<Integer, Lamp> LAMPS = new HashMap<>();

    public static Lamp get(final int id) {
        return LAMPS.get(id);
    }

    static {
        for (val lamp : all) {
            LAMPS.put(lamp.getItem().getId(), lamp);
        }
    }
}
