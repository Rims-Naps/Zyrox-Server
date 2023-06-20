package com.zenyte.plugins.dialogue.halloween2021D;

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
public final class GraveDiggerD extends Dialogue {

    public static final String GRAVEDIG_STARTED_DIALOGUE_ATTRIBUTE = "gravedig_started";
    public static final String GRAVEDIG_COMPLETE_DIALOGUE_ATTRIBUTE = "gravedig_complete";

    public GraveDiggerD(final Player player, final NPC npc) {
        super(player, npc.getId(), npc);
    }

    @Override
    public void buildDialogue() {
        {
            if(!player.getBooleanAttribute(GRAVEDIG_STARTED_DIALOGUE_ATTRIBUTE)) {
                player("Hello! Death told me to come talk to you.");
                npc("Did he? Well now that I mention it I do need some help with a certain task...");
                player("Let me guess... does it involve digging by any chance?");
                npc("Well uhhh... yeah. I misplaced some skulls in the wrong graves and I need to return them to their proper places.");
                npc("I'll take care of putting the skulls back, and you can keep anything you find while digging.");
                npc("Take this spade and search around the graves in this area.").executeAction(() -> {
                    if(player.getInventory().getFreeSlots() > 0) {
                        player.getInventory().addItem(new Item(ItemId.SPADE, 1));
                    } else {
                        World.spawnFloorItem(new Item(ItemId.SPADE, 1), player.getLocation(), player, 0, 200);
                    }
                    player.putBooleanAttribute(GRAVEDIG_STARTED_DIALOGUE_ATTRIBUTE, true);
                    npc("This ought to get you started!");
                });
            } else {
                if(player.getBooleanAttribute(GRAVEDIG_COMPLETE_DIALOGUE_ATTRIBUTE)) {
                    npc("Happy Halloween! Have you went and met back up with the Halloween Guide yet?");
                    player("I'll get right on that...");
                } else {
                    npc("Have you found all of the skulls yet?");
                    if(hasAllSkulls()) {
                        player("Yeah! I also managed to find a few pieces of an outfit along the way!");
                        npc("If I remember correctly, there were 3 in total scattered around..");
                        player("I sure hope I managed to get them all, it would be a shame if someone else snatched them up.");
                        npc("It might be a good idea to check all of the graves just in case!");
                        npc("Anyways, here's a little something extra for grabbing those skulls for me!").executeAction(() -> {
                            player.getInventory().deleteItems(new Item(24562, 1), new Item(24563, 1), new Item(24564, 1));
                            if(player.getInventory().getFreeSlots() > 0) {
                                player.getInventory().addItem(new Item(ItemId.COAL, 1));
                            } else {
                                World.spawnFloorItem(new Item(ItemId.COAL, 1), player.getLocation(), player, 0, 200);
                            }
                            player.putBooleanAttribute(GRAVEDIG_COMPLETE_DIALOGUE_ATTRIBUTE, true);
                            npc("On second thought... Sorry, I ran out of bonds to give out... someone came and took them all.");
                        });
                    } else {
                        player("Not yet! I'll keep looking!");
                        npc("Thanks again for your help!");
                        player("Of course, I " + player.getUsername() + ", always save the day!");
                    }
                }
            }
        }
    }

    private boolean hasAllSkulls() {
        return player.getInventory().containsItems(new Item(24562, 1), new Item(24563, 1), new Item(24564, 1));
    }

}
