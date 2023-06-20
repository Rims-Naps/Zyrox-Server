package com.zenyte.game.content.theatreofblood.boss.verzikvitur.dialogue;

import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.VerzikRoom;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.Expression;

public class VerzikViturD extends Dialogue {

    public VerzikViturD(Player player) {
        super(player, NpcId.VERZIK_VITUR_8372);
    }

    @Override
    public void buildDialogue() {
        {
            npcWithId(NpcId.VERZIK_VITUR_8370, "Now that was quite the show! I haven't been that entertained in a long time!", Expression.VERZIK_OTHERWISE);
            npcWithId(NpcId.VERZIK_VITUR_8370,"Of course, you know I can't let you leave here alive.<br>Time for your final performance...", Expression.VERZIK_OTHERWISE);
            options("Is your party ready to fight?", "Yes, let's begin.", "No, don't start yet.")
                    .onOptionOne(() -> {
                        setKey(5);
                        if(VerSinhazaArea.getParty(player) != null) {
                            ((VerzikRoom)VerSinhazaArea.getParty(player).getRaid().getActiveRoom()).moveToDawnbringerPhase();
                        }
                        return;
                    })
                    .onOptionTwo(() -> {
                        setKey(10);
                       return;
                    });
            npc(5, "I'm going to enjoy this...", Expression.VERZIK_ENJOY);
            npc(10,"Don't waste my fucking time, kid.", Expression.VERZIK_OTHERWISE);
        }
    }
}
