package com.zenyte.game.content.kebos.npcs;

import com.zenyte.game.content.achievementdiary.DiaryReward;
import com.zenyte.game.content.achievementdiary.diaries.KourendDiary;
import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Tommeh | 16/11/2019 | 21:16
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class Tyss extends NPCPlugin {

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    npc("Greetings stranger. How can I help you?");
                    options(TITLE, "Change my spellbook", "Can I have an ash sanctifier, please?", "Nevermind")
                            .onOptionOne(() -> setKey(5))
                            .onOptionTwo(() -> setKey(10));
                    npc(5, "As you wish.").executeAction(() -> {
                        val spellbook = player.getCombatDefinitions().getSpellbook();
                        if (spellbook.equals(Spellbook.ARCEUUS)) {
                            player.getCombatDefinitions().setSpellbook(Spellbook.NORMAL, true);
                        } else {
                            player.getCombatDefinitions().setSpellbook(Spellbook.ARCEUUS, true);
                            player.getAchievementDiaries().update(KourendDiary.SWITCH_TO_NECROMANCY_SPELLBOOK);
                        }
                    });
                    player(10, "Can I have an ash sanctifier, please?").executeAction(() -> {
                        if (!DiaryReward.RADAS_BLESSING3.eligibleFor(player)) {
                            setKey(15);
                        } else if (!player.containsItem(ItemId.ASH_SANCTIFIER)) {
                            if (!player.getInventory().hasFreeSlots()) {
                                setKey(20);
                            } else {
                                setKey(25);
                            }
                        } else {
                            setKey(30);
                        }
                    });
                    npc(15, "No. You need to complete all of the hard tasks in your Kourend & Kebos Achievement Diary.");
                    npc(20, "Not unless you make space to carry it, traveller.");
                    npc(25, "Thanks for your efforts throughout the tasks set. Here, you may have this.").executeAction(() -> {
                        player.getInventory().addItem(ItemId.ASH_SANCTIFIER,1);
                    });
                    npc(30, "You already appear to own such a device, traveller.");
                }
            });
        });
        bind("Spellbook", (player, npc) -> {
            val spellbook = player.getCombatDefinitions().getSpellbook();
            if (spellbook.equals(Spellbook.ARCEUUS)) {
                player.getCombatDefinitions().setSpellbook(Spellbook.NORMAL, true);
            } else {
                player.getCombatDefinitions().setSpellbook(Spellbook.ARCEUUS, true);
                player.getAchievementDiaries().update(KourendDiary.SWITCH_TO_NECROMANCY_SPELLBOOK);
            }
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 7050 };
    }
}
