package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;


public class WildernessMerchantShop extends NPCPlugin {
    @Override
    public void handle() {

        bind("Talk-to", (player, npc) ->player.getDialogueManager().start(new Dialogue(player, npc.getId()) {
            @Override
            public void buildDialogue() {
                npc("Thank you for stopping traveller! The wheel on my cart got stuck in a hole and broke...");
                npc("It looks like I will have to take care of business out here...");
                npc("Feel free to browse my wares but keep in my mind I am looking for very specific items!");
            }
        }));
        bind("Trade", (player, npc) -> player.openShop("Wilderness Merchant Shop"));
    }
    @Override
    public int[] getNPCs() {
        return new int[] {13044};
    }
}
