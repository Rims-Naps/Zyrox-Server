package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.sailing.Sailing;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Kris | 26/11/2018 19:53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CaptainBarnaby extends NPCPlugin {

    private static final Item COST = new Item(995, 30);


    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                npc("Do you want to go on a trip to Brimhaven?");
                npc("The trip will cost you 30 coins.");
                options(TITLE, "Yes please.", "No, thank you.").onOptionOne(() -> setKey(10));
                player(10, "Yes please!").executeAction(() -> {
                    if (player.getInventory().containsItem(COST)) {
                        player.sendMessage("You pay " + COST.getAmount() + " coins and board the ship.");
                        Sailing.sail(player, "Ardougne", "Brimhaven");
                    } else {
                        setKey(15);
                    }
                });
                npc(15, "You don't have enough gold on you to go on this trip.");
            }
        }));

        bind("Pay-fare", (player, npc) -> player.getDialogueManager().start(new Dialogue(player, npc) {
            @Override
            public void buildDialogue() {
                npc("The trip will cost you 30 coins.");
                options(TITLE, "Yes please.", "No, thank you.").onOptionOne(() -> setKey(10));
                player(10, "Yes please!").executeAction(() -> {
                    if (player.getInventory().containsItem(COST)) {
                        player.sendMessage("You pay " + COST.getAmount() + " coins and board the ship.");
                        Sailing.sail(player, "Ardougne", "Brimhaven");
                    } else {
                        setKey(15);
                    }
                });
                npc(15, "You don't have enough gold on you to go on this trip.");
            }
        }));
    }

    @Override
    public int[] getNPCs() {
        return new int[]{3649};
    }
}
