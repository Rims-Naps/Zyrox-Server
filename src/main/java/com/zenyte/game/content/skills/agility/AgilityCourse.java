package com.zenyte.game.content.skills.agility;

/**
 * @author Kris | 21. dets 2017 : 18:42.25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public interface AgilityCourse {

	public Class<?>[] getObstacles();
	public double getAdditionalCompletionXP();
	
}
