package com.zenyte.game.content.skills.agility.faladorrooftop;

import com.zenyte.game.content.skills.agility.AgilityCourse;
import com.zenyte.game.world.entity.Location;

/**
 * @author Noele | Apr 29, 2018 : 12:29:29 PM
 * @see http://noeles.life || noele@zenyte.com
 */
public class FaladorRooftopCourse implements AgilityCourse {
	
	public static final Location[] MARK_LOCATIONS = { 
			new Location(3046, 3345, 3), new Location(3046, 3365, 3), new Location(3036, 3363, 3),
			new Location(3015, 3355, 3), new Location(3011, 3339, 3), new Location(3023, 3334, 3) };

	@Override
	public Class<?>[] getObstacles() {
		return new Class<?>[] { RoughWall.class, Tightrope.class, HandHolds.class, JumpGap.class, JumpLedge.class, FinishCourse.class };
	}

	@Override
	public double getAdditionalCompletionXP() {
		return 0;
	}

}
