package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.pathfinding.events.RouteEvent;
import com.zenyte.game.world.entity.pathfinding.events.player.EntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.FaladorBarD;

/**
 * @author Cresinkel
 */

public class FaladorBar extends NPCPlugin {


    @Override
    public void handle() {
        bind("Talk-to", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.getDialogueManager().start(new FaladorBarD(player, npc));
            }

            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                player.setRouteEvent(new EntityEvent(player, new EntityStrategy(npc, npc.getId() == 1315 ? (npc.getY() == 3373 ? 2 : 1) : 0, npc.getId() == 1315 ? RouteEvent.EAST_EXIT : 0), () -> execute(player, npc), false));
            }
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                NpcId.EMILY, NpcId.KAYLEE
        };
    }

}
