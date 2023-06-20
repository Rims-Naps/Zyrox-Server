package com.zenyte.game.world.entity.player.dailychallenge;

import lombok.Data;

/**
 * @author Tommeh | 03/05/2019 | 22:16
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Data
public class ChallengeDetails {

    private final ChallengeDifficulty difficulty;
    private final Object[] additionalInformation;

    public ChallengeDetails(final ChallengeDifficulty difficulty, final Object... additionalInformation) {
        this.difficulty = difficulty;
        this.additionalInformation = additionalInformation;
    }
}
