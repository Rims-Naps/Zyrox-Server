package com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc;

import com.zenyte.game.content.theatreofblood.boss.verzikvitur.dialogue.VerzikViturD;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;

public class VerzikNPC extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new VerzikViturD(player));
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {NpcId.VERZIK_VITUR_8369};
    }
}
