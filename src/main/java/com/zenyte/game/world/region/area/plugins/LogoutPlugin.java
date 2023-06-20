package com.zenyte.game.world.region.area.plugins;

import com.zenyte.game.world.entity.player.Player;

/**
 * @author Kris | 30. juuni 2018 : 16:19:00
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public interface LogoutPlugin {

    default boolean manualLogout(final Player player) {
        return true;
    }

    default void onLogout(final Player player) {

    }
	
}
