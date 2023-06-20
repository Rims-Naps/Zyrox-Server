package com.zenyte.game.content.skills.herblore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tommeh | 17-3-2019 | 19:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum PoisonAttachement {

    POISON(187),
    POISON_PLUS(5937),
    POISON_PLUS_PLUS(5940);

    private final int potion;

    public static final Set<PoisonAttachement> ALL = EnumSet.allOf(PoisonAttachement.class);
    private static final Map<Integer, PoisonAttachement> MAP = new HashMap<>();

    static {
        for (val attachement : ALL) {
            MAP.put(attachement.getPotion(), attachement);
        }
    }

    public static PoisonAttachement get(final int id) {
        return MAP.get(id);
    }
}