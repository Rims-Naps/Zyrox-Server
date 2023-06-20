package com.zenyte.plugins.renewednpc.christmas2022;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.events.BunnyD;
import com.zenyte.plugins.dialogue.events.SantaD;


/**
 * @author Kris | 25/11/2018 16:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class Santa2022 extends NPCPlugin {

    public static final Location[] DIG_SPOTS = {new Location(2969, 3961, 0), new Location(2748, 3733, 0), new Location( 2864, 3509, 0),
            new Location( 2872, 3934, 0), new Location(3285, 3372, 0)};

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new SantaD(player, npc)));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {15037};
    }
}
