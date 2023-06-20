package com.zenyte.plugins.renewednpc.halloween2021npc;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.halloween2021D.GraveDiggerD;
import com.zenyte.plugins.dialogue.halloween2021D.SkeletonHalloweenD;

/**
 * @author Kris | 25/11/2018 16:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class GraveDiggerNPC2021 extends NPCPlugin {

    public static final Location[] DIG_SPOTS = {new Location(3242, 3194, 0), new Location(3242, 3196, 0), new Location( 3245, 3199, 0),
            new Location( 3247, 3199, 0), new Location(3240, 3195, 0), new Location(3241, 3199, 0)};

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new GraveDiggerD(player, npc)));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {8189};
    }
}
