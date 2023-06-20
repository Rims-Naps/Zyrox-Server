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

public enum EliteTasks
{
    KILLCOUNT_1("Alchemical Veteran", "Killcount: Kill the Alchemical Hydra 75 times", 1),
    KILLCOUNT_2("Callisto Veteran", "Killcount: Kill Callisto 20 times", 2),
    KILLCOUNT_3("Cerberus Veteran", "Killcount: Kill Cerberus 75 times", 3),
    KILLCOUNT_4("Chambers of Xeric Veteran", "Killcount: Complete the Chambers of Xeric 25 times", 4),
    KILLCOUNT_5("Chaos Elemental Veteran", "Killcount: Kill the Chaos Elemental 25 times", 5),
    KILLCOUNT_6("Commander Zilyana Veteran", "Killcount: Kill Commander Zilyana 100 times", 6),
    KILLCOUNT_7("Corporeal Beast Veteran", "Killcount: Kill the Corporeal Beast 25 times", 7),
    KILLCOUNT_8("If Gorillas Could Fly", "Killcount: Kill a Demonic Gorilla", 8),
    KILLCOUNT_9("Fight Caves Veteran", "Killcount: Complete the Fight Caves once", 9),
    KILLCOUNT_10("General Graardor Veteran", "Killcount: Kill General Graardor 100 times", 10),
    KILLCOUNT_11("Grotesque Guardians Veteran", "Killcount: Kill the Grotesque Guardians 50 times", 11),
    KILLCOUNT_12("Half-Way There", "Killcount: Kill a Jal-Zek within the Inferno", 12),
    KILLCOUNT_13("K'ril Tsutsaroth Veteran", "Killcount: Kill K'ril Tsutsaroth 100 times", 13),
    KILLCOUNT_14("Kalphite Queen Veteran", "Killcount: Kill the Kalphite Queen 50 times", 14),
    KILLCOUNT_15("Kree'arra Veteran", "Killcount: Kill Kree'arra 100 times", 15),
    KILLCOUNT_16("Mimic Veteran", "Killcount: Kill the Mimic once", 16),
    KILLCOUNT_17("Scorpia Veteran", "Killcount: Kill Scorpia 25 times", 17),
    KILLCOUNT_18("Thermonuclear Veteran", "Killcount: Kill the Thermonuclear Smoke Devil 20 times", 18),
    KILLCOUNT_19("Venenatis Veteran", "Killcount: Kill Venenatis 20 times", 19),
    KILLCOUNT_20("Vet'eran", "Killcount: Kill Vet'ion 20 times", 20),
    KILLCOUNT_21("Vorkath Veteran", "Killcount: Kill Vorkath 50 times", 21),
    KILLCOUNT_22("Zulrah Veteran", "Killcount: Kill Zulrah 75 times", 22),
    KILLCOUNT_23("Theatre of Blood Veteran", "Killcount: Complete the Theatre of Blood 25 times", 72),

    MECHANICAL_1("Ghost Buster", "Mechnical: Kill Cerberus after successfully negating 6 or more attacks from Summoned Souls", 23),
    MECHANICAL_2("Redemption Enthusiast", "Mechnical: Kill the Abyssal Portal without forcing Vespula to land", 24),
    MECHANICAL_3("Mutta-diet", "Mechnical: Kill the Muttadile without letting her or her baby recover hitpoints from the meat tree", 25),
    MECHANICAL_4("Together We'll Fall", "Mechnical: Kill the Vanguards within 10 seconds of the first one dying", 26),
    MECHANICAL_5("Perfectly Balanced", "Mechnical: Kill the Vanguards without them resetting their health", 27),
    MECHANICAL_6("From One King to Another", "Mechnical: Kill Prime using a Rune Thrownaxe special attack, bounced off Dagannoth Rex", 28),
    MECHANICAL_7("Death to the Seer King", "Mechnical: Kill Dagannoth Prime whilst under attack by Dagannoth Supreme and Dagannoth Rex", 29),
    MECHANICAL_8("Toppling the Diarchy", "Mechnical: Kill Dagannoth Rex and one other Dagannoth king at the exact same time", 30),
    MECHANICAL_9("Death to the Warrior King", "Mechnical: Kill Dagannoth Rex whilst under attack by Dagannoth Supreme and Dagannoth Prime", 31),
    MECHANICAL_10("Rapid Succession", "Mechnical: Kill all three Dagannoth Kings within 9 seconds of the first one", 32),
    MECHANICAL_11("Death to the Archer King", "Mechnical: Kill Dagannoth Supreme whilst under attack by Dagannoth Prime and Dagannoth Rex", 33),
    MECHANICAL_12("A Near Miss!", "Mechnical: Complete the Fight Caves after surviving a hit from TzTok-Jad without praying", 34),
    MECHANICAL_13("Ourg Freezer II", "Mechnical: Kill General Graardor without him attacking any players", 35),
    MECHANICAL_14("Hard Hitter", "Mechnical: Kill the Giant Mole with 4 or fewer instances of damage", 36),
    MECHANICAL_15("Done before Dusk", "Mechnical: Kill the Grotesque Guardians before Dusk uses his prison attack for a second time", 37),
    MECHANICAL_16("Insect Deflection", "Mechnical: Kill the Kalphite Queen by using the Vengeance spell as the finishing blow", 38),
    MECHANICAL_17("Snake. Snake!? Snaaaaaake!", "Mechnical: Kill 3 Snakelings simultaneously", 39),
    MECHANICAL_18("Snake Rebound", "Mechnical: Kill Zulrah by using the Vengeance spell as the finishing blow", 40),

    PERFECTION_1("Anti-Bite Mechanics", "Perfection: Kill Cerberus without taking any melee damage", 41),
    PERFECTION_2("Unrequired Antifire", "Perfection: Kill Cerberus without taking damage from any lava pools", 42),
    PERFECTION_3("Cryo No More", "Perfection: Receive kill-credit for the Ice Demon without taking any damage", 43),
    PERFECTION_4("Shayzien Specialist", "Perfection: Receive kill-credit for a Lizardman Shaman without taking damage from any shamans in the room", 44),
    PERFECTION_5("Undying Raid Team", "Perfection: Complete a Chambers of Xeric raid without anyone dying", 45),
    PERFECTION_6("Dancing with Statues", "Perfection: Receive kill-credit for a Stone Guardian without taking damage from falling rocks", 46),
    PERFECTION_7("Hot on Your Feet", "Perfection: Kill the Corporeal Beast without anyone killing the dark core or taking damage from the dark core", 47),
    PERFECTION_8("Perfect Grotesque Guardians", "Perfection: Kill the Grotesque Guardians whilst completing the \"Don't look at the eclipse\", \"Prison Break\", \"Granite footwork\", \"Heal no more\", \"Static Awareness\" and \"Done before dusk\" tasks", 48),
    PERFECTION_9("Demonic Defence", "Perfection: Kill K'ril Tsutsaroth in a privately rented instance without taking any of his melee hits", 49),
    PERFECTION_10("Demon Evasion", "Perfection: Kill Skotizo without taking any damage", 50),
    PERFECTION_11("Hazard Prevention", "Perfection: Kill the Thermonuclear Smoke Devil without it hitting anyone", 51),

    RESTRICTION_1("Kill It with Fire", "Restriction: Finish off the Ice Demon with a fire spell", 52),
    RESTRICTION_2("Blizzard Dodger", "Restriction: Receive kill-credit for the Ice Demon without activating the Protect from Range prayer", 53),
    RESTRICTION_3("Reminisce", "Restriction: Kill Commander Zilyana with melee only", 54),
    RESTRICTION_4("Chicken Killer", "Restriction: Kill the Corporeal Beast solo", 55),
    RESTRICTION_5("Finding the Weak Spot", "Restriction: Finish off the Corporeal Beast with a Crystal Halberd special attack", 56),
    RESTRICTION_6("Hitting Them Where It Hurts", "Restriction: Finish off a Demonic Gorilla with a demonbane weapon", 57),
    RESTRICTION_7("Facing Jad Head-on", "Restriction: Complete the Fight Caves with only melee", 58),
    RESTRICTION_8("Plant-Based Diet", "Restriction: Kill Hespori without losing any prayer points", 59),
    RESTRICTION_9("Prayer Smasher", "Restriction: Kill the Kalphite Queen using only the Verac's Flail as a weapon", 60),
    RESTRICTION_10("Up for the Challenge", "Restriction: Kill Skotizo without equipping a demonbane weapon", 61),
    RESTRICTION_11("Spec'd Out", "Restriction: Kill the Thermonuclear Smoke Devil using only special attacks", 62),
    RESTRICTION_12("Stick 'em With the Pointy End", "Restriction: Kill Vorkath using melee weapons only", 63),
    RESTRICTION_13("Zombie Destroyer", "Restriction: Kill Vorkath's zombified spawn without using crumble undead", 64),

    SPEED_1("Dust Seeker", "Speed: Complete a Chambers of Xeric Challenge mode raid in the target time", 65),
    SPEED_2("Grotesque Guardians Speed-Trialist", "Speed: Kill the Grotesque Guardians in less than 2 minutes and 20 seconds", 66),
    SPEED_3("Hespori Speed-Trialist", "Speed: Kill the Hespori in less than 50 seconds", 67),
    SPEED_4("Zulrah Speed-Trialist", "Speed: Kill Zulrah in less than 1 minute 20 seconds", 68),
    SPEED_5("Cerberus Speed-Trialist", "Speed: Kill Cerberus in less than 50 seconds", 71),

    STAMINA_1("From Dusk...", "Stamina: Kill the Grotesque Guardians 10 times without leaving the instance", 69),
    STAMINA_2("Ten-tacles", "Stamina: Kill the Kraken 50 times in a privately rented instance without leaving the room", 70),

    ;
    private static final Int2ObjectMap<EliteTasks> tasksById = new Int2ObjectOpenHashMap<>();
    private static final ObjectList<EliteTasks> tasks = ObjectArrayList.wrap(values());

    private final String taskName;
    private String description;
    private int taskId;

    EliteTasks(String taskName, String description, int taskId) {
        this.description = description;
        this.taskId = taskId;
        this.taskName = taskName;
    }

    static {
        for (EliteTasks task : tasks) {
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
        String[] descs = new String[EliteTasks.values().length];
        for(int i = 0; i < EliteTasks.values().length; i++)
        {
            descs[i] = isDone(p, EliteTasks.values()[i]) ? "<str>" + EliteTasks.values()[i].getTaskName() : EliteTasks.values()[i].getTaskName();
        }
        return descs;
    }

    public static boolean isDone(Player player, EliteTasks task) {
        if (task.taskId == 1) {
            if (player.getNotificationSettings().getKillcount("alchemical hydra") >= 75){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 2) {
            if (player.getNotificationSettings().getKillcount("callisto") >= 20){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 3) {
            if (player.getNotificationSettings().getKillcount("cerberus") >= 75){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 4) {
            if (player.getNumericAttribute("chambersofxeric").intValue() >= 25){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 5) {
            if (player.getNotificationSettings().getKillcount("chaos elemental") >= 25){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 6) {
            if (player.getNotificationSettings().getKillcount("commander zilyana") >= 100){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 7) {
            if (player.getNotificationSettings().getKillcount("corporeal beast") >= 25){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 8) {
            if (player.getSettings().getKillsLog().getOrDefault("demonic gorilla", 0) >= 1){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 9) {
            if (player.getNotificationSettings().getKillcount("tztok-jad") >= 1){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 10) {
            if (player.getNotificationSettings().getKillcount("general graardor") >= 100){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 11) {
            if (player.getNotificationSettings().getKillcount("grotesque guardians") >= 50){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 12) {
            if (player.getNotificationSettings().getKillcount("jal-zek") >= 1){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 13) {
            if (player.getNotificationSettings().getKillcount("k'ril tsutsaroth") >= 100){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 14) {
            if (player.getNotificationSettings().getKillcount("Kalphite Queen") >= 50){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 15) {
            if (player.getNotificationSettings().getKillcount("kree'arra") >= 100){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 16) {
            if (player.getNotificationSettings().getKillcount("mimic") >= 1){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 17) {
            if (player.getNotificationSettings().getKillcount("scorpia") >= 25){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 18) {
            if (player.getNotificationSettings().getKillcount("thermonuclear smoke devil") >= 20){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 19) {
            if (player.getNotificationSettings().getKillcount("venenatis") >= 20){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 20) {
            if (player.getNotificationSettings().getKillcount("vet'ion") >= 20){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 21) {
            if (player.getNotificationSettings().getKillcount("vorkath") >= 50){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 22) {
            if (player.getNotificationSettings().getKillcount("zulrah") >= 75){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 72) {
            if (player.getNumericAttribute("theatreofblood").intValue() >= 25){
                if (!player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("elite-combat-achievement" + task.getTaskId(), true);
                    EliteTasks.sendEliteCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        return player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId());
    }

    public static int countEliteDone(Player player) {
        int count = 0;
        for (int id = 0; id < EliteTasks.values().length; id++) {
            val task = EliteTasks.values()[id];
            if (player.getBooleanAttribute("elite-combat-achievement" + task.getTaskId())) {
                count++;
            }
        }
        return count;
    }

    public static boolean allEliteCombatAchievementsDone(Player player) {
        val totalCount = EliteTasks.values().length;
        val playersCount = countEliteDone(player);
        return playersCount == totalCount;
    }

    public static void sendEliteCompletion(Player player, final int taskId) {
        val task = EliteTasks.tasksById.get(taskId);
        player.sendMessage("Congratulations, you've completed an elite combat task: " + Colour.RS_GREEN.wrap(task.taskName));
        player.getNotifications().addLast(new Notification("Combat Task Completed!", "Task Completed: " + "<col=ffffff>" + task.taskName + "</col>", 0xc9af97));
    }


}