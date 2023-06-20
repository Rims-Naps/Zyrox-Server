package com.zenyte.plugins.renewednpc;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.FarmerGricollerD;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class FarmerGricoller extends NPCPlugin {

    @Override
    public final void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new FarmerGricollerD(player, npc.getId(), false, false)));
        bind("Rewards", (player, npc) -> GameInterface.TITHE_FARM_REWARDS.open(player));
    }

    @Override
    public final int[] getNPCs() {
        return new int[] {NpcId.FARMER_GRICOLLER};
    }
}
