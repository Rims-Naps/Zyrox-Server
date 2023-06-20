package mgi.custom.christmas;

import mgi.types.config.ObjectDefinitions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

/**
 * @author Kris | 24/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@AllArgsConstructor
public enum ChristmasObject {

    CAVE_ENTRANCE(8930, 40027),
    HOLE(19640, 40028),
    HOLLOW_LOG(19425, 40029),
    CHEST(32758, 40030),
    TUNNEL(19424, 40031),
    SNOW_DRIFT(19435, 40032),
    ALT_HOLLOW_LOG(19422, 40033)
    ;

    private final int originalObject, repackedObject;
    public static Int2IntMap redirectedIds = new Int2IntOpenHashMap();
    ObjectDefinitions.ObjectDefinitionsBuilder builder() {
        return ObjectDefinitions.getOrThrow(originalObject).toBuilder().id(repackedObject);
    }

    static {
        for (val value : values()) {
            redirectedIds.put(value.originalObject, value.repackedObject);
        }
    }
}
