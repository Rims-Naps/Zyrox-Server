package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;

public class Belona extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.openShop("Mining Guild Mineral Exchange"));
        bind("Trade", (player, npc) -> player.openShop("Mining Guild Mineral Exchange"));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {NpcId.BELONA};
    }
}
