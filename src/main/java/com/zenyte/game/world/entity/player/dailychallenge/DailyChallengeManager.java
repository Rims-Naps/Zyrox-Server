package com.zenyte.game.world.entity.player.dailychallenge;

import com.google.common.eventbus.Subscribe;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.GameSetting;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.DailyChallenge;
import com.zenyte.game.world.entity.player.dailychallenge.challenge.SkillingChallenge;
import com.zenyte.game.world.entity.player.dailychallenge.reward.RewardType;
import com.zenyte.plugins.events.InitializationEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.Map;

/**
 * @author Tommeh | 02/05/2019 | 22:36
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class DailyChallengeManager {

    private final transient Player player;
    @Setter
    @Getter
    private Map<DailyChallenge, ChallengeProgress> challengeProgression;

    public DailyChallengeManager(final Player player) {
        this.player = player;
        this.challengeProgression = new Object2ObjectLinkedOpenHashMap<>(3);
    }

    @Subscribe
    public static final void onInitialization(final InitializationEvent event) {
        val player = event.getPlayer();
        val savedPlayer = event.getSavedPlayer();
        val otherManager = savedPlayer.getDailyChallengeManager();
        if (otherManager == null || otherManager.challengeProgression == null) {
            return;
        }
        player.getDailyChallengeManager().setChallengeProgression(otherManager.challengeProgression);
    }

    public void update(final DailyChallenge challenge, final int progress) {
        val challengeProgress = challengeProgression.get(challenge);
        if (challengeProgress == null || challengeProgress.isCompleted()) {
            return;
        }
        challengeProgress.progress(progress);
        check(challenge);
    }

    public void update(final DailyChallenge challenge) {
        update(challenge, 1);
    }

    private void check(final DailyChallenge challenge) {
        val progress = challengeProgression.get(challenge);
        if (progress == null) {
            return;
        }
        if (progress.getProgress() == challenge.getLength()) {
            player.sendMessage("<col=ce8500><shad=000000>Daily Challenge complete: " + challenge.getName() + "! Return to the Challenge Headmaster in Edgeville to claim your reward!");
        } else {
            if (player.getNumericAttribute(GameSetting.DAILY_CHALLENGE_NOTIFICATIONS.toString()).intValue() == 1) {
                player.sendMessage("<col=ce8500><shad=000000>Daily Challenge updated: " + progress);
            }
        }
    }

    public void assignChallenge(final DailyChallenge challenge) {
        val amountToRemove = Math.max(0, (challengeProgression.size() + 1) - getMaximumChallenges());
        if (amountToRemove > 0) {
            for (int i = 0; i < amountToRemove; i++) {
                val removable = getRemovableChallenge();
                if (removable != null) {
                    challengeProgression.remove(removable);
                }
            }
        }
        challengeProgression.put(challenge, new ChallengeProgress(challenge));
        player.sendMessage("<img=13><col=ce8500><shad=000000>New Daily Challenge: " + challengeProgression.get(challenge));
    }

    private DailyChallenge getRemovableChallenge() {
        val iterator = challengeProgression.entrySet().iterator();
        if (iterator.hasNext()) {
            return iterator.next().getKey();
        }
        return null;
    }

    public DailyChallenge getRandomChallenge() {
        val category = ChallengeCategory.SKILLING;
        var count = 50;
        DailyChallenge challenge = null;
        while (--count > 0 && challenge == null) {
            val information = category.getDetails(player);
            for (val c : ChallengeWrapper.challenges.values()) {
                if (challengeProgression.containsKey(c)) {
                    continue;
                }
                if (!c.getCategory().equals(category) || !c.getDifficulty().equals(information.getDifficulty())) {
                    continue;
                }
                if (c.getCategory().equals(ChallengeCategory.SKILLING) && ((SkillingChallenge) c).getSkill() != (int) information.getAdditionalInformation()[0]) {
                    continue;
                }
                challenge = c;
            }
        }
        return challenge;
    }

    public DailyChallenge getChallenge(final int index) {
        val set = challengeProgression.entrySet();
        val array = set.toArray();
        if (index >= array.length) {
            return null;
        }
        val entry = (Map.Entry<DailyChallenge, ChallengeProgress>) array[index];
        return entry.getKey();
    }

    public ChallengeProgress getProgress(final DailyChallenge challenge) {
        return challengeProgression.get(challenge);
    }

    public boolean claim(final DailyChallenge challenge) {
        val progress = challengeProgression.get(challenge);
        if (progress == null || !progress.isCompleted()) {
            return false;
        }

        val rewards = challenge.getRewards();
        var space = 0; //TODO kinda shit way improve in the future?
        for (val reward : rewards) {
            if (reward.getType().equals(RewardType.ITEM)) {
                space += 1;
            }
        }
        if (!player.getInventory().checkSpace(space)) {
            return false;
        }
        player.getInventory().addOrDrop(ItemId.SHERLOCK_NOTE, Utils.random(2,5));
        player.sendMessage("<col=ce8500><shad=000000>Challenge redeemed: " + challenge.getName());
        for (val reward : rewards) {
            reward.apply(player);
        }
        challengeProgression.remove(challenge);
        return true;
    }

    public void notifyUnclaimedChallenges() {
        var unclaimedChallenges = 0;
        for (val entry : challengeProgression.entrySet()) {
            val progress = entry.getValue();
            if (progress.isCompleted() && !progress.isClaimed()) {
                unclaimedChallenges++;
            }
        }
        if (unclaimedChallenges > 0) {
            player.sendMessage("<col=ce8500>You seem to have one or more completed challenges. Return to the Challenge Headmaster in Edgeville to claim your rewards!");
        }
    }

    private int getMaximumChallenges() {
        if (player.getMemberRank().eligibleTo(MemberRank.DRAGONSTONE_MEMBER)) {
            return 5;
        } else if (player.getMemberRank().eligibleTo(MemberRank.EMERALD_MEMBER)) {
            return 4;
        }
        return 3;
    }

}
