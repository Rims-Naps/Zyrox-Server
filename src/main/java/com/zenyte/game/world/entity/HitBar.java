package com.zenyte.game.world.entity;

import com.zenyte.game.world.entity.player.Player;

public abstract class HitBar {

	public abstract int getType();

	public abstract int getPercentage();

	public int getToPercentage() {
		return getPercentage();
	}

	public int getDelay() {
		return 0;
	}

	public boolean display(Player player) {
		return true;
	}
}
