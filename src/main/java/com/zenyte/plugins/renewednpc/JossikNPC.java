package com.zenyte.plugins.renewednpc;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;

/**
 * @author Kris | 12/06/2019 08:49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class JossikNPC extends NPCPlugin {
    @Override
    public void handle() {
        bind("Rewards", (player, npc) -> GameInterface.JOSSIKS_SALVAGED_GODBOOKS.open(player));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                4423
        };
    }
}
