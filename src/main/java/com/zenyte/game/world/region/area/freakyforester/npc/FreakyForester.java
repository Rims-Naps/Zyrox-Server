package com.zenyte.game.world.region.area.freakyforester.npc;

import com.zenyte.game.item.ItemId;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.NpcId;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.dialogue.Expression;
import com.zenyte.game.world.region.area.freakyforester.FreakyForesterArea;

public class FreakyForester extends NPCPlugin
{

    public static final Dialogue getInitialDialogue(final Player player, final NPC freaky) {
        return new Dialogue(player, freaky) {
            @Override
            public void buildDialogue() {
                player("Whoa where am I?");
                npc("Where are you? Nevermind that, I'm in need of meat from a pheasant!");
                player("You want me to kill a penniless person?! You monster!", Expression.ANGRY);
                npc("No, not peasant.... pheasant! I need you to get me the meat of a pheasant with " + getTailAmountAsString(player) + ".");
                player("Oh... Okay. I'm totally against killing things but I guess that's not so bad.");
                npc("Yeah, suuuuuuuure you are.");
            }
        };
    }

    public static final Dialogue getStartedDialogue(final Player player, final NPC freaky) {
        return new Dialogue(player, freaky) {
            @Override
            public void buildDialogue() {
                npc("Did you find what I was looking for?");
                if(player.getInventory().containsItem(getNeededId(player)))
                {
                    player("I have the pheasant meat you wanted!");
                    if(hasMoreThanOnePheasant(player))
                    {
                        npc("Well done! But you seem to have some extras. I need you to get rid of them first so they don't contaminate the good meat I wanted.");
                        player("Aw jeez, Ri- I mean, uh... whatever, I guess.");
                        npc("Hurry! *burps* That other meat is starting to stink!");
                    } else
                    {
                        npc("Well done! I'll take that and you can get out of here using that portal! Sorry for keeping you against your will.").executeAction(() -> {
                            player.getAttributes().put("freaky forester complete", true);
                        });
                    }
                } else
                {
                    player("No, what was I looking for again?");
                    npc("I need you to get me the meat of a pheasant with " + getTailAmountAsString(player) + ".");
                    player("Ugh. Fine...");
                }
            }
        };
    }

    public static boolean hasMoreThanOnePheasant(Player player)
    {
        int numDifferent = 0;
        if(player.getInventory().containsItem(ItemId.RAW_PHEASANT))
        {
            numDifferent++;
        }
        if(player.getInventory().containsItem(ItemId.RAW_PHEASANT_6179))
        {
            numDifferent++;
        }
        if(player.getInventory().containsItem(ItemId.RAW_PHEASANT_11704))
        {
            numDifferent++;
        }
        if(player.getInventory().containsItem(ItemId.RAW_PHEASANT_28890))
        {
            numDifferent++;
        }
        return numDifferent > 1;
    }


    @Override
    public void handle()
    {
        bind("Talk-to", (player, npc) -> player.getDialogueManager().start(getStartedDialogue(player, npc)));
    }

    @Override
    public int[] getNPCs() {
        return new int[] {
                NpcId.FREAKY_FORESTER
        };
    }


    private static int getNeededId(Player player)
    {
        int tailCount = player.getNumericAttribute(FreakyForesterArea.ATTR_FF_TAIL_AMOUNT).intValue();
        switch(tailCount)
        {
            case 1:
                return ItemId.RAW_PHEASANT;
            case 2:
                return ItemId.RAW_PHEASANT_6179;
            case 3:
                return  ItemId.RAW_PHEASANT_11704;
            case 4:
                return  ItemId.RAW_PHEASANT_28890;
            default:
                return -1;
        }
    }


    public static String getTailAmountAsString(Player player)
    {
        int tailCount = player.getNumericAttribute(FreakyForesterArea.ATTR_FF_TAIL_AMOUNT).intValue();
        switch(tailCount)
        {
            case 1:
                return "one tail";
            case 2:
                return "two tails";
            case 3:
                return "three tails";
            case 4:
                return "four tails";
            default:
                return "no tails";
        }
    }

}
