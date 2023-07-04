package com.zenyte.game.world.entity.player.dailychallenge.reward.impl;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dailychallenge.reward.ChallengeReward;
import com.zenyte.game.world.entity.player.dailychallenge.reward.RewardType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * @author Tommeh | 04/05/2019 | 14:03
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@RequiredArgsConstructor
public class ExperienceReward implements ChallengeReward {

    @Getter  private final int skill;
    private final int experience;

    @Override
    public void apply(Player player) {
        var givenXp = experience * player.getExperienceRate(skill);
        if (player.getSkillingXPRate() == 10) {
            givenXp *= 1.15;
        } else if (player.getSkillingXPRate() == 5) {
            givenXp *= 1.25;
        }
        player.getSkills().addXp(skill, experience);
        player.sendMessage("<col=ce8500><shad=000000>You have been awarded with " + Utils.format(givenXp) + " XP in " + Skills.getSkillName(skill) + "!");
    }

    public int getExperience(final Player player) {
        var experience = this.experience;
        if (player.getSkillingXPRate() == 10) {
            experience *= 1.15;
        } else if (player.getSkillingXPRate() == 5) {
            experience *= 1.25;
        }
        return experience;
    }

    @Override
    public RewardType getType() {
        return RewardType.EXPERIENCE;
    }
}
