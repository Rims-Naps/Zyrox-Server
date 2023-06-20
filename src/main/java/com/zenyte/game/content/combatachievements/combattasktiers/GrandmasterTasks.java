package com.zenyte.game.content.combatachievements.combattasktiers;

import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Notification;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.val;

/**
 * @author Cresinkel
 */

public enum GrandmasterTasks
{
    KILLCOUNT_1("Chambers of Xeric Grandmaster", "Killcount: Complete the Chambers of Xeric 150 times", 1),
    KILLCOUNT_2("Chambers of Xeric: CM Grandmaster", "Killcount: Complete the Chambers of Xeric: Challenge Mode 25 times", 2),
    KILLCOUNT_3("Inferno Grandmaster", "Killcount: Complete the Inferno 5 times", 3),
    KILLCOUNT_4("Theatre of Blood Grandmaster", "Killcount: Complete the Theatre of Blood 150 times", 36),

    MECHANICAL_1("Denying the Healers II", "Mechnical: Complete the Fight Caves without TzTok-Jad being healed by a Yt-HurKot", 4),
    MECHANICAL_2("Playing with Jads", "Mechnical: Complete wave 68 of the Inferno within 30 seconds of the first JalTok-Jad dying", 5),
    MECHANICAL_3("The Floor Is Lava", "Mechnical: Kill Tzkal-Zuk without letting Jal-ImKot dig during any wave in the Inferno", 6),

    PERFECTION_1("Animal Whisperer", "Perfection: Kill Commander Zilyana in a privately rented instance without taking any damage from the boss or bodyguards", 7),
    PERFECTION_2("Keep Away", "Perfection: Kill General Graardor in a privately rented instance without taking any damage from the boss or bodyguards", 8),
    PERFECTION_3("Defence Matters", "Perfection:Kill General Graardor 2 times consecutively in a privately rented instance without taking any damage from his bodyguards", 9),
    PERFECTION_4("No Luck Required", "Perfection: Kill Tzkal-Zuk without being attacked by TzKal-Zuk and without taking damage from a JalTok-Jad", 10),
    PERFECTION_5("Demon Whisperer", "Perfection: Kill K'ril Tsutsaroth in a privately rented instance without ever being hit by his bodyguards", 11),
    PERFECTION_6("Perfect Theatre", "Perfection: Complete all of the following Combat Achievement tasks: \"Perfect Maiden\", \"Perfect Bloat\", \"Perfect Nylocas\", \"Perfect Sotetseg\", \"Perfect Xarpus\" and \"Perfect Verzik\"", 37),

    RESTRICTION_1("No Pressure", "Restriction: Kill the Alchemical Hydra using only Dharok's Greataxe as a weapon whilst having no more than 10 Hitpoints throughout the entire fight", 12),
    RESTRICTION_2("No Time for a Drink", "Restriction: Complete the Fight Caves without gaining any prayer points, ie. no potions or special attacks to heal prayer nor the use of the falador shield.", 13),
    //TOO HARD FOR ZENYTE's PLAYERBASE: RESTRICTION_3("Jad? What Are You Doing Here?", "Restriction: Kill Tzkal-Zuk without killing the JalTok-Jad which spawns during wave 69", 14),
    //TOO HARD DUE TO NO SCYTHE: RESTRICTION_4("Facing Jad Head-on II", "Restriction: Kill Tzkal-Zuk without equipping any range or mage weapons before wave 69", 15),
    RESTRICTION_5("Antifreeze", "Restriction: Kill Tzkal-Zuk without using any ancient ice spells during any wave in the Inferno", 16),
    RESTRICTION_6("Budget Setup", "Restriction: Kill Tzkal-Zuk without equipping a Twisted Bow within the Inferno", 17),
    RESTRICTION_7("Wasn't Even Close", "Restriction: Kill Tzkal-Zuk without letting your hitpoints fall below 10 during any wave in the Inferno", 18),
    RESTRICTION_8("The Worst Ranged Weapon", "Restriction: Kill Kree'arra by only dealing damage to him with a salamander", 19),
    RESTRICTION_9("The Fremennik Way", "Restriction: Kill Vorkath with only your fists", 20),
    RESTRICTION_10("Faithless Encounter", "Restriction: Kill Vorkath without losing any prayer points", 21),
    RESTRICTION_11("Morytania Only", "Restriction: Complete the Theatre of Blood without any member of the team equipping a non-barrows weapon (except Dawnbringer)", 38),

    SPEED_1("Alchemical Speed-Runner", "Speed: Kill the Alchemical Hydra in less than 1 minute 30 seconds", 22),
    SPEED_2("Chambers of Xeric (Trio) Speed-Runner", "Speed: Complete a Chambers of Xeric (Trio) in less than 16 minutes and 30 seconds", 23),
    SPEED_3("Chambers of Xeric (Solo) Speed-Runner", "Speed: Complete a Chambers of Xeric (Solo) in less than 19 minutes and 30 seconds", 24),
    SPEED_4("Chambers of Xeric: CM (Trio) Speed-Runner", "Speed: Complete a Chambers of Xeric: Challenge Mode (Trio) in less than 34 minutes", 25),
    SPEED_5("Chambers of Xeric: CM (Solo) Speed-Runner", "Speed: Complete a Chambers of Xeric: Challenge Mode (Solo) in less than 45 minutes", 26),
    SPEED_6("Fight Caves Speed-Runner", "Speed: Complete the Fight Caves in less than 20 minutes", 27),
    SPEED_7("Grotesque Guardians Speed-Runner", "Speed: Kill the Grotesque Guardians in less than 1 minute and 50 seconds", 28),
    SPEED_8("Inferno Speed-Runner", "Speed: Complete the Inferno in less than 70 minutes", 29),
    SPEED_9("Vorkath Speed-Runner", "Speed: Kill Vorkath in less than 1 minute", 30),
    SPEED_10("Zulrah Speed-Runner", "Speed: Kill Zulrah in less than 54 seconds", 31),
    SPEED_11("Theatre (Duo) Speed-Runner", "Speed: Complete the Theatre of Blood (Duo) in less than 26 minutes", 39),
    SPEED_12("Theatre (Trio) Speed-Runner", "Speed: Complete the Theatre of Blood (Trio) in less than 17 minutes and 30 seconds", 40),
    SPEED_13("Theatre (4-Scale) Speed-Runner", "Speed: Complete the Theatre of Blood (4-scale) in less than 15 minutes", 41),
    SPEED_14("Theatre (5-Scale) Speed-Runner", "Speed: Complete the Theatre of Blood (5-scale) in less than 14 minutes and 15 seconds", 42),

    STAMINA_1("Peach Conjurer", "Stamina: Kill Commander Zilyana 50 times in a privately rented instance without leaving the room", 32),
    STAMINA_2("Ourg Killer", "Stamina: Kill General Graardor 15 times in a privately rented instance without leaving the room", 33),
    STAMINA_3("Ash Collector", "Stamina: Kill K'ril Tsutsaroth 20 times in a privately rented instance without leaving the room", 34),
    STAMINA_4("Feather Hunter", "Stamina: Kill Kree'arra 30 times in a privately rented instance without leaving the room", 35),

    ;
    private static final Int2ObjectMap<GrandmasterTasks> tasksById = new Int2ObjectOpenHashMap<>();
    private static final ObjectList<GrandmasterTasks> tasks = ObjectArrayList.wrap(values());

    private final String taskName;
    private String description;
    private int taskId;

    GrandmasterTasks(String taskName, String description, int taskId) {
        this.description = description;
        this.taskId = taskId;
        this.taskName = taskName;
    }

    static {
        for (GrandmasterTasks task : tasks) {
            tasksById.put(task.getTaskId(), task);
        }
    }

    public String getTaskName()
    {
        return this.taskName;
    }

    public String getDescription() {return this.description;}

    public int getTaskId() {return this.taskId;}

    public static String[] descriptionsToColoredArray(Player p)
    {
        String[] descs = new String[GrandmasterTasks.values().length];
        for(int i = 0; i < GrandmasterTasks.values().length; i++)
        {
            descs[i] = isDone(p, GrandmasterTasks.values()[i]) ? "<str>" + GrandmasterTasks.values()[i].getTaskName() : GrandmasterTasks.values()[i].getTaskName();
        }
        return descs;
    }

    public static boolean isDone(Player player, GrandmasterTasks task) {
        if (task.taskId == 1) {
            if (player.getNumericAttribute("chambersofxeric").intValue() >= 150){
                if (!player.getBooleanAttribute("grandmaster-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("grandmaster-combat-achievement" + task.getTaskId(), true);
                    GrandmasterTasks.sendGrandmasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 2) {
            if (player.getNumericAttribute("challengechambersofxeric").intValue() >= 25){
                if (!player.getBooleanAttribute("grandmaster-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("grandmaster-combat-achievement" + task.getTaskId(), true);
                    GrandmasterTasks.sendGrandmasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 3) {
            if (player.getNotificationSettings().getKillcount("tzkal-zuk") >= 5){
                if (!player.getBooleanAttribute("grandmaster-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("grandmaster-combat-achievement" + task.getTaskId(), true);
                    GrandmasterTasks.sendGrandmasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 36) {
            if (player.getNumericAttribute("theatreofblood").intValue() >= 150){
                if (!player.getBooleanAttribute("grandmaster-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("grandmaster-combat-achievement" + task.getTaskId(), true);
                    GrandmasterTasks.sendGrandmasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        return player.getBooleanAttribute("grandmaster-combat-achievement" + task.getTaskId());
    }

    public static int countGrandmasterDone(Player player) {
        int count = 0;
        for (int id = 0; id < GrandmasterTasks.values().length; id++) {
            val task = GrandmasterTasks.values()[id];
            if (player.getBooleanAttribute("grandmaster-combat-achievement" + task.getTaskId())) {
                count++;
            }
        }
        return count;
    }

    public static boolean allGrandmasterCombatAchievementsDone(Player player) {
        val totalCount = GrandmasterTasks.values().length;
        val playersCount = countGrandmasterDone(player);
        return playersCount == totalCount;
    }

    public static void sendGrandmasterCompletion(Player player, final int taskId) {
        val task = GrandmasterTasks.tasksById.get(taskId);
        player.sendMessage("Congratulations, you've completed a grandmaster combat task: " + Colour.RS_GREEN.wrap(task.taskName));
        player.getNotifications().addLast(new Notification("Combat Task Completed!", "Task Completed: " + "<col=ffffff>" + task.taskName + "</col>", 0xc9af97));
    }


}