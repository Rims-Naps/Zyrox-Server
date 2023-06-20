package com.zenyte.game.content.skills.agility.canifisrooftop;

import com.zenyte.game.content.skills.agility.AgilityCourse;
import com.zenyte.game.world.entity.Location;

public class CanifisRooftopCourse implements AgilityCourse {
	
	public static final Location[] MARK_LOCATIONS = { 
			new Location(3499, 3505, 2), new Location(3488, 3500, 2), 
			new Location(3476, 3494, 3), new Location(3478, 3483, 2), 
			new Location(3497, 3471, 3), new Location(3514, 3478, 2), };

	@Override
	public Class<?>[] getObstacles() {
		return new Class<?>[] { TallTree.class, JumpGap.class, JumpLongGap.class, PoleVault.class };
	}

	@Override
	public double getAdditionalCompletionXP() {
		return 0;
	}

}
