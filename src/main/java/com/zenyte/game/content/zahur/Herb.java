package com.zenyte.game.content.zahur;

import com.zenyte.game.item.Item;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tommeh | 16-3-2019 | 18:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum Herb {

    GUAM_LEAF(new Item(249), new Item(199), new Item(91)),
    MARRENTILL(new Item(251), new Item(201), new Item(93)),
    TARROMIN(new Item(253), new Item(203), new Item(95)),
    HARRALANDER(new Item(255), new Item(205), new Item(97)),
    RANARR_WEED(new Item(257), new Item(207), new Item(99)),
    TOADFLAX(new Item(2998), new Item(3049), new Item(3002)),
    IRIT_LEAF(new Item(259), new Item(209), new Item(101)),
    AVANTOE(new Item(261), new Item(211), new Item(103)),
    KWUARM(new Item(263), new Item(213), new Item(105)),
    SNAPDRAGON(new Item(3000), new Item(3051), new Item(3004)),
    CADANTINE(new Item(265), new Item(215), new Item(107)),
    LANTADYME(new Item(2481), new Item(2485), new Item(2483)),
    DWARF_WEED(new Item(267), new Item(217), new Item(109)),
    TORSTOL(new Item(269), new Item(219), new Item(111)),
    SNAKE_WEED(new Item(1526), new Item(1525), new Item(-1));

    private static final Set<Herb> ALL = EnumSet.allOf(Herb.class);
    private static final Map<Integer, Herb> HERBS = new HashMap<>(ALL.size());

    public static final Map<Integer, Herb> CLEAN_HERBS = new HashMap<>(ALL.size());


    static {
        for (val herb : ALL) {
            //HERBS.put(herb.getClean().getId(), herb);
            HERBS.put(herb.getClean().getDefinitions().getNotedId(), herb);
            HERBS.put(herb.getGrimy().getDefinitions().getNotedId(), herb);
            CLEAN_HERBS.put(herb.getClean().getDefinitions().getUnnotedOrDefault(), herb);
        }
    }

    private final Item clean, grimy, unfinishedPotion;

    public static Herb get(final int id, final boolean grimyAllowed) {
        for (val entry : HERBS.entrySet()) {
            val key = entry.getKey();
            val herb = entry.getValue();
            if (!grimyAllowed && herb.getGrimy().getDefinitions().getNotedId() == id) {
                return null;
            }
            if (key == id) {
                return herb;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name().toLowerCase().replace("_", " ");
    }

}
