package com.zenyte.game.world.region;

import com.zenyte.game.world.entity.player.Player;

public interface PlayerPredicate {

	public boolean test(final Player player);
	
}
