package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.skills.slayer.dialogue.KrystiliaAssignmentD;
import com.zenyte.game.content.skills.slayer.dialogue.KrystiliaD;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;

/**
 * @author Kris | 26/11/2018 17:38
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Krystilia extends NPCPlugin {

    @Override
    public void handle() {
        bind("Rewards", (player, npc) -> player.getSlayer().openInterface());
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new KrystiliaD(player, npc)));
        bind("Assignment", (player, npc) -> player.getDialogueManager().start(new KrystiliaAssignmentD(player, npc)));
    }

    @Override
    public int[] getNPCs() {
        return new int[] { 7663 };
    }
}
