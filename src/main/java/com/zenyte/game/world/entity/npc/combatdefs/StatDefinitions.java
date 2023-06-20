package com.zenyte.game.world.entity.npc.combatdefs;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.Arrays;

/**
 * @author Kris | 18/11/2018 02:51
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@Setter
public class StatDefinitions {

    int[] combatStats = new int[5];
    int[] aggressiveStats = new int[5];
    int[] defensiveStats = new int[5];
    int[] otherBonuses = new int[3];

    int[] getArray(final StatType type) {
        val index = type.ordinal() / 5;
        return index == 0 ? combatStats : index == 1 ? aggressiveStats : index == 2 ? defensiveStats : otherBonuses;
    }

    public void set(final StatType type, final int value) {
        getArray(type)[type.index()] = value;
    }

    public int get(final StatType type) {
        return getArray(type)[type.index()];
    }

    public StatDefinitions clone() {
        val defs = new StatDefinitions();
        defs.combatStats = Arrays.copyOf(combatStats, combatStats.length);
        defs.aggressiveStats = Arrays.copyOf(aggressiveStats, aggressiveStats.length);
        defs.defensiveStats = Arrays.copyOf(defensiveStats, defensiveStats.length);
        defs.otherBonuses = Arrays.copyOf(otherBonuses, otherBonuses.length);
        return defs;
    }

    public int getAggressiveStat(final StatType type) {
        return aggressiveStats[type.ordinal()];
    }

}
