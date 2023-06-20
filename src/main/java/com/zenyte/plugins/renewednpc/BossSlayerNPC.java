package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.skills.slayer.dialogue.SlayerMasterAssignmentD;
import com.zenyte.game.content.skills.slayer.dialogue.SlayerMasterD;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.halloween2021D.GraveDiggerD;

/**
 * @author Kris | 25/11/2018 16:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BossSlayerNPC extends NPCPlugin {


    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new SlayerMasterD(player, npc)));
        bind("Assignment", (player, npc) -> player.getDialogueManager().start(new SlayerMasterAssignmentD(player, npc)));
        bind("Trade", (player, npc) -> player.openShop("Slayer Equipment"));
        bind("Rewards", (player, npc) -> player.getSlayer().openInterface());
    }

    @Override
    public int[] getNPCs() {
        return new int[] {8038};
    }
}
