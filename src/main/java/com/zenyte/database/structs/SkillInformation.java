package com.zenyte.database.structs;

import java.util.ArrayList;
import java.util.List;

public enum SkillInformation {

    ATTACK(),
    DEFENCE(),
    STRENGTH(),
    HITPOINTS(),
    RANGED(),
    PRAYER(),
    MAGIC(),
    COOKING(),
    WOODCUTTING(),
    FLETCHING(),
    FISHING(),
    FIREMAKING(),
    CRAFTING(),
    SMITHING(),
    MINING(),
    HERBLORE(),
    AGILITY(),
    THIEVING(),
    SLAYER(),
    FARMING(),
    RUNECRAFTING(),
    HUNTER(),
    CONSTRUCTION(),
    ;

    public static final List<SkillInformation> all = new ArrayList<>();
    public static final SkillInformation[] VALUES = values();


    SkillInformation() {}

    static {
        for(SkillInformation info :  VALUES)
            all.add(info);
    }

}
