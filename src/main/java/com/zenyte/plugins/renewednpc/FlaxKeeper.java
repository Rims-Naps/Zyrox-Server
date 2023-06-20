package com.zenyte.plugins.renewednpc;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.BertD;
import com.zenyte.plugins.dialogue.FlaxKeeperD;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Cresinkel
 */

public class FlaxKeeper extends NPCPlugin {

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new FlaxKeeperD(player, npc));
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                5522
        };
    }
}
