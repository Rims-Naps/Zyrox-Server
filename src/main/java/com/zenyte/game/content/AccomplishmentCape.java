package com.zenyte.game.content;

import com.zenyte.game.util.TextUtils;
import com.zenyte.game.world.entity.player.Skills;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tommeh | 24-3-2019 | 17:41
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum AccomplishmentCape {

    ATTACK(Skills.ATTACK, 9747, 9748, 9749),
    DEFENCE(Skills.DEFENCE, 9753, 9754, 9755),
    STRENGTH(Skills.STRENGTH, 9750, 9751, 9752),
    HITPOINTS(Skills.HITPOINTS, 9768, 9769, 9770),
    RANGED(Skills.RANGED, 9756, 9757, 9758),
    PRAYER(Skills.PRAYER, 9759, 9760, 9761),
    MAGIC(Skills.MAGIC, 9762, 9763, 9764),
    COOKING(Skills.COOKING, 9801, 9802, 9803),
    WOODCUTTING(Skills.WOODCUTTING, 9807, 9808, 9809),
    FLETCHING(Skills.FLETCHING, 9783, 9784, 9785),
    FISHING(Skills.FISHING, 9798, 9799, 9800),
    FIREMAKING(Skills.FIREMAKING, 9804, 9805, 9806),
    CRAFTING(Skills.CRAFTING, 9780, 9781, 9782),
    SMITHING(Skills.SMITHING, 9795, 9796, 9797),
    MINING(Skills.MINING, 9792, 9793, 9794),
    HERBLORE(Skills.HERBLORE, 9774, 9775, 9776),
    AGILITY(Skills.AGILITY, 9771, 9772, 9773),
    THIEVING(Skills.THIEVING, 9777, 9778, 9779),
    SLAYER(Skills.SLAYER, 9786, 9787, 9788),
    FARMING(Skills.FARMING, 9810, 9811, 9812),
    RUNECRAFTING(Skills.RUNECRAFTING, 9765, 9766, 9767),
    CONSTRUCTION(Skills.CONSTRUCTION, 9789, 9790, 9791),
    HUNTER(Skills.HUNTER, 9948, 9949, 9950),
    DIARY(-1, 19476, 13069, 13070),
    MUSIC(-1,13221,13222,13223),
    ATTACK200M(Skills.ATTACK,9747,31050,9749),
    DEFENSE200M(Skills.DEFENCE,9753,32037,9755),
    STRENGTH200M(Skills.STRENGTH,9750,32039,9752),
    AGILITY200M(Skills.AGILITY,9771,32041,9773),
    CONSTRUCTION200M(Skills.CONSTRUCTION,9789,32043,9791),
    COOKING200M(Skills.COOKING,9801,32045,9803),
    CRAFTING200M(Skills.CRAFTING,9780,32047,9782),
    FARMING200M(Skills.FARMING,9810, 32049,9812),
    FISHING200M(Skills.FISHING,9798,32051,9800),
    FLETCHING200M(Skills.FLETCHING,9783,32053,9785),
    FIREMAKING200M(Skills.FIREMAKING,9804,32055,9806),
    HERBLORE200M(Skills.HERBLORE,9774,32058,9776),
    HITPOINTS200M(Skills.HITPOINTS,9768,32060,9770),
    HUNTER200M(Skills.HUNTER,9948,32062,9950),
    MAGIC200M(Skills.MAGIC,9762,32064,9764),
    MINING200M(Skills.MINING,9792,32066,9794),
    PRAYER200M(Skills.PRAYER,9759,32068,9761),
    RANGING200M(Skills.RANGED,9756,32070,9758),
    RUNECRAFTING200M(Skills.RUNECRAFTING,9765,32072,9767),
    SLAYER200M(Skills.SLAYER,9786,32074,9788),
    SMITHING200M(Skills.SMITHING,9795,32076,9797),
    THIEVING200M(Skills.THIEVING,9777,32078,9779),
    WOODCUTTING200M(Skills.WOODCUTTING,9807,32080,9809);

    private final int skill, untrimmed, trimmed, hood;
    private static final Set<AccomplishmentCape> ALL = EnumSet.allOf(AccomplishmentCape.class);
    private static final Map<Integer, AccomplishmentCape> CAPES = new HashMap<>();
    private static final Map<Integer, AccomplishmentCape> BY_SKILL = new HashMap<>();

    public static AccomplishmentCape get(final int id) {
        return CAPES.get(id);
    }

    public static AccomplishmentCape getBySkill(final int id) {
        return BY_SKILL.get(id);
    }

    static {
        for (val cape : ALL) {
            CAPES.put(cape.getUntrimmed(), cape);
            CAPES.put(cape.getTrimmed(), cape);
            BY_SKILL.put(cape.getSkill(), cape);
        }
    }

    @Override
    public String toString() {
        return TextUtils.capitalizeFirstCharacter(name().toLowerCase());
    }
}
