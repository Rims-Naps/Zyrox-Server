package com.zenyte.game.content.skills.agility.wildernesscourse;

import com.zenyte.game.content.skills.agility.AgilityCourse;

/**
 * @author Tommeh | 24 feb. 2018 : 23:24:01
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class WildernessCourse implements AgilityCourse {

	@Override
	public Class<?>[] getObstacles() {
		return new Class<?>[] { ObstaclePipe.class, RopeSwing.class, SteppingStone.class, LogBalance.class, Rocks.class };
	}

	@Override
	public double getAdditionalCompletionXP() {
		return 499;
	}

}
