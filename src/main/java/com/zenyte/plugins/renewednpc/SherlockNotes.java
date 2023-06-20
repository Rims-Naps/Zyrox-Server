package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;

/**
 * @author Matt
 */
public class SherlockNotes extends NPCPlugin {
    @Override
    public void handle() {

        bind("Trade", (player, npc) -> player.openShop("Sherlock's book shop"));
    }
    @Override
    public int[] getNPCs() {
        return new int[] {13061};
    }
}
