package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.DailyChallenge;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.entity.player.dailychallenge.reward.RewardType;
import com.zenyte.game.world.entity.player.dailychallenge.reward.impl.ExperienceReward;
import com.zenyte.game.world.entity.player.dailychallenge.reward.impl.ItemReward;
import lombok.val;

import java.util.Optional;

/**
 * @author Tommeh | 06/05/2019 | 16:38
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class DailyChallengesOverviewInterface extends Interface {

    @Override
    protected void attach() {
        put(5, "View Daily Challenge");
        put(22, "Claim");
    }

    @Override
    public void open(Player player) {
        val manager = player.getDailyChallengeManager();
        val challenges = manager.getChallengeProgression();
        player.getInterfaceHandler().sendInterface(this);
        if (challenges.isEmpty()) {
            return;
        }
        var index = 0;
        for (val challenge : challenges.keySet()) {
            player.getPacketDispatcher().sendClientScript(10301, index, challenge.getName());
            index++;
        }
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("View Daily Challenge"), 0, challenges.size() * 2, AccessMask.CLICK_OP1);
        viewDailyChallenge(player, manager.getChallenge(0));
    }

    @Override
    public void close(final Player player, final Optional<GameInterface> replacement) {
        player.addTemporaryAttribute("daily_challenge_claimable", 0);
    }

    private void viewDailyChallenge(final Player player, final DailyChallenge challenge) {
        val manager = player.getDailyChallengeManager();
        val progression = manager.getProgress(challenge);
        if (progression == null) {
            return;
        }
        val progress = progression.getProgress();
        val claimable = progression.isCompleted() && player.getBooleanTemporaryAttribute("daily_challenge_claimable");
        val percentage = 278D / challenge.getLength() * progress;
        val extra = challenge instanceof SkillingChallenge ? ((SkillingChallenge) challenge).getSkill() : -1;
        val builder = new StringBuilder();
        val rewards = challenge.getRewards();
        for (val reward : rewards) {
            if (reward.getType().equals(RewardType.ITEM)) {
                val itemReward = (ItemReward) reward;
                builder.append(0).append("|");
                builder.append(itemReward.getItem().getId()).append("|");
                builder.append(itemReward.getItem().getAmount()).append("|");
            } else {
                val experienceReward = (ExperienceReward) reward;
                builder.append(1).append("|");
                builder.append(experienceReward.getSkill()).append("|");
                builder.append(experienceReward.getExperience(player) * player.getExperienceRate(experienceReward.getSkill())).append("|");
            }
        }
        player.getPacketDispatcher().sendClientScript(10302, challenge.getName(), progress, challenge.getLength(), (int) percentage, challenge.getCategory().toString(), challenge.getDifficulty().toString(), extra, builder.length() == 0 ? "" : builder.toString(), claimable ? 1 : 0);
        player.getPacketDispatcher().sendComponentSettings(getInterface(), getComponent("Claim"), -1, 0, claimable ? AccessMask.CLICK_OP1 : AccessMask.NONE);
        player.addTemporaryAttribute("active_daily_challenge", challenge);
    }

    @Override
    protected void build() {
        bind("View Daily Challenge", (player, slotId, itemId, option) -> {
            val challenge = player.getDailyChallengeManager().getChallenge(slotId / 2);
            if (challenge == null) {
                return;
            }
            viewDailyChallenge(player, challenge);
        });
        bind("Claim", player -> {
            val obj = player.getTemporaryAttributes().get("active_daily_challenge");
            if (!(obj instanceof DailyChallenge)) {
                return;
            }
            val manager = player.getDailyChallengeManager();
            val challenge = (DailyChallenge) obj;
            if (manager.claim(challenge)) {
                open(player);
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.DAILY_CHALLENGES_OVERVIEW;
    }
}
