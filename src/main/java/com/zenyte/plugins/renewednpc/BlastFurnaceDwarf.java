package com.zenyte.plugins.renewednpc;

import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.pathfinding.events.player.EntityEvent;
import com.zenyte.game.world.entity.pathfinding.strategy.EntityStrategy;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.dialogue.impl.NPCChat;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class BlastFurnaceDwarf extends NPCPlugin {
    
    @Override
    public void handle() {
        bind("Talk-to", new OptionHandler() {
            @Override
            public void handle(final Player player, final NPC npc) {
                if (npc.getId() != 2923)
                    player.getDialogueManager().start(new NPCChat(player, npc.getId(), "*huff* Can't you see I'm busy here?"));
                else {
                    npc.faceEntity(player);
        
                    if (player.getSkills().getLevel(Skills.SMITHING) >= 60)
                        player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You are an experienced smith, you may use the furnace free of charge! " + Colour.RED.wrap("(No additional fee)")));
                    else
                        player.getDialogueManager().start(new NPCChat(player, npc.getId(), "Instead of paying 2500 coins every 10 minutes, the additional gold is added onto the amount taken from your coffer every tick."));
                }
            }
            
            @Override
            public void click(final Player player, final NPC npc, final NPCOption option) {
                player.setRouteEvent(new EntityEvent(player, new EntityStrategy(npc), () -> {
                    player.stopAll();
                    player.faceEntity(npc);
                    this.handle(player, npc);
                }, false));
            }
        });
    
        bind("Pay", new OptionHandler() {
            @Override
            public void handle(final Player player, final NPC npc) {
                npc.faceEntity(player);
            
                if (player.getSkills().getLevel(Skills.SMITHING) >= 60)
                    player.getDialogueManager().start(new NPCChat(player, npc.getId(), "You are an experienced smith, you may use the furnace free of charge! " + Colour.RED.wrap("(No additional fee)")));
                else
                    player.getDialogueManager().start(new NPCChat(player, npc.getId(), "Instead of paying 2500 coins every 10 minutes, the additional gold is added onto the amount taken from your coffer every tick."));
            }
        });
    }
    
    @Override
    public int[] getNPCs() {
        return new int[]{5454, 7384, 7385, 7386, 7387, 6602, 2923};
    }
}