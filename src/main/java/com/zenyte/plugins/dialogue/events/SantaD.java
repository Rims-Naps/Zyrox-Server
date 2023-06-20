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
public final class SantaD extends Dialogue {

    public static final String CHRISTMAS2022_STARTED_DIALOGUE_ATTRIBUTE2 = "christmas2022_started";
    public static final String CHRISTMAS2022_COMPLETE_DIALOGUE_ATTRIBUTE2 = "christmas2022_complete";

    public SantaD(final Player player, final NPC npc) {
        super(player, npc.getId(), npc);
    }

    @Override
    public void buildDialogue() {
        {
            if(!player.getBooleanAttribute(CHRISTMAS2022_STARTED_DIALOGUE_ATTRIBUTE2)) {
                player("Hello Santa and Merry Christmas!");
                npc("There won't be anything merry about this Christmas..");
                npc("You see last year during the holiday convention..");
                npc("Easter Bunny and I decided to bury some presents....");
                npc("The problem is after 300 days and a.. ");
                npc("thousand ounces of milk I may of forgot where that was.. ");
                player("Here we go again..");
                player("Let me guess you need me to locate them?");
                npc("Great thinking!");
                npc("I mean who else could solve such a mystery?");
                player("You got that right.. ");
                player("Anyways did you leave yourself any clues?");
                npc("Yeah! They are written in my naughty or nice list!");
                npc("They are as followed:");
                npc("I buried one near a small pond surrounded by snow.");
                npc("I burned one south of something with 13 letters..");
                npc("Grr.. I can't make out what it was though...");
                npc("I buried one near someone I had to tell a story too..");
                npc("The final present was buried under a snowflake!");
                npc("All of them would be buried in the snow of course!");
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
                            player.getInventory().deleteItems(new Item(13346, 1), new Item(6542, 1), new Item(13656, 1), new Item(29025, 1));
                            if(player.getInventory().getFreeSlots() > 0) {
                                player.getInventory().addItem(29030, 1);
                            } else {
                                World.spawnFloorItem(new Item(29030, 1), player.getLocation(), player, 0, 200);
                            }
                            player.putBooleanAttribute(CHRISTMAS2022_COMPLETE_DIALOGUE_ATTRIBUTE2, true);
                            npc("Thank you for saving Christmas!.");
                        });
                    } else {
                        player("Not yet! I'll keep looking!");
                        npc("Remember..");
                        npc("They are as followed:");
                        npc("I buried one near a small pond surrounded by snow.");
                        npc("I burned one south of something with 13 letters..");
                        npc("Grr.. I can't make out what it was though...");
                        npc("I buried one near someone I had to tell a story too..");
                        npc("The final present was buried under a snowflake!");
                        npc("All of them would be buried in the snow of course!");
                        npc("Thanks again for your help!");
                        player("Of course, I " + player.getUsername() + ", always save the day!");
                    }
                }
            }
        }
    }

    private boolean hasAllEggs() {
        return player.getInventory().containsItems(new Item(13346, 1), new Item(6542, 1), new Item(13656, 1), new Item(29025, 1));
    }

}
