package com.zenyte.game.content.theatreofblood;

import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 5/22/2020 | 5:47 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public interface TheatreAreaController {

    default void onLoad() {
    }

    default void onCompletion() {
    }

    default void onStart(final Player player) {
    }

    default TheatreRoom onAdvancement() {
        return null;
    }

}
