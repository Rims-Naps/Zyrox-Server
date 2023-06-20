package com.zenyte.plugins.dialogue.halloween2021D;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

public class HalloweenDayD extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
           player.getDialogueManager().start(new Dialogue(player, npc) {
               @Override
               public void buildDialogue() {
                   {
                       options("What can I help you with on this spooky day?", new DialogueOption("Happy Halloween!", key(100)), new DialogueOption("Nothing", key(200)));
                   }

                   {
                       player(100, "Happy Halloween!");
                       npc("Happy Halloween to you too! I have something for you...");
                       npc("Take this present from the Staff Team!").executeAction(() -> {
                           if(player.getInventory().getFreeSlots() > 0) {
                               player.getInventory().addItem(30089, 1);
                           } else {
                               World.spawnFloorItem(new Item(30089, 1), player.getLocation(), player, 0, 200);
                           }
                           player("Thank you!!");
                       });
                   }

                   {
                       player(200, "Unfortunately nothing!");
                       npc("Alright have a great Halloween!");
                       player("Thank you!");
                   }
               }
           });
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[]{13040};
    }
}
