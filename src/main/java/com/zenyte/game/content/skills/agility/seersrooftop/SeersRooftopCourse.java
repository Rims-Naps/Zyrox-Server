/**
 * 
 */
package com.zenyte.game.content.skills.agility.seersrooftop;

import com.zenyte.game.content.skills.agility.AgilityCourse;
import com.zenyte.game.world.entity.Location;

/**
 * @author Noele | May 1, 2018 : 2:40:05 AM
 */
public class SeersRooftopCourse implements AgilityCourse {
	
	public static final Location[] MARK_LOCATIONS = { 
			new Location(2725, 3494, 3), new Location(2708, 3492, 2), new Location(2712, 3478, 2),
			new Location(2702, 3473, 3), new Location(2699, 3462, 2) };

	@Override
	public Class<?>[] getObstacles() {
		return new Class<?>[] { StartCourse.class, FirstJumpGap.class, Tightrope.class, SecondJumpGap.class, ThirdJumpGap.class, FinishCourse.class };
	}

	@Override
	public double getAdditionalCompletionXP() {
		return 0;
	}

}
