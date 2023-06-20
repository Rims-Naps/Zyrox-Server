package com.zenyte.plugins.renewednpc.halloween2021npc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.halloween2021D.SkeletonHalloweenD;

/**
 * @author Kris | 25/11/2018 16:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SkeletonHalloweenNPC2021 extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new SkeletonHalloweenD(player, npc)));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {13018};
    }
}
