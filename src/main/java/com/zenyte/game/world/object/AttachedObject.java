package com.zenyte.game.world.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kris | 1. apr 2018 : 4:02.52
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 *      profile</a>}
 */
@AllArgsConstructor
public final class AttachedObject {

	@Getter
	private final WorldObject object;
	@Getter
	private final int startTime, endTime, minX, maxX, minY, maxY;

}