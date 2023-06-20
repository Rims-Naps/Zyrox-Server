package com.zenyte.plugins.dialogue.kourend;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

public class ShayzienInfiltrator extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", ((player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc.getId(), npc) {
                @Override
                public void buildDialogue() {
                    {
                        options("What would you like to ask?", new DialogueOption("Who are you?", key(100)),
                                new DialogueOption("Do you know how many lizardmen I've killed?", key(200)));
                    }
                    {
                        player(100, "Who are you?");
                        npc("Quiet! I'm here observing the lizardmen shamans, try not to blow my cover.");
                        player("You don't seem to be hiding very well.");
                        npc("As if you could do better!");
                        player("Riiiight, anyways, can I attack these lizardmen shaman?");
                        npc("Well I don't like the idea of you risking my cover. You are trusted by the Shayzien people though, so I suppose you can. Just be careful.");
                    }
                    {
                        player(200, "Do you know how many lizardmen I've killed?");
                        int lizCount = player.getSettings().getKillsLog().getOrDefault("lizardman", 0) & 0xFFFF;
                        int bruteCount = player.getSettings().getKillsLog().getOrDefault("lizardman brute", 0) & 0xFFFF;
                        int shamanCount = player.getSettings().getKillsLog().getOrDefault("lizardman shaman", 0) & 0xFFFF;
                        npc(String.format("The Shayzien records say you've killed %,d lizardmen since they began tracking your kills. %,d were lizardman shamans.", (lizCount + bruteCount + shamanCount), shamanCount));
                    }
                }
            });
        }));
    }

    @Override
    public int[] getNPCs() {
        return new int[]{8579};
    }
}
