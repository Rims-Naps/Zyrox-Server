package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.ApprenticeFelixD;

/**
 * @author Cresinkel
 */
public class ApprenticeFelixShop extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new ApprenticeFelixD(player, npc));
        });
        bind("Trade", (player, npc) -> player.openShop("Apprentice Felix's Shop"));
    }
    @Override
    public int[] getNPCs() {
        return new int[] {10050};
    }
}
