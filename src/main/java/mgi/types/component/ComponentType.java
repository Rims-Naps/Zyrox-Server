package mgi.types.component;

import com.zenyte.game.util.Utils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Tommeh | 02/02/2020 | 18:48
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
@RequiredArgsConstructor
public enum ComponentType {

    LAYER(0),
    ITEM_CONTAINER(2),
    RECTANGLE(3),
    TEXT(4),
    GRAPHIC(5),
    MODEL(6),
    LINE(9);

    private final int id;

    private static Object2ObjectOpenHashMap<String, ComponentType> types;

    public static ComponentType get(final String type) {
        return types.get(type);
    }

    static {
        Utils.populateMap(values(), types = new Object2ObjectOpenHashMap<>(), type -> type.toString());
    }

    @Override
    public String toString() {
        return name().replaceAll("_", "").toLowerCase();
    }
}
