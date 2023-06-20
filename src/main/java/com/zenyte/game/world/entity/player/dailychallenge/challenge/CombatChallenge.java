package com.zenyte.game.world.entity.player.dailychallenge.challenge;

import com.zenyte.game.world.entity.player.dailychallenge.ChallengeCategory;
import com.zenyte.game.world.entity.player.dailychallenge.ChallengeDifficulty;
import com.zenyte.game.world.entity.player.dailychallenge.reward.ChallengeReward;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.zenyte.game.world.entity.player.dailychallenge.ChallengeDifficulty.EASY;

/**
 * @author Tommeh | 04/05/2019 | 13:33
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@RequiredArgsConstructor
public enum CombatChallenge implements DailyChallenge {

    KILL_5_BEARS("Kill 5 Bears", EASY, 5, "bear");

    private final String name;
    private final ChallengeDifficulty difficulty;
    private final ChallengeReward[] rewards;
    private final int length;
    @Getter
    private final String npc;

    CombatChallenge(final String name, final ChallengeDifficulty difficulty, final int length, final String npc, final ChallengeReward... rewards) {
        this.name = name;
        this.difficulty = difficulty;
        this.length = length;
        this.npc = npc;
        this.rewards = rewards;
    }

    public static final CombatChallenge[] all = values();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChallengeCategory getCategory() {
        return ChallengeCategory.COMBAT;
    }

    @Override
    public ChallengeDifficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public ChallengeReward[] getRewards() {
        return rewards;
    }

    @Override
    public int getLength() {
        return length;
    }
}