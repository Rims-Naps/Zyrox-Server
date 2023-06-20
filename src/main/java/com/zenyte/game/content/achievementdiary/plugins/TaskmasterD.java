package com.zenyte.game.content.achievementdiary.plugins;

import com.zenyte.game.content.achievementdiary.AchievementDiaries;
import com.zenyte.game.content.achievementdiary.Diary;
import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tommeh | 6-11-2018 | 16:45
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class TaskmasterD extends Dialogue {

    private Diary diary;
    private DiaryReward reward, previousReward, pendingReward;

    public TaskmasterD(final Player player, final NPC npc) {
        super(player, npc);
        diary = AchievementDiaries.getDiaryByTaskmaster(npcId);
        if (diary == null) {
            return;
        }
        val pendingRewards = player.getAchievementDiaries().getPendingRewards();
        if (!pendingRewards.get(diary.area()).isEmpty()) {
            val unclaimedRewards = pendingRewards.get(diary.area()).entrySet().stream().filter(bool -> bool.getValue().equals(false)).map(Map.Entry::getKey).collect(Collectors.toList());
            if (!unclaimedRewards.isEmpty()) {
                pendingReward = DiaryReward.getWorstReward(unclaimedRewards);
                previousReward = DiaryReward.getPreviousReward(pendingReward);
            }
        }
        reward = DiaryReward.getBestEligibleReward(player, diary.area());
    }

    @Override
    public void buildDialogue() {
        if (pendingReward != null) {
            player("I've completed all of the " + pendingReward.getComplexity().toString().toLowerCase() + " tasks in my " + pendingReward.getArea().getAreaName() + " Achievement Diary!");
            npc("I can see that, well done! You'll be wanting your reward then!");
            player("Yes please!").executeAction(() -> {
                if (!player.getInventory().checkSpace(2)) {
                    setKey(20);
                } else {
                    setKey(5);
                    if (previousReward != null) {
                        player.removeItem(previousReward.getItem());
                    }
                    player.getInventory().addItem(pendingReward.getItem());
                    val lampItem = new Item(pendingReward.getLamp().getItem());
                    if (pendingReward == DiaryReward.KARAMJA_GLOVES1 || pendingReward == DiaryReward.KARAMJA_GLOVES2 || pendingReward == DiaryReward.KARAMJA_GLOVES3) {
                        lampItem.setCharges(1);
                    }
                    player.getInventory().addItem(lampItem);
                    player.getAchievementDiaries().getPendingRewards().get(diary.area()).put(pendingReward, true);
                }
            });
            doubleItem(5, pendingReward.getItem(), pendingReward.getLamp().getItem(), "The taskmaster gives you your rewards.");
            player("Wow thanks!");
            npc("If you ever lose your " + pendingReward.getItemName() + ", come back to me to reclaim it.");
            npc(20, "I'm afraid you don't have enough inventory space to claim your rewards.");
            return;
        }
        if (reward == null) {
            npc("Hello there.");
            options(TITLE, "Who are you?", "Bye!")
                    .onOptionOne(() -> setKey(5))
                    .onOptionTwo(() -> setKey(20));
            npc(5, "They call me " + npc.getName(player) + "... I'm the taskmaster for the " + diary.area().getAreaName() + " Achievement Diary.");
            player("What is the Achievement Diary?");
            npc("It's a diary that helps you keep track of particular achievements. In and around " + diary.area().getAreaName() + " it can help you discover some quite useful things. Eventually, with enough exploration, the inhabitants will reward you.");
            player(20, "Bye!");
            npc("See you later!");
        } else if (npc.getId() == 5514) { //lesser fanatic
            npc("Hello there.");
            options(TITLE, "Can I have a previous reward?", "Can I have another " + reward.getItemName() + "?", "Who are you?", "Can you toggle my noted lava dragon bones?", "Bye!")
                    .onOptionOne(() -> setKey(5))
                    .onOptionTwo(() -> setKey(10))
                    .onOptionThree(() -> setKey(20))
                    .onOptionFour(() -> {
                        if (DiaryReward.WILDERNESS_SWORD4.eligibleFor(player)) {
                            if (player.getAttributes().containsKey("NOTED_LAVA_DRAGON_BONES")) {
                                setKey(50);
                            } else {
                                setKey(60);
                            }
                        } else {
                            setKey(70);
                        }
                    })
                    .onOptionFive(() -> setKey(25));
            player(5, "Can I have a previous reward?");
            final List<DiaryReward> rewards = DiaryReward.get(diary.area());
            options("Which tier reward do you want?", "Easy", "Medium", "Hard", "Elite")
                    .onOptionOne(() -> giveReward(player, rewards.get(0)))
                    .onOptionTwo(() -> giveReward(player, rewards.get(1)))
                    .onOptionThree(() -> giveReward(player, rewards.get(2)))
                    .onOptionFour(() -> giveReward(player, rewards.get(3)));

            player(10, "Can I have another " + reward.getItemName() + "?").executeAction(() -> {
                if (player.getInventory().checkSpace()) {
                    player.getInventory().addItem(reward.getItem());
                    setKey(30);
                } else {
                    setKey(40);
                }
            });

            npc(20, "They call me " + npc.getName(player) + "... I'm the taskmaster for the " + diary.area().getAreaName() + " Achievement Diary.");
            player("What is the Achievement Diary?");
            npc("It's a diary that helps you keep track of particular achievements. In and around " + diary.area().getAreaName() + " it can help you discover some quite useful things. Eventually, with enough exploration, the inhabitants will reward you.");

            player(25, "Bye!");
            npc("See you later!");
            item(30, reward.getItem(),  npc.getName(player) + " gives you another " + reward.getItemName() + ".");
            npc(40, "I'm afraid you don't have enough space in your inventory.");
            npc(50, "I sure can, your lava dragon bones drops will now be unnoted.").executeAction(() -> {
                player.getAttributes().remove("NOTED_LAVA_DRAGON_BONES");
            });
            npc(60, "I sure can, your lava dragon bones drops will now be noted.").executeAction(() -> {
                player.getAttributes().put("NOTED_LAVA_DRAGON_BONES", 1);
            });
            npc(70, "I can only do this if you have the wilderness elite diaries completed, Bye.");
        } else {
            npc("Hello there.");
            options(TITLE, "Can I have a previous reward?", "Can I have another " + reward.getItemName() + "?", "Who are you?", "Bye!")
                    .onOptionOne(() -> setKey(5))
                    .onOptionTwo(() -> setKey(10))
                    .onOptionThree(() -> setKey(20))
                    .onOptionFour(() -> setKey(25));
            player(5, "Can I have a previous reward?");
            final List<DiaryReward> rewards = DiaryReward.get(diary.area());
            options("Which tier reward do you want?", "Easy", "Medium", "Hard", "Elite")
                    .onOptionOne(() -> giveReward(player, rewards.get(0)))
                    .onOptionTwo(() -> giveReward(player, rewards.get(1)))
                    .onOptionThree(() -> giveReward(player, rewards.get(2)))
                    .onOptionFour(() -> giveReward(player, rewards.get(3)));

            player(10, "Can I have another " + reward.getItemName() + "?").executeAction(() -> {
                if (player.getInventory().checkSpace()) {
                    player.getInventory().addItem(reward.getItem());
                    setKey(30);
                } else {
                    setKey(40);
                }
            });

            npc(20, "They call me " + npc.getName(player) + "... I'm the taskmaster for the " + diary.area().getAreaName() + " Achievement Diary.");
            player("What is the Achievement Diary?");
            npc("It's a diary that helps you keep track of particular achievements. In and around " + diary.area().getAreaName() + " it can help you discover some quite useful things. Eventually, with enough exploration, the inhabitants will reward you.");

            player(25, "Bye!");
            npc("See you later!");
            item(30, reward.getItem(),  npc.getName(player) + " gives you another " + reward.getItemName() + ".");
            npc(40, "I'm afraid you don't have enough space in your inventory.");
        }
    }

    public void giveReward(Player player, DiaryReward reward)
    {
        player.getDialogueManager().finish();
        if(reward.eligibleFor(player))
        {
            if(player.getInventory().checkSpace())
            {
                player.getDialogueManager().start(new Dialogue(player)
                {
                    @Override
                    public void buildDialogue()
                    {
                        plain("You receive another " + reward.getItemName()).executeAction(() -> {
                            player.getInventory().addItem(reward.getItem());
                        });
                    }
                });
            }
        }
        else
        {
            player.getDialogueManager().start(new Dialogue(player)
            {
                @Override
                public void buildDialogue()
                {
                    plain("You are not eligible for this reward!");
                }
            });
        }
    }

}
