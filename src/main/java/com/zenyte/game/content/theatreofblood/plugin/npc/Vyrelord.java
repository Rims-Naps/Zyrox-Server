package com.zenyte.game.content.theatreofblood.plugin.npc;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.Expression;

public class Vyrelord extends NPCPlugin {
    private final String[] DIALOGUE_CHOICES = {"Get lost!", "I do enjoy a day out at the Theatre.", "Will you be entering the Theatre? It will be fun to <br> watch you die.",
                                                "Leave me alone food sack!"};
    @Override
    public void handle() {
        this.bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    npc(DIALOGUE_CHOICES[Utils.random(0, DIALOGUE_CHOICES.length - 1)], Expression.ANGRY);
                }
            });
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[]{8332, 8333, 8334, 8335};
    }
}
