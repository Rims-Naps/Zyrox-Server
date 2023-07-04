package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;


/**
 * @author Cresinkel
 */
public class WizardCromperty extends NPCPlugin {

    private static final DiaryReward[] DIARY_REWARDS = {
            DiaryReward.ARDOUGNE_CLOAK4, DiaryReward.ARDOUGNE_CLOAK3,
            DiaryReward.ARDOUGNE_CLOAK2
    };

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            if (TreasureTrail.talk(player, npc)) {
                return;
            }
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    npc("Hello there. My name is Cromperty. I am a Wizard,<br>and an inventor.");
                    npc("You must be " + player.getName() + ". My good friend Sedridor has told me about you. As both wizard and inventor, he has aided me in my great invention!");
                    options(TITLE,"Can you teleport me to the Rune Essence?", "Claim free pure essence.")
                            .onOptionOne(() -> setKey(5))
                            .onOptionTwo(() -> {
                                if (!canClaim(player)) {
                                    player.sendMessage("You need to complete at least the medium Ardougne diaries to get free pure essence from Wizard Cromperty.");
                                    return;
                                }
                                if (player.getAttributes().containsKey("DAILY_PURE_ESSENCE")) {
                                    setKey(15);
                                } else {
                                    setKey(10);
                                }

                            });
                    player(5, "Can you teleport me to the Rune Essence?").executeAction(() -> Aubury.teleport(player, npc));
                    npc(10, "There you go, free pure essence. Return tomorrow for some more.").executeAction(() -> {
                        var maxAmount = 0;
                        for (int index = 0; index < DIARY_REWARDS.length; index++) {
                            val reward = DIARY_REWARDS[index];
                            val amount = index == 0 ? 250 : index == 1 ? 150 : 100;
                            if (reward.eligibleFor(player)) {
                                maxAmount = amount;
                                break;
                            }
                        }
                        val amt = maxAmount;
                        player.getInventory().addOrDrop(7937, amt);
                        player.getAttributes().put("DAILY_PURE_ESSENCE", 1);
                    });
                    npc(15, "I've already given you your free allowance for today. Come back to me tomorrow for some more.");
                }
            });
        });
        bind("Teleport", Aubury::teleport);
    }

    private boolean canClaim(final Player player) {
        for (val reward : DIARY_REWARDS) {
            if (reward.eligibleFor(player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 8480, 8481 };
    }
}
