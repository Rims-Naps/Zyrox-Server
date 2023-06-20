package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.WiseOldManD;

/**
 * @author Kris | 25/11/2018 16:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class WiseOldMan extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new WiseOldManD(player, npc)));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {2108};
    }
}
