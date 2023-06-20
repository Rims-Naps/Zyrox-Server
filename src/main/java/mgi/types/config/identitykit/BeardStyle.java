package mgi.types.config.identitykit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tommeh | 13-2-2019 | 16:55
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum BeardStyle {

    CLEAN_SHAVEN(0, 14),
    GOATEE(1, 10),
    LONG(2, 11),
    MEDIUM(3, 12),
    SMALL_MOUSTACHE(4, 13),
    SHORT(5, 15),
    POINTY(6, 16),
    SPLIT(7, 17),
    HANDLEBAR(8, 111),
    MUTTON(9, 112),
    FULL_MOTTON(10, 113),
    BIG_MOUSTACHE(11, 114),
    WAXED_MOUSTACHE(12, 115),
    DALI(13, 116),
    VIZIER(14, 117);

    private final int slotId, id;
    private static final BeardStyle[] VALUES = values();
    private static final Map<Integer, Integer> STYLES = new HashMap<>(VALUES.length);

    static {
        for (val style : VALUES) {
            STYLES.put(style.slotId, style.id);
        }
    }

    public static int getStyle(final int slotId) {
        return STYLES.get(slotId);
    }
}
