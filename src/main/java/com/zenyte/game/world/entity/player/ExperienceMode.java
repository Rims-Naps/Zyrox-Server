package com.zenyte.game.world.entity.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @author Tommeh | 26-1-2019 | 22:07
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Getter
@RequiredArgsConstructor
public enum ExperienceMode {

    TIMES_50(50),
    TIMES_100(100);

    private final int rate;

    @Override
    public String toString() {
        val base = name();
        return base.substring(base.indexOf("_") + 1) + "x rate";
    }


}
