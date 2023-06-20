package com.zenyte.game.content.vote;

import com.zenyte.Constants;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import com.zenyte.plugins.dialogue.WiseOldManD;

import static com.zenyte.game.item.ItemId.BOOK_OF_BOOSTS;

public class BookOfBoosts extends ItemPlugin
{

    @Override
    public void handle()
    {
        bind("Check perks", (player, item, container, slotId) -> {
            if(!Constants.isOwner(player) && player.getNumericAttribute(WiseOldManD.BOOSTER_END).longValue() == 0)
            {
                player.sendMessage("You need to be a booster to use this menu.");
                return;
            }
            player.getDialogueManager().start(getDialogue(player));
        });
        bind("Remaining time", (player, item, container, slotId) -> {
            String initialMessage = "Your booster perks are currently expired. ";
            if(player.getNumericAttribute(WiseOldManD.BOOSTER_END).longValue() > System.currentTimeMillis())
            {
                long remainingMillis = player.getNumericAttribute(WiseOldManD.BOOSTER_END).longValue() - System.currentTimeMillis();
                int remainingDaysApprox = Math.round(remainingMillis / 86400000);
                if(remainingDaysApprox == 0)
                {
                    initialMessage = "You have less than 1 day of boosts remaining. ";
                } else
                {
                    initialMessage = "You have " + remainingDaysApprox + " days of boosts remaining. ";
                }
            }
            player.sendMessage(initialMessage +  "You have voted " + player.getNumericAttribute(WiseOldManD.BOOSTER_VOTES) + "/7 days needed for another boost.");
        });
    }

    public Dialogue getDialogue(Player player) {
        return new OptionsMenuD(player, "Current Boosts:", BoosterPerks.descriptionsToColoredArray(player))
        {
            @Override
            public void handleClick(int slotId)
            {
                if(slotId > BoosterPerks.values().length || slotId < 0)
                {
                    player.sendMessage("Not a valid option.");
                    return;
                }
                BoosterPerks.toggle(player, BoosterPerks.values()[slotId]);
            }

            @Override
            public boolean cancelOption() {
                return true;
            }
        };
    }


    @Override
    public int[] getItems()
    {
        return new int[] { BOOK_OF_BOOSTS };
    }
}
