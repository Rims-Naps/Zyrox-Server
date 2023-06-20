package com.zenyte.game.world.entity.player.var;

import com.zenyte.game.world.entity.player.Player;

/**
 * @author Kris | 27. juuli 2018 : 18:27:52
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@FunctionalInterface
public interface VarValueFunction {

	public int getValue(final Player player);
	
}
