package com.zenyte.plugins.dialogue.events;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Kris | 2. nov 2017 : 23:24.58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class BunnyD extends Dialogue {

    public static final String CHRISTMAS2022_STARTED_DIALOGUE_ATTRIBUTE2 = "christmas2022_started";
    public static final String CHRISTMAS2022_COMPLETE_DIALOGUE_ATTRIBUTE2 = "christmas2022_complete";

    public BunnyD(final Player player, final NPC npc) {
        super(player, npc.getId(), npc);
    }

    @Override
    public void buildDialogue() {
        {
            if(!player.getBooleanAttribute(CHRISTMAS2022_STARTED_DIALOGUE_ATTRIBUTE2)) {
                player("Hello Santa and Merry Christmas!");
                npc("There won't be anything merry about this Christmas..");
                npc("You see last year during the holiday convention Easter Bunny and I decided to bury some presents..");
                npc("The problem is after 300 days and a thousand ounces of milk I may of forgot where that was.. ");
                player("Here we go again.. Let me guess you need me to locate them?");
                npc("Great thinking!");
                npc("I mean who else could solve such a mystery?");
                player("You got that right.. Anyways did you leave yourself any clues? ");
                npc("Yeah! They are written in my naughty or nice list!");
                npc("Clue 1: I buried it near a small pond surrounded by snow.");
                npc("Clue 2: The next sentence its buried south of something with 13 letters but I can't make out what.");
                npc("Clue 3: I don't remember much about the location but I had to tell him a story before I could dig.");

                npc("Clue 5: The final present was buried under a snowflake!");
                npc("Take this spade and best of luck.").executeAction(() -> {
                    if(player.getInventory().getFreeSlots() > 0) {
                        player.getInventory().addItem(new Item(ItemId.SPADE, 1));
                    } else {
                        World.spawnFloorItem(new Item(ItemId.SPADE, 1), player.getLocation(), player, 0, 200);
                    }
                    player.putBooleanAttribute(CHRISTMAS2022_STARTED_DIALOGUE_ATTRIBUTE2, true);
                    npc("This ought to get you started!");
                });
            } else {
                if(player.getBooleanAttribute(CHRISTMAS2022_COMPLETE_DIALOGUE_ATTRIBUTE2)) {
                    npc("Thank you for all you have done for us! You saved Christmas!");
                    player("No problem!");
                } else {
                    npc("Have you found all th presents yet??");
                    if(hasAllEggs()) {
                        player("Yeah!");
                        npc("Perfect hand them over and I will give you a reward!!").executeAction(() -> {
                            player.getInventory().deleteItems(new Item(7928, 1), new Item(7929, 1), new Item(7931, 1), new Item(7932, 1), new Item(7930, 1));
                            if(player.getInventory().getFreeSlots() > 0) {
                                player.getInventory().addItem(new Item(ItemId.EASTER_CARROT, 1));
                            } else {
                                World.spawnFloorItem(new Item(ItemId.COAL, 1), player.getLocation(), player, 0, 200);
                            }
                            player.putBooleanAttribute(CHRISTMAS2022_COMPLETE_DIALOGUE_ATTRIBUTE2, true);
                            npc("Thank you for saving Christmas!.");
                        });
                    } else {
                        player("Not yet! I'll keep looking!");
                        npc("Remember..");
                        npc("Clue 1: I buried it near a small pond surrounded by snow.");
                        npc("Clue 2: The next sentence its buried south of something with 13 letters but I can't make out what.");
                        npc("Clue 3: I don't remember much about the location but I had to tell him a story before I could dig.");

                        npc("Clue 5: The final present was buried under a snowflake!");
                        npc("Thanks again for your help!");
                        player("Of course, I " + player.getUsername() + ", always save the day!");
                    }
                }
            }
        }
    }

    private boolean hasAllEggs() {
        return player.getInventory().containsItems(new Item(7928, 1), new Item(7929, 1), new Item(7930, 1), new Item(7931, 1), new Item(7932, 1));
    }

}
