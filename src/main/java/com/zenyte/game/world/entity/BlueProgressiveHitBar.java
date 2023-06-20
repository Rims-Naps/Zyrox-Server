package com.zenyte.game.world.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Kris | 18. nov 2017 : 6:02.57
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class BlueProgressiveHitBar extends HitBar {

	public BlueProgressiveHitBar(final int percentage) {
		this.percentage = percentage;
	}

	@Getter @Setter private int percentage;

	@Override
	public int getType() {
		return 7;
	}

}
