package com.zenyte.game.world.object;

import com.zenyte.game.item.Item;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public enum CastleWarsTable {

    POTIONS(4463, new Item(4045, 1)),
    ROCKS(4460, new Item(4043, 1)),
    ROPE(4462, new Item(954, 1)),
    BARRICADES(4461, new Item(4053, 1)),
    TOOLKITS(4459, new Item(4051, 1)),
    PICKAXES(4464, new Item(1265, 1)),
    BANDAGES(4458, new Item(4049, 1)),
    ;

    @Getter private final int object;
    @Getter private final Item loot;

    public static final Map<Integer, CastleWarsTable> DATA = new HashMap<>();

    CastleWarsTable(final int object, final Item loot) {
        this.object = object;
        this.loot = loot;
    }

    public static CastleWarsTable getData(final int objectId) {
        val entry = DATA.get(objectId);
        return entry == null ? PICKAXES : entry;
    }

    static {
        for(final CastleWarsTable table : values())
            DATA.put(table.getObject(), table);
    }
}
