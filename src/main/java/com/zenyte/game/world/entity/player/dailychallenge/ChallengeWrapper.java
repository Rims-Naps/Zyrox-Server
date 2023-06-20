package com.zenyte.game.world.entity.player.dailychallenge;

import com.zenyte.game.world.entity.player.dailychallenge.challenge.CombatChallenge;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.DailyChallenge;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import lombok.val;

import java.util.*;

/**
 * @author Tommeh | 03/05/2019 | 22:55
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ChallengeWrapper {

    public static Map<String, DailyChallenge> challenges = new HashMap<>();

    static {
        for (val challenge : SkillingChallenge.all) {
            challenges.put(challenge.name(), challenge);
        }
        for (val challenge : CombatChallenge.all) {
            challenges.put(challenge.name(), challenge);
        }
    }

    public static DailyChallenge get(final String name) {
        return challenges.get(name);
    }

}