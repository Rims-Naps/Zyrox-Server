package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;

/**
 * @author Matt
 */
public class StarShop extends NPCPlugin {
    @Override
    public void handle() {

        bind("Trade", (player, npc) -> player.openShop("Dusuri's Star Shop"));
    }
    @Override
    public int[] getNPCs() {
        return new int[] {13041};
    }
}
