package com.zenyte.game.content.skills.agility.barbariancourse;

import com.zenyte.game.content.skills.agility.AgilityCourse;

/**
 * @author Kris | 9. veebr 2018 : 4:57.02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class BarbarianOutpostCourse implements AgilityCourse {

	@Override
	public Class<?>[] getObstacles() {
		return new Class<?>[] { Ropeswing.class, LogBalance.class, ObstacleNet.class, BalancingLedge.class, CrumblingWall.class };
	}

	@Override
	public double getAdditionalCompletionXP() {
		return 46.2;
	}

}
