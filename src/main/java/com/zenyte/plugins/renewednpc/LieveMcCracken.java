package com.zenyte.plugins.renewednpc;

import com.google.common.collect.ImmutableMap;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Christopher
 * @since 1/24/2020
 */
public class LieveMcCracken extends NPCPlugin {

    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                npc("*hic* G'day! Do ya...<br>... well, do ya need any help with yer trident? *buuurrrp*");
                options(TITLE,
                        new DialogueOption("Let's talk about tridents.", key(5)),
                        new DialogueOption("I'll be, heh, leaving you now...", key(10)));

                val tentacleAmount = player.getNumericAttribute("stored_kraken_tentacles").intValue();
                if (tentacleAmount > 0 && tentacleAmount < 10) {
                    npc(5, "If ya bring me a trident, I can enhance it for ya. It'll store 20,000 charges, not just 2,500. Ya brought me " + tentacleAmount + " tentacles so far, but I'll want 10 of 'em to do the job.");
                    npc("Just pass me " + (10 - tentacleAmount) + " more tentacles, then give me yer trident an' I'll get to work for ya.");
                } else if (tentacleAmount >= 10) {
                    npc(5, "Ya brought me " + tentacleAmount + " tentacles so far. Bring me a trident to enhance, an' I'll get to work for ya.");
                } else {
                    npc(5, "If ya bring me a trident, I can enhance it for ya. It'll store 20,000 charges, not just 2,500. But I wants payin' fer the job");
                    npc("Give me 10 kraken tentacles first. I'll hold onto 'em for ya. Then ya can bring me a trident to enhance, an' I'll get to work for ya.");
                }

                npc(10, "Wow, so original.");
            }
        }));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {7412};
    }
}
