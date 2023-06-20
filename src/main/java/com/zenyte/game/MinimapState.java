package com.zenyte.game;

import lombok.Getter;

/**
 * @author Kris | 4. dets 2017 : 0:26.23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public  enum MinimapState {

	ENABLED(0),
	MAP_UNCLICKABLE(1),
	MAP_DISABLED(2),
	COMPASS_DISABLED(3),
	COMPASS_DISABLED_MAP_UNCLICKABLE(4),
	DISABLED(5);
	
	@Getter private final int state;
	
	private MinimapState(final int state) {
		this.state = state;
	}
	
}
