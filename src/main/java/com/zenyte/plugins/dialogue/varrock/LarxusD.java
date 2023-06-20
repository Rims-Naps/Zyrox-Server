package com.zenyte.plugins.dialogue.varrock;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Cresinkel
 */
public final class LarxusD extends Dialogue {

    public LarxusD(final Player player, final NPC npc) {
        super(player, npc);
    }

    @Override
    public void buildDialogue() {
        npc("Hello, you must be here to give me my lost scrolls!");
        player("Yes, take a look at these...");
    }

}
