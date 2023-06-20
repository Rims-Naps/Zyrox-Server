package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.events.BunnyD;


/**
 * @author Kris | 25/11/2018 16:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class EasterBunny2022 extends NPCPlugin {

    public static final Location[] DIG_SPOTS = {new Location(3363, 3418, 0), new Location(2674, 3241, 0), new Location( 1686, 3749, 0),
            new Location( 3074, 3518, 0), new Location(3285, 3372, 0)};

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new BunnyD(player, npc)));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {15177};
    }
}
