package com.zenyte.game.content.combatachievements;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.OptionsMenuD;

/**
 * @author Cresinkel
 */

public class CombatAchievementsBook extends ItemPlugin
{

    @Override
    public void handle()
    {
        bind("Check tasks", (player, item, container, slotId) -> {
            player.getDialogueManager().start(getDialogue(player));
        });
    }

    public Dialogue getDialogue(Player player) {
        return new OptionsMenuD(player, "Choose a task tier:", CombatAchievementsTiers.descriptionsToArray(player))
        {
            @Override
            public void handleClick(int slotId)
            {
                if(slotId > CombatAchievementsTiers.values().length || slotId < 0)
                {
                    player.sendMessage("Not a valid option.");
                    return;
                }
                if (slotId < 6) {
                    CombatAchievementsTiers.pick(player, CombatAchievementsTiers.values()[slotId]);
                } else {
                    player.getDialogueManager().start(getDialogue(player));
                    if (CombatAchievementsTiers.totalCount(player) == CombatAchievementsTiers.totalTasks()) {
                        player.sendMessage("You have completed all the combat achievements!");
                    } else {
                        player.sendMessage("You have completed " + CombatAchievementsTiers.totalCount(player) + " out of the " + CombatAchievementsTiers.totalTasks() + " total tasks.");
                    }
                }
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
        return new int[] { 32239 };
    }
}
