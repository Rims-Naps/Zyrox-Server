package com.zenyte.game.content.event.easter2020.plugin.npc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.Expression;

/**
 * @author Corey
 * @since 12/04/2020
 */
public class ImplingWorker extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    npc("Gotta paint!", Expression.HIGH_REV_JOLLY);
                    player("I should probably let them on with their work.");
                }
            });
        });
    }
    
    @Override
    public int[] getNPCs() {
        return new int[]{
                15192,
                15191
        };
    }
}
