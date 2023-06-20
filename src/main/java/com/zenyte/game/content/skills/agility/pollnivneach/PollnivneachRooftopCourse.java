package com.zenyte.game.content.skills.agility.pollnivneach;

import com.zenyte.game.content.skills.agility.AgilityCourse;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.masks.Animation;

/**
 * @author Christopher
 * @since 3/30/2020
 */
public class PollnivneachRooftopCourse implements AgilityCourse {
    public static final Animation runningStartAnim = new Animation(1995);
    public static final Animation landAnim = new Animation(2588);
    public static final Location[] MARK_LOCATIONS = {
            new Location(3346, 2968, 1), new Location(3354, 2974, 1),
            new Location(3361, 2993, 2)};

    @Override
    public Class<?>[] getObstacles() {
        return new Class<?>[]{
                Basket.class, MarketStall.class, Banner.class, Gap.class, Tree.class, RoughWall.class, MonkeyBars.class,
                SteppingTree.class, DryingLine.class
        };
    }

    @Override
    public double getAdditionalCompletionXP() {
        return 0;
    }
}
