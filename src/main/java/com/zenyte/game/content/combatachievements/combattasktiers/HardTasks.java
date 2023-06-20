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

public enum HardTasks
{

    KILLCOUNT_1("Callisto Adept", "Killcount: Kill Callisto 10 times", 1),
    KILLCOUNT_2("Chaos Elemental Adept", "Killcount: Kill the Chaos Elemental 10 times", 2),
    KILLCOUNT_3("Chaos Fanatic Adept", "Killcount: Kill the Chaos Fanatic 25 times", 3),
    KILLCOUNT_4("Commander Zilyana Adept", "Killcount: Kill Commander Zilyana 50 times", 4),
    KILLCOUNT_5("Crazy Archaeologist Adept", "Killcount: Kill the Crazy Archaeologist 25 times", 5),
    KILLCOUNT_6("Dagannoth Rex Adept", "Killcount: Kill Dagannoth Rex 25 times", 6),
    KILLCOUNT_7("Dagannoth Supreme Adept", "Killcount: Kill Dagannoth Supreme 25 times", 7),
    KILLCOUNT_8("Dagannoth Prime Adept", "Killcount: Kill Dagannoth Prime 25 times", 8),
    KILLCOUNT_9("General Graardor Adept", "Killcount: Kill General Graardor 50 times", 9),
    KILLCOUNT_10("Grotesque Guardians Adept", "Killcount: Kill the Grotesque Guardians 25 times", 10),
    KILLCOUNT_11("Hespori Adept", "Killcount: Kill Hespori 5 times", 11),
    KILLCOUNT_12("K'ril Tsutsaroth Adept", "Killcount: Kill K'ril Tsutsaroth 50 times", 12),
    KILLCOUNT_13("Kalphite Queen Adept", "Killcount: Kill the Kalphite Queen 25 times", 13),
    KILLCOUNT_14("Kraken Adept", "Killcount: Kill the Kraken 20 times", 14),
    KILLCOUNT_15("Kree'arra Adept", "Killcount: Kill Kree'arra 50 times", 15),
    KILLCOUNT_16("Scorpia Adept", "Killcount: Kill Scorpia 10 times", 16),
    KILLCOUNT_17("Skotizo Adept", "Killcount: Kill Skotizo 5 times", 17),
    KILLCOUNT_18("Venenatis Adept", "Killcount: Kill Venenatis 10 times", 18),
    KILLCOUNT_19("Vet'ion Adept", "Killcount: Kill Vet'ion 10 times", 19),
    KILLCOUNT_20("Zulrah Adept", "Killcount: Kill Zulrah 25 times", 20),

    MECHANICAL_1("Hoarder", "Mechnical: Kill the Chaos Elemental without it unequipping any of your items", 21),
    MECHANICAL_2("Commander Showdown", "Mechnical: Finish off Commander Zilyana while all of her bodyguards are dead", 22),
    MECHANICAL_3("General Showdown", "Mechnical: Finish off General Graardor whilst all of his bodyguards are dead", 23),
    MECHANICAL_4("Demonic Showdown", "Mechnical: Finish off K'ril Tsutsaroth while all of his bodyguards are dead", 24),
    MECHANICAL_5("Airborne Showdown", "Mechnical: Finish off Kree'arra whilst all of her bodyguards are dead", 25),
    MECHANICAL_6("Ourg Freezer", "Mechnical: Kill General Graardor whilst he is immobilized", 26),
    MECHANICAL_7("Whack-a-Mole", "Mechnical: Kill the Giant Mole within 10 seconds of her resurfacing", 27),
    MECHANICAL_8("Why Are You Running?", "Mechnical: Kill the Giant Mole without her burrowing more than 2 times", 28),
    MECHANICAL_9("Static Awareness", "Mechnical: Kill the Grotesque Guardians without being hit by any lightning attacks", 29),
    MECHANICAL_10("Heal No More", "Mechnical: Kill the Grotesque Guardians without letting Dawn receive any healing from her orbs", 30),
    MECHANICAL_11("Granite Footwork", "Mechnical: Kill the Grotesque Guardians without taking damage from Dawn's rockfall attack", 31),
    MECHANICAL_12("Prison Break", "Mechnical: Kill the Grotesque Guardians without taking damage from Dusk's prison attack", 32),
    MECHANICAL_13("Don't Look at the Eclipse", "Mechnical: Kill the Grotesque Guardians without taking damage from Dusk's blinding attack", 33),
    MECHANICAL_14("Weed Whacker", "Mechnical: Kill all of Hesporis flowers within 5 seconds", 34),
    MECHANICAL_15("Hesporisn't", "Mechnical: Finish off Hespori with a special attack", 35),
    MECHANICAL_16("Yarr No More", "Mechnical: Receive kill-credit for K'ril Tsutsaroth without him using his special attack", 36),
    MECHANICAL_17("Chitin Penetrator", "Mechnical: Kill the Kalphite Queen while her defence was last lowered by you", 37),
    MECHANICAL_18("Unnecessary Optimization", "Mechnical: Kill the Kraken after killing all four tentacles", 38),

    PERFECTION_1("The Flincher", "Perfection: Kill the Chaos Elemental without taking any damage from it's attacks", 39),
    PERFECTION_2("I Can't Reach That", "Perfection: Kill Scorpia without taking any damage from her", 40),

    RESTRICTION_1("Faithless Crypt Run", "Restriction: Kill all six Barrows Brothers and loot the Barrows chest without ever having more than 0 prayer points", 41),
    RESTRICTION_2("Just Like That", "Restriction: Kill Karil using only damage dealt by special attacks", 42),
    RESTRICTION_3("Praying to the Gods", "Restriction: Kill the Chaos Fanatic 10 times without drinking any potion which restores prayer or leaving the Wilderness", 43),
    RESTRICTION_4("Demonbane Weaponry II", "Restriction: Finish off K'ril Tsutsaroth with a demonbane weapon", 44),
    RESTRICTION_5("Guardians No More", "Restriction: Kill Scorpia without killing her guardians", 45),

    STAMINA_1("Who Is the King Now?", "Stamina: Kill The King Black Dragon 10 times in a privately rented instance without leaving the instance", 46),
    STAMINA_2("Krakan't Hurt Me", "Stamina: Kill the Kraken 25 times in a privately rented instance without leaving the room", 47),
    STAMINA_3("Why Fletch?", "Stamina: Subdue the Wintertodt after earning 3000 or more points", 48),
    ;
    private static final Int2ObjectMap<HardTasks> tasksById = new Int2ObjectOpenHashMap<>();
    private static final ObjectList<HardTasks> tasks = ObjectArrayList.wrap(values());

    private final String taskName;
    private String description;
    private int taskId;

    HardTasks(String taskName, String description, int taskId) {
        this.description = description;
        this.taskId = taskId;
        this.taskName = taskName;
    }

    static {
        for (HardTasks task : tasks) {
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
        String[] descs = new String[HardTasks.values().length];
        for(int i = 0; i < HardTasks.values().length; i++)
        {
            descs[i] = isDone(p, HardTasks.values()[i]) ? "<str>" + HardTasks.values()[i].getTaskName() : HardTasks.values()[i].getTaskName();
        }
        return descs;
    }

    public static boolean isDone(Player player, HardTasks task) {
        if (task.taskId == 1) {
            if (player.getNotificationSettings().getKillcount("callisto") >= 10){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 2) {
            if (player.getNotificationSettings().getKillcount("chaos elemental") >= 10){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 3) {
            if (player.getNotificationSettings().getKillcount("chaos fanatic") >= 25){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 4) {
            if (player.getNotificationSettings().getKillcount("commander zilyana") >= 50){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 5) {
            if (player.getNotificationSettings().getKillcount("crazy archaeologist") >= 25){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 6) {
            if (player.getNotificationSettings().getKillcount("dagannoth rex") >= 25){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 7) {
            if (player.getNotificationSettings().getKillcount("dagannoth supreme") >= 25){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 8) {
            if (player.getNotificationSettings().getKillcount("dagannoth prime") >= 25){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 9) {
            if (player.getNotificationSettings().getKillcount("general graardor") >= 50){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 10) {
            if (player.getNotificationSettings().getKillcount("grotesque guardians") >= 25){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 11) {
            if (player.getNotificationSettings().getKillcount("hespori") >= 5){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 12) {
            if (player.getNotificationSettings().getKillcount("k'ril tsutsaroth") >= 50){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 13) {
            if (player.getNotificationSettings().getKillcount("Kalphite Queen") >= 25){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 14) {
            if (player.getNotificationSettings().getKillcount("kraken") >= 20){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 15) {
            if (player.getNotificationSettings().getKillcount("kree'arra") >= 50){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 16) {
            if (player.getNotificationSettings().getKillcount("scorpia") >= 10){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 17) {
            if (player.getNotificationSettings().getKillcount("skotizo") >= 5){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 18) {
            if (player.getNotificationSettings().getKillcount("venenatis") >= 10){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 19) {
            if (player.getNotificationSettings().getKillcount("vet'ion") >= 10){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 20) {
            if (player.getNotificationSettings().getKillcount("zulrah") >= 25){
                if (!player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("hard-combat-achievement" + task.getTaskId(), true);
                    HardTasks.sendHardCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        return player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId());
    }

    public static int countHardDone(Player player) {
        int count = 0;
        for (int id = 0; id < HardTasks.values().length; id++) {
            val task = HardTasks.values()[id];
            if (player.getBooleanAttribute("hard-combat-achievement" + task.getTaskId())) {
                count++;
            }
        }
        return count;
    }

    public static boolean allHardCombatAchievementsDone(Player player) {
        val totalCount = HardTasks.values().length;
        val playersCount = countHardDone(player);
        return playersCount == totalCount;
    }

    public static void sendHardCompletion(Player player, final int taskId) {
        val task = HardTasks.tasksById.get(taskId);
        player.sendMessage("Congratulations, you've completed a hard combat task: " + Colour.RS_GREEN.wrap(task.taskName));
        player.getNotifications().addLast(new Notification("Combat Task Completed!", "Task Completed: " + "<col=ffffff>" + task.taskName + "</col>", 0xc9af97));
    }


}