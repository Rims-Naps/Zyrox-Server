package com.zenyte.game.content.skills.agility.gnomecourse;

import com.zenyte.game.content.skills.agility.AgilityCourse;

/**
 * @author Kris | 21. dets 2017 : 18:48.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class GnomeCourse implements AgilityCourse {

	@Override
	public Class<?>[] getObstacles() {
		return new Class<?>[] { LogBalance.class, WallObstacleNet.class, BottomTreeBranch.class,
			BalancingRope.class, UpperTreeBranch.class, StandingObstacleNet.class, ObstaclePipe.class };
	}

	@Override
	public double getAdditionalCompletionXP() {
		return 39;
	}

}
