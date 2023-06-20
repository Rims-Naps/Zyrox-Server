package com.zenyte.plugins.renewednpc;

import com.google.common.collect.ImmutableMap;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.broadcasts.BroadcastType;
import com.zenyte.game.world.broadcasts.WorldBroadcasts;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.plugins.dialogue.ArianwynD;
import com.zenyte.plugins.dialogue.varrock.LarxusD;

import java.util.Map;

/**
 * @author Cresinkel
 */

public class Arianwyn extends NPCPlugin {

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            player.getDialogueManager().start(new ArianwynD(player, npc));
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                5292
        };
    }
}
