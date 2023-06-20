package com.zenyte.game.content.skills.agility.ardougnerooftop;

import com.zenyte.game.content.skills.agility.AgilityCourse;
import com.zenyte.game.world.entity.Location;

/**
 * @author Kris | 08/06/2019 09:04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ArdougneRooftopCourse implements AgilityCourse {

    public static final Location[] MARK_LOCATIONS = { new Location(2657, 3318, 3) };

    @Override
    public Class<?>[] getObstacles() {
        return new Class<?>[] { WoodenBeams.class, Gap.class, Plank.class, Gap2.class, Gap3.class, SteepRoof.class, Gap4.class };
    }

    @Override
    public double getAdditionalCompletionXP() {
        return 0;
    }

}
