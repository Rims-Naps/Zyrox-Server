package com.zenyte.game.item;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import mgi.types.config.items.ItemDefinitions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 15/03/2019 18:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum SkillcapePerk {

    ATTACK(9747, 9748, 10639, 13280, 13282, 13342, 31050, 32367),
    COOKING(9801, 9802, 10658, 13280, 13282, 13342, 32045, 32367),
    FARMING(9810, 9811, 10661, 13280, 13282, 13342, 32049, 32367),
    MINING(9792, 9793, 10655, 13280, 13282, 13342, 32066, 32367),
    RANGED(9756, 9757, 10642, 13280, 13282, 13342, 13337, 21898, 32070, 32367),
    SLAYER(9786, 9787, 10653, 13280, 13282, 13342, 32074, 32367),
    THIEVING(9777, 9778, 10650, 13280, 13282, 13342, 32078, 32367),
    WOODCUTTING(9807, 9808, 10660, 13280, 13282, 13342, 32080, 32367),
    FIREMAKING(9804, 9805, 10659, 13280, 13282, 13342, 32055, 32367),
    RUNECRAFTING(9765, 9766, 10645, 13280, 13282, 13342, 32072, 32367),
    HITPOINTS(9768, 9769, 10647, 13280, 13282, 13342, 32060, 32367),
    HERBLORE(9774, 9775, 10649, 13280, 13282, 13342, 32058, 32367),
    AGILITY(9771, 9772, 10648, 13340, 13341, 13280, 13282, 13342, 32041, 32367),
    PRAYER(9759, 9760, 10643, 13280, 13282, 13342, 32068, 32367),
    SMITHING(9795, 9796, 10656, 13280, 13282, 13342, 32076, 32367),
    STRENGTH(9750, 9751, 10640, 13280, 13282, 13342, 32039, 32367),
    HUNTER(9948, 9949, 10646, 13280, 13282, 13342, 32062, 32367),
    FISHING(9798, 9799, 10657, 13280, 13282, 13342, 32051, 32367),
    CONSTRUCTION(9789, 9790, 10654, 13280, 13282, 13342, 32043, 32367),
    CRAFTING(9780, 9781, 10651, 13280, 13282, 13342, 32047, 32367),
    DEFENCE(9753, 9754, 10641, 13280, 13282, 13342, 32037, 32367),
    MAGIC(9762, 9763, 10644, 13280, 13282, 13342, 32064, 32367),
    FLETCHING(9783, 9784, 10652, 13280, 13282, 13342, 32053, 32367),
    DIARY(19476, 13069),
    MUSIC(13221, 13222)

    ;

    @Getter private final int[] capes;
    @Getter private final int[] skillCapes;

    SkillcapePerk(final int... capes) {
        this.capes = capes;
        val list = new IntArrayList();
        for (val cape : capes) {
            val definitions = ItemDefinitions.get(cape);
            if (definitions == null) continue;
            if (definitions.getName().toLowerCase().contains("max") || definitions.getName().toLowerCase().contains("completionist"))
                continue;
            list.add(cape);
        }
        this.skillCapes = list.toIntArray();
    }


    public boolean isEffective(@NotNull final Player player) {
        val cape = player.getEquipment().getId(EquipmentSlot.CAPE);
        return cape != -1 && ArrayUtils.contains(capes, cape);
    }

    public boolean isCarrying(@NotNull final Player player) {
        return player.carryingAny(capes);
    }
}
