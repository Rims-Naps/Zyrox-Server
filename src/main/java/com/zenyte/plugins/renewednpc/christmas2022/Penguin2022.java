package com.zenyte.plugins.renewednpc.christmas2022;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.events.ChristmasPenguinD;
import com.zenyte.plugins.dialogue.events.SantaD;


/**
 * @author Kris | 25/11/2018 16:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Penguin2022 extends NPCPlugin {

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new ChristmasPenguinD(player, npc)));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {13335};
    }
}
