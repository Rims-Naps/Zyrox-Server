package com.zenyte.plugins.dialogue.kourend;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

public class ShayzienSoldierD extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", ((player, npc) -> {
            player.getDialogueManager().start(new Dialogue(player, npc.getId(), npc) {
                @Override
                public void buildDialogue() {
                    {
                        npc("Hello. Do you fancy a fight? It's just a bit of fun, so I can practice combat. I'll give you a piece of my armour if you win.");
                        options("Do you want to fight?", new DialogueOption("Okay, I reckon I can take you.", key(100)), new DialogueOption("No thanks.", key(200)));
                    }
                    {
                        player(100, "Okay, I reckon I can take you.");
                        if(hasPreviousSet(npc.getId(), player)) {
                            npc("Bring it on then!").executeAction(() -> {
                                npc.setTransformation(npc.getId() + 1);
                                npc.setHitpoints(npc.getMaxHitpoints());
                                npc.getCombat().setTarget(player);
                                return;
                            });
                        } else {
                            npc("You should get the lower tier of armor first before trying to fight me.");
                            player("Yeah, I don't think I'm ready to fight you yet.");
                            return;
                        }

                    }
                    {
                        player(200, "No thanks.");
                        npc("Let me know if you change your mind. I need combat practice if I'm ever going to level up.");
                        return;
                    }
                }
            });
        }));
    }

    @Override
    public int[] getNPCs() {
        return new int[]{6904, 6906, 6908, 6910, 6912};
    }


    public boolean hasPreviousSet(int npcId, Player player) {
        int min = 13357, max = 13361;

        if(npcId == 6904 || npcId == 6905) {
            return true;
        } else {
            npcId -= 2;
        }
        switch(npcId) {
            case 6904:
            case 6905:
                min = 13357;
                max = 13361;
                break;
            case 6906:
            case 6907:
                min = 13362;
                max = 13366;
                break;
            case 6908:
            case 6909:
                min = 13367;
                max = 13371;
                break;
            case 6910:
            case 6911:
                min = 13372;
                max = 13376;
                break;
            case 6912:
            case 6913:
                min = 13377;
                max = 13381;
                break;
        }
        for(int i = min; i <= max; i++) {
            if(!player.containsItem(i)) {
                return false;
            }
        }
        return true;
    }
}
