package com.zenyte.game.world.entity.player.dailychallenge;

import com.zenyte.game.util.TextUtils;

/**
 * @author Tommeh | 02/05/2019 | 22:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public enum ChallengeDifficulty {
    EASY,
    MEDIUM,
    HARD,
    ELITE;

    @Override
    public String toString() {
        return TextUtils.formatName(name().toLowerCase());
    }
}
