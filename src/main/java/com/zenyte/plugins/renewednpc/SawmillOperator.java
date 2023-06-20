package com.zenyte.plugins.renewednpc;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.pathfinding.events.player.TileEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.dialogue.varrock.SawmillOperatorD;

/**
 * @author Kris | 25/11/2018 16:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SawmillOperator extends NPCPlugin {
    @Override
    public void handle() {

        bind("Talk-to", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.getDialogueManager().start(new SawmillOperatorD(player, npc));
            }
            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                player.setRouteEvent(new TileEvent(player,
                        new TileStrategy(npc.getLocation().transform(npc.getSpawnDirection(), 1), 0), () -> {
                    player.stopAll();
                    player.faceEntity(npc);
                    this.handle(player, npc);
                }));
            }
        });

        bind("Buy-plank", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                GameInterface.SAWMILL.open(player);
            }
            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                player.setRouteEvent(new TileEvent(player,
                        new TileStrategy(npc.getLocation().transform(npc.getSpawnDirection(), 1), 0), () -> {
                    player.stopAll();
                    player.faceEntity(npc);
                    this.handle(player, npc);
                }));
            }
        });
        bind("Trade", new OptionHandler() {
            @Override
            public void handle(Player player, NPC npc) {
                player.openShop("Construction supplies");
            }

            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                player.setRouteEvent(new TileEvent(player,
                        new TileStrategy(npc.getLocation().transform(npc.getSpawnDirection(), 1), 0), () -> {
                    player.stopAll();
                    player.faceEntity(npc);
                    this.handle(player, npc);
                }));
            }

        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                5422
        };
    }
}
