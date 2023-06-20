package com.zenyte.game.content.skills.agility.draynorrooftop;

import com.zenyte.game.content.skills.agility.AgilityCourse;
import com.zenyte.game.world.entity.Location;

/**
 * @author Tommeh | 25 feb. 2018 : 19:20:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public final class DraynorRooftopCourse implements AgilityCourse {
	
	public static final Location[] MARK_LOCATIONS = { 
			new Location(3101, 3278, 3), new Location(3091, 3275, 3),
			new Location(3093, 3266, 3), new Location(3098, 3259, 3) };

	@Override
	public Class<?>[] getObstacles() {
		return new Class<?>[] { RoughWall.class, FirstTightRope.class, SecondTightRope.class, NarrowWall.class, Wall.class, Gap.class, Crate.class };
	}

	@Override
	public double getAdditionalCompletionXP() {
		return 0;
	}

}
