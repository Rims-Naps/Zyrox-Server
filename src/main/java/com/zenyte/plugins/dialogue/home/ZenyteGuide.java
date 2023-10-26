package com.zenyte.plugins.dialogue.home;

import com.zenyte.game.ui.GameTab;
import com.zenyte.game.world.entity.npc.actions.NPCPlugin;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

public class ZenyteGuide extends NPCPlugin {
    @Override
    public void handle() {
        bind("Talk-to", (player, npc) -> {
           player.getDialogueManager().start(new Dialogue(player, npc) {
               @Override
               public void buildDialogue() {
                   {
                       options("Hello adventurer what can I help you with?", new DialogueOption("How can I start training?", key(100)), new DialogueOption("How do I start making money?", key(200)),
                               new DialogueOption("Can I donate to receive items?", key(300)), new DialogueOption("How can I keep updated with the server and know what is going on?", key(400)));
                   }

                   {
                       player(100, "How can I start training?");
                       npc("You will want to use the Pixies Portal located north of the Grand Exchange and click training....");
                       npc("This will bring you to a list of different places and monsters you can train on. ");
                       player("Thank you!");
                   }

                   {
                       player(200, "How do I start making money?");
                       npc("The best way to obtain starter cash is by voting you can do this by typing ::vote...");
                       npc("Once you have voted you will be rewarded with coins to start your adventure... ");
                       npc("You can vote every 12 hours! You can also make money by training thieving, or by...");
                       npc("Training slayer and selling off your loot at the Grand Exchange in the Home area...");
                       player("Thank you!");
                   }

                   {
                       player(300, "Can I donate to receive items?");
                       npc("Yes you can donate to receive items for a list of possible items go into the game noticeboard ...");
                       npc("scroll down to \"Useful links\" and click \"Store\"");
                       npc("You should also reach to to a admin+ in-game or on discord to see if there is any specials... ");

                       npc("I would also like to point out you are allowed to donate with OSGP..");
                       npc("Hopefully this helps and if you have any questions do the ::donation command! ");

                   }

                   {
                       player(400, "How can I keep updated with the server and know what is going on?");
                       npc("If you would like to stay up to date I would recommend joining the discord and the forums...");
                       npc("You can find the link to the forums and discord by going to the game noticeboard and..");
                       npc("scroll down to \"Useful links\" and click \"Forums\" or \"Discord\"... ");
                       npc("We have several channels on discord such as #update-snippets and #announcements... ");
                       npc("We also have forum sections for suggestions and feedback so feel free to take a look at those..");
                       npc("Hopefully this helps and if you have any questions feel free to ask a member of the Staff Team! ");
                   }
               }
           });
        });
    }

    @Override
    public int[] getNPCs() {
        return new int[]{13035}; //and we're done no i did some changes because I saw that you made the boxes $15/piece but it'd be much more worth just getting a bond to trade for them
    }
}
