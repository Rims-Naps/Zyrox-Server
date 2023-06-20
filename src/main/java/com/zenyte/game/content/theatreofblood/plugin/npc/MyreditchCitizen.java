package com.zenyte.game.content.theatreofblood.plugin.npc;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.Expression;

public class MyreditchCitizen extends NPCPlugin {

    private final String[] DIALOGUE_CHOICES = {"Can't talk. I need to prepare myself.", "I guess this will be my last day of blood tithes, one way <br> or another.",
            "Soon I will be free!", "I will survive the Theatre! Just like Serafina did!", "I'm starting to think I made a mistake coming here.", "It can't be that hard can it?"};
    @Override
    public void handle() {
        this.bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc) {
               @Override
               public void buildDialogue() {
                    npc(DIALOGUE_CHOICES[Utils.random(0, DIALOGUE_CHOICES.length - 1)], Expression.ANXIOUS);
               }
           });
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[]{8328, 8329, 8330, 8331};
    }
}
