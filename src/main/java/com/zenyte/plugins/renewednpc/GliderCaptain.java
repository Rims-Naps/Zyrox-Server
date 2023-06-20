package com.zenyte.plugins.renewednpc;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;

/**
 * @author Tommeh | 13-3-2019 | 18:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GliderCaptain extends NPCPlugin {

    @Override
    public void handle() {
        bind("Glider", (player, npc) -> GameInterface.GNOME_GLIDER.open(player));
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 7517, 6088, 6089, 6090, 6091, 6092, 7178 };
    }
}
