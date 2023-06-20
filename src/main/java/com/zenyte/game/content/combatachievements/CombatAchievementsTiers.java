package com.zenyte.game.content.combatachievements;

import com.zenyte.game.content.combatachievements.combattasktiers.*;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.plugins.dialogue.OptionsMenuD;
import lombok.val;

/**
 * @author Cresinkel
 */

public enum CombatAchievementsTiers
{

    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3),
    ELITE("Elite", 4),
    MASTER("Master", 5),
    GRANDMASTER("Grandmaster", 6),
    TOTAL("Total", 7);


    private String description;
    private int tierId;

    CombatAchievementsTiers(String description, int tierId) {
        this.description = description;
        this.tierId = tierId;
    }

    public String getDescription()
    {
        return this.description;
    }

    public int getTierId()
    {
        return this.tierId;
    }

    public static String[] descriptionsToArray(Player player)
    {
        String[] descs = new String[CombatAchievementsTiers.values().length];
        for(int i = 0; i < CombatAchievementsTiers.values().length; i++)
        {
            val taskDoneCount = i == 0 ? EasyTasks.countEasyDone(player) : i == 1 ? MediumTasks.countMediumDone(player) : i == 2 ? HardTasks.countHardDone(player) : i == 3 ? EliteTasks.countEliteDone(player) : i == 4 ? MasterTasks.countMasterDone(player) : i == 5 ? GrandmasterTasks.countGrandmasterDone(player) : totalCount(player);
            val taskTotalCount = i == 0 ? EasyTasks.values().length : i == 1 ? MediumTasks.values().length : i == 2 ? HardTasks.values().length : i == 3 ? EliteTasks.values().length : i == 4 ? MasterTasks.values().length : i == 5 ? GrandmasterTasks.values().length : totalTasks();
            descs[i] = taskDoneCount == taskTotalCount ? Colour.RS_GREEN.wrap(CombatAchievementsTiers.values()[i].getDescription() + " (" + taskDoneCount + "/" + taskTotalCount + ")") : CombatAchievementsTiers.values()[i].getDescription() + " (" + taskDoneCount + "/" + taskTotalCount + ")";
        }
        return descs;
    }

    public static void pick(Player player, CombatAchievementsTiers tier) {
        player.getDialogueManager().start(getDialogue(player, tier));
    }

    public static Dialogue getDialogue(Player player, CombatAchievementsTiers tier) {
        String[][] taskDescr = {EasyTasks.descriptionsToColoredArray(player),
                MediumTasks.descriptionsToColoredArray(player),
                HardTasks.descriptionsToColoredArray(player),
                EliteTasks.descriptionsToColoredArray(player),
                MasterTasks.descriptionsToColoredArray(player),
                GrandmasterTasks.descriptionsToColoredArray(player)};
        val tierId = tier.tierId;
        val tasks = taskDescr[tierId-1];

        return new OptionsMenuD(player, tier.description + " Combat Achievements", tasks)
        {
            @Override
            public void handleClick(int slotId)
            {
                if(slotId > tasks.length || slotId < 0)
                {
                    player.sendMessage("Not a valid option.");
                    return;
                }
                val message = tierId == 1 ? EasyTasks.values()[slotId].getDescription() : tierId == 2 ? MediumTasks.values()[slotId].getDescription() : tierId == 3 ? HardTasks.values()[slotId].getDescription() : tierId == 4 ? EliteTasks.values()[slotId].getDescription() : tierId == 5 ? MasterTasks.values()[slotId].getDescription() : GrandmasterTasks.values()[slotId].getDescription();
                if (message.length() < 200) {
                    player.sendMessage(message);
                } else {
                    for (int index = 0; index < 20; index++) {
                        if (message.substring(0, 200 + index).endsWith(" ")) {
                            player.sendMessage(message.substring(0, 200 + index));
                            player.sendMessage(message.substring(200 + index));
                            break;
                        }
                    }
                }
                player.getDialogueManager().start(getDialogue(player, tier));
            }

            @Override
            public boolean cancelOption() {
                return true;
            }
        };
    }

    public static int totalCount(Player player){
        return EasyTasks.countEasyDone(player) + MediumTasks.countMediumDone(player) + HardTasks.countHardDone(player)
                + EliteTasks.countEliteDone(player) + MasterTasks.countMasterDone(player) + GrandmasterTasks.countGrandmasterDone(player);
    }

    public static int totalTasks(){
        return EasyTasks.values().length + MediumTasks.values().length + HardTasks.values().length
                + EliteTasks.values().length + MasterTasks.values().length + GrandmasterTasks.values().length;
    }
}
