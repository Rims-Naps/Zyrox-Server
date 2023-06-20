package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.FrejaD;
import com.zenyte.plugins.dialogue.halloween2021D.HalloweenGuideD;

/**
 * @author Matt
 */
public class Freja extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new FrejaD(player, npc)));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {8039};
    }
}
