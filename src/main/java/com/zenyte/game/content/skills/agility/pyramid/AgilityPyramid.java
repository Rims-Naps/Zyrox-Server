package com.zenyte.game.content.skills.agility.pyramid;

import com.zenyte.game.content.skills.agility.AgilityCourse;
import com.zenyte.game.world.entity.player.VarManager;
import com.zenyte.utils.StaticInitializer;

@StaticInitializer
public class AgilityPyramid implements AgilityCourse {
    public static final double MAX_COMPLETION_BONUS = 1000;
    public static final int HIDE_PYRAMID_VARBIT = 1556;
    public static final int MOVING_BLOCK_VARBIT = 1550;
    static {
        VarManager.appendPersistentVarbit(HIDE_PYRAMID_VARBIT);
        VarManager.appendPersistentVarbit(MOVING_BLOCK_VARBIT);
    }

    @Override
    public Class<?>[] getObstacles() {
        return new Class<?>[] { LowWall.class, Ledge.class, Plank.class, CrossGap.class, JumpGap.class, ClimbingRocks.class, Doorway.class};
    }

    @Override
    public double getAdditionalCompletionXP() {
        return 0;
    }
}
