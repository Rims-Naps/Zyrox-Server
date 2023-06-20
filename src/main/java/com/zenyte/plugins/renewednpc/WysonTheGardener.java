package com.zenyte.plugins.renewednpc;

import com.zenyte.game.content.treasuretrails.TreasureTrail;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Kris | 13/06/2019 18:07
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class WysonTheGardener extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
            if (TreasureTrail.talk(player, npc)) {
                return;
            }
            player.getDialogueManager().start(new Dialogue(player, npc) {
                @Override
                public void buildDialogue() {
                    val inv = player.getInventory();
                    val hasMolePieces = inv.containsItem(7416, 1) || inv.containsItem(7417, 1)
                            || inv.containsItem(7418, 1) || inv.containsItem(7419, 1);
                    if (!hasMolePieces) {
                        npc("Good morning.");
                        return;
                    }
                    npc("If I'm not mistaken, you've got some body parts from a big mole there! I'll trade it for bird nests if ye likes.");
                    options(TITLE, new DialogueOption("Okay, I will trade the mole parts.", key(10)),
                            new DialogueOption("Sorry, but I'm not interested.", key(20)));
                    player(10, "Okay, I will trade the mole parts.").executeAction(() -> {
                        //Cap the numbers to 10000 maximum to prevent lag from admins.
                        val claws = inv.deleteItem(7416, 10000).getSucceededAmount();
                        val notedClaws = inv.deleteItem(7417, 10000).getSucceededAmount();
                        val skins = inv.deleteItem(7418, 10000).getSucceededAmount();
                        val notedSkins = inv.deleteItem(7419, 10000).getSucceededAmount();
                        val total = claws + notedClaws + skins + notedSkins;
                        int seedCount = 0;
                        int ringCount = 0;
                        int emptyCount = 0;
                        for (int i = 0; i < total; i++) {
                            val random = Utils.random(99);
                            if (random < 70) {
                                seedCount++;
                            } else if (random < 95) {
                                ringCount++;
                            } else {
                                emptyCount++;
                            }
                        }
                        if (seedCount > 0) {
                            inv.addOrDrop(new Item(12793, 1, seedCount));
                        }
                        if (ringCount > 0) {
                            inv.addOrDrop(new Item(12794, 1, ringCount));
                        }
                        if (emptyCount > 0) {
                            inv.addOrDrop(new Item(12792, 1, emptyCount));
                        }
                    });

                    player(20, "Sorry, but I'm not interested.");
                }
            });
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                3253
        };
    }
}
