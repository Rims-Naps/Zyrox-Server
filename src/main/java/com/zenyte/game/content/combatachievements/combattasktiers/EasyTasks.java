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

public enum EasyTasks
{

    KILLCOUNT_1("Noxious Foe","Killcount: Kill an Aberrant Spectre", 1),
    KILLCOUNT_2("Barrows Novice", "Killcount: Open the Barrows chest 10 times", 2),
    KILLCOUNT_3("Big, Black and Fiery", "Killcount: Kill a Black Dragon", 3),
    KILLCOUNT_4("The Demonic Punching Bag", "Killcount: Kill a Bloodveld", 4),
    KILLCOUNT_5("Bryophyta Novice", "Killcount: Kill Bryophyta once", 5),
    KILLCOUNT_6("The Walking Volcano", "Killcount: Kill a Fire Giant", 6),
    KILLCOUNT_7("Giant Mole Novice", "Killcount: Kill the Giant Mole 10 times", 7),
    KILLCOUNT_8("A Greater Foe", "Killcount: Kill a Greater Demon", 8),
    KILLCOUNT_9("A Demon's Best Friend", "Killcount: Kill a Hellhound", 9),
    KILLCOUNT_10("King Black Dragon Novice", "Killcount: Kill the King Black Dragon 10 time", 10),
    KILLCOUNT_11("A Scaley Encounter", "Killcount: Kill a Lizardman Shaman", 11),
    KILLCOUNT_12("Obor Novice", "Killcount: Kill Obor once", 12),
    KILLCOUNT_13("Wintertodt Novice", "Killcount: Subdue the Wintertodt 5 times", 13),
    KILLCOUNT_14("A Slithery Encounter", "Killcount: Kill a Wyrm", 14),

    MECHANICAL_1("Protection from Moss", "Mechnical: Kill Bryophyta with the Protect from Magic prayer active", 15),
    MECHANICAL_2("Sleeping Giant", "Mechnical: Kill Obor whilst he is immobilized", 16),
    MECHANICAL_3("Handyman", "Mechnical: Repair a brazier which has been destroyed by the Wintertodt", 17),
    MECHANICAL_4("Mummy!", "Mechnical: Heal a pyromancer after they have fallen", 18),

    PERFECTION_1("Preparation Is Key", "Perfection: Kill Bryophyta without suffering any poison damage", 19),
    PERFECTION_2("Shayzien Protector", "Perfection: Kill a Lizardman Shaman whilst being protected from their poison attack", 20),

    RESTRICTION_1("Defence? What Defence?", "Restriction: Kill any Barrows Brother using magic attacks", 21),
    RESTRICTION_2("Fighting as Intended II", "Restriction: Kill Bryophyta with a rune scimitar", 22),
    RESTRICTION_3("A Slow Death", "Restriction: Kill Bryophyta with either poison or venom being the final source of damage", 23),
    RESTRICTION_4("Not So Great After All", "Restriction: Finish off a Greater Demon with a demonbane weapon", 24),
    RESTRICTION_5("Fighting as Intended", "Restriction: Kill Obor with the Fire blast spell", 25),
    RESTRICTION_6("Cosy", "Restriction: Subdue the Wintertodt with four pieces of warm equipment equipped", 26),

    ;
    private static final Int2ObjectMap<EasyTasks> tasksById = new Int2ObjectOpenHashMap<>();
    private static final ObjectList<EasyTasks> tasks = ObjectArrayList.wrap(values());

    private final String taskName;
    private final String description;
    private final int taskId;

    EasyTasks(String taskName, String description, int taskId) {
        this.description = description;
        this.taskId = taskId;
        this.taskName = taskName;
    }

    static {
        for (EasyTasks task : tasks) {
            tasksById.put(task.getTaskId(), task);
        }
    }

    public String getTaskName()
    {
        return this.taskName;
    }

    public String getDescription()
    {
        return this.description;
    }

    public int getTaskId()
    {
        return this.taskId;
    }

    public static String[] descriptionsToColoredArray(Player p)
    {
        String[] descs = new String[EasyTasks.values().length];
        for(int i = 0; i < EasyTasks.values().length; i++)
        {
            descs[i] = isDone(p, EasyTasks.values()[i]) ? "<str>" + EasyTasks.values()[i].getTaskName() : EasyTasks.values()[i].getTaskName();
        }
        return descs;
    }

    public static boolean isDone(Player player, EasyTasks task) {
        if (task.taskId == 1) {
            if (player.getNotificationSettings().getKillcount("aberrant spectre") >= 1){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 2) {
            if (player.getNotificationSettings().getKillcount("barrows") >= 10){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 3) {
            if (player.getNotificationSettings().getKillcount("black dragon") >= 1){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 4) {
            if (player.getNotificationSettings().getKillcount("bloodveld") >= 1){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 5) {
            if (player.getNotificationSettings().getKillcount("bryophyta") >= 1){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 6) {
            if (player.getNotificationSettings().getKillcount("fire giant") >= 1){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 7) {
            if (player.getNotificationSettings().getKillcount("giant mole") >= 10){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 8) {
            if (player.getNotificationSettings().getKillcount("greater demon") >= 1){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 9) {
            if (player.getNotificationSettings().getKillcount("hellhound") >= 1){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 10) {
            if (player.getNotificationSettings().getKillcount("king black dragon") >= 10){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 11) {
            if (player.getNotificationSettings().getKillcount("lizardman shaman") >= 1){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 12) {
            if (player.getNotificationSettings().getKillcount("obor") >= 1){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 13) {
            if (player.getNotificationSettings().getKillcount("Wintertodt") >= 5){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 14) {
            if (player.getNotificationSettings().getKillcount("wyrm") >= 1){
                if (!player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("easy-combat-achievement" + task.getTaskId(), true);
                    EasyTasks.sendEasyCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        return player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId());
    }

    public static int countEasyDone(Player player) {
        int count = 0;
        for (int id = 0; id < EasyTasks.values().length; id++) {
            val task = EasyTasks.values()[id];
            if (player.getBooleanAttribute("easy-combat-achievement" + task.getTaskId())) {
                count++;
            }
        }
        return count;
    }

    public static boolean allEasyCombatAchievementsDone(Player player) {
        val totalCount = EasyTasks.values().length;
        val playersCount = countEasyDone(player);
        return playersCount == totalCount;
    }

    public static void sendEasyCompletion(Player player, final int taskId) {
        val task = EasyTasks.tasksById.get(taskId);
        player.sendMessage("Congratulations, you've completed an easy combat task: " + Colour.RS_GREEN.wrap(task.taskName));
        player.getNotifications().addLast(new Notification("Combat Task Completed!", "Task Completed: " + "<col=ffffff>" + task.taskName + "</col>", 0xc9af97));
    }

}
