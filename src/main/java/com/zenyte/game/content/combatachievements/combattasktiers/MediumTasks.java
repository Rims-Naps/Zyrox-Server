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

public enum MediumTasks
{

    KILLCOUNT_1("Barrows Champion", "Killcount: Open the Barrows chest 25 times", 1),
    KILLCOUNT_2("Brutal, Big, Black and Firey", "Killcount: Kill a Brutal Black Dragon", 2),
    KILLCOUNT_3("Bryophyta Champion", "Killcount: Kill Bryophyta 5 times", 3),
    KILLCOUNT_4("Chaos Fanatic Champion", "Killcount: Kill the Chaos Fanatic 10 times", 4),
    KILLCOUNT_5("Crazy Archaeologist Champion", "Killcount: Kill the Crazy Archaeologist 10 times", 5),
    KILLCOUNT_6("Dagannoth Rex Champion", "Killcount: Kill Dagannoth Rex 10 times", 6),
    KILLCOUNT_7("Dagannoth Supreme Champion", "Killcount: Kill Dagannoth Supreme 10 times", 7),
    KILLCOUNT_8("Dagannoth Prime Champion", "Killcount: Kill Dagannoth Prime 10 times", 8),
    KILLCOUNT_9("A Smashing Time", "Killcount: Kill a Gargoyle", 9),
    KILLCOUNT_10("Giant Mole Champion", "Killcount: Kill the Giant mole 25 times", 10),
    KILLCOUNT_11("King Black Dragon Champion", "Killcount: Kill the King Black Dragon 25 times", 11),
    KILLCOUNT_12("Master of Broad Weaponry", "Killcount: Kill a Kurask", 12),
    KILLCOUNT_13("Obor Champion", "Killcount: Kill Obor 5 times", 13),
    KILLCOUNT_14("A Frozen Foe from the Past", "Killcount: Kill a Skeletal Wyvern", 14),
    KILLCOUNT_15("Skotizo Champion", "Killcount: Kill Skotizo once", 15),
    KILLCOUNT_16("Wintertodt Champion", "Killcount: Subdue the Wintertodt 10 times", 16),

    MECHANICAL_1("Can't Touch Me", "Mechnical: Kill Dharok, Verac, Torag and Guthan without letting them attack you with melee and loot the chest", 17),
    MECHANICAL_2("Quick Cutter", "Mechnical: Kill all 3 of Bryophyta's growthlings within 3 seconds of the first one dying", 18),
    MECHANICAL_3("Mage of the Ruins", "Mechnical: Kill the Crazy Archaeologist with only magical attacks", 19),
    MECHANICAL_4("A Frozen King", "Mechnical: Kill Dagannoth Rex whilst he is immoblized", 20),
    MECHANICAL_5("Claw Clipper", "Mechnical: Kill the King Black Dragon with the Protect from Melee prayer activated", 21),
    MECHANICAL_6("Back to the Wall", "Mechnical: Kill Obor without being pushed back more than one square by his knockback attack", 22),
    MECHANICAL_7("Demonic Weakening", "Mechnical: Kill Skotizo with no altars active", 23),

    PERFECTION_1("Pray for Success", "Perfection: Kill all six Barrows Brothers and loot the Barrows chest without taking any damage from any of the brothers", 24),
    PERFECTION_2("Sorry, What Was That?", "Perfection: Kill the Chaos Fanatic without being hit by his explosion attack", 25),
    PERFECTION_3("I'd Rather Not Learn", "Perfection: Kill the Crazy Archaeologist without anyone being hit by his Rain of Knowledge attack", 26),
    PERFECTION_4("Avoiding Those Little Arms", "Perfection: Kill the Giant Mole without her damaging you", 27),
    PERFECTION_5("Squashing the Giant", "Perfection: Kill Obor without taking any damage off prayer", 28),
    PERFECTION_6("Can We Fix It?", "Perfection: Subdue the Wintertodt without allowing all 4 braziers to be broken at the same time", 29),

    RESTRICTION_1("Antifire Protection", "Restriction: Kill the King Black Dragon with an antifire potion active and an antidragon shield equipped", 30),
    RESTRICTION_2("Hide Penetration", "Restriction: Kill the King Black Dragon with a stab weapon", 31),
    RESTRICTION_3("Demonbane Weaponry", "Restriction: Kill Skotizo with a demonbane weapon equipped", 32),
    RESTRICTION_4("Leaving No One Behind", "Restriction: Subdue the Wintertodt without any of the Pyromancers falling", 33),
    ;
    private static final Int2ObjectMap<MediumTasks> tasksById = new Int2ObjectOpenHashMap<>();
    private static final ObjectList<MediumTasks> tasks = ObjectArrayList.wrap(values());

    private final String taskName;
    private String description;
    private int taskId;

    MediumTasks(String taskName, String description, int taskId) {
        this.description = description;
        this.taskId = taskId;
        this.taskName = taskName;
    }

    static {
        for (MediumTasks task : tasks) {
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
        String[] descs = new String[MediumTasks.values().length];
        for(int i = 0; i < MediumTasks.values().length; i++)
        {
            descs[i] = isDone(p, MediumTasks.values()[i]) ? "<str>" + MediumTasks.values()[i].getTaskName() : MediumTasks.values()[i].getTaskName();
        }
        return descs;
    }

    public static boolean isDone(Player player, MediumTasks task) {
        if (task.taskId == 1) {
            if (player.getNotificationSettings().getKillcount("barrows") >= 25){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 2) {
            if (player.getNotificationSettings().getKillcount("brutal black dragon") >= 1) {
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 3) {
            if (player.getNotificationSettings().getKillcount("bryophyta") >= 5){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 4) {
            if (player.getNotificationSettings().getKillcount("chaos fanatic") >= 10){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 5) {
            if (player.getNotificationSettings().getKillcount("crazy archaeologist") >= 10){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 6) {
            if (player.getNotificationSettings().getKillcount("dagannoth rex") >= 10){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 7) {
            if (player.getNotificationSettings().getKillcount("dagannoth supreme") >= 10){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 8) {
            if (player.getNotificationSettings().getKillcount("dagannoth prime") >= 10){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 9) {
            if (player.getNotificationSettings().getKillcount("gargoyle") >= 1){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 10) {
            if (player.getNotificationSettings().getKillcount("giant mole") >= 25){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 11) {
            if (player.getNotificationSettings().getKillcount("king black dragon") >= 25){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 12) {
            if (player.getNotificationSettings().getKillcount("kurask") >= 1){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 13) {
            if (player.getNotificationSettings().getKillcount("obor") >= 5){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 14) {
            if (player.getNotificationSettings().getKillcount("skeletal wyvern") >= 1){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 15) {
            if (player.getNotificationSettings().getKillcount("skotizo") >= 1){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 16) {
            if (player.getNotificationSettings().getKillcount("Wintertodt") >= 10){
                if (!player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("medium-combat-achievement" + task.getTaskId(), true);
                    MediumTasks.sendMediumCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        return player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId());
    }

    public static int countMediumDone(Player player) {
        int count = 0;
        for (int id = 0; id < MediumTasks.values().length; id++) {
            val task = MediumTasks.values()[id];
            if (player.getBooleanAttribute("medium-combat-achievement" + task.getTaskId())) {
                count++;
            }
        }
        return count;
    }

    public static boolean allMediumCombatAchievementsDone(Player player) {
        val totalCount = MediumTasks.values().length;
        val playersCount = countMediumDone(player);
        return playersCount == totalCount;
    }

    public static void sendMediumCompletion(Player player, final int taskId) {
        val task = MediumTasks.tasksById.get(taskId);
        player.sendMessage("Congratulations, you've completed a medium combat task: " + Colour.RS_GREEN.wrap(task.taskName));
        player.getNotifications().addLast(new Notification("Combat Task Completed!", "Task Completed: " + "<col=ffffff>" + task.taskName + "</col>", 0xc9af97));
    }


}