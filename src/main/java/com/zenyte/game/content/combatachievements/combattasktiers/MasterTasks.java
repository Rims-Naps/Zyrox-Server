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

public enum MasterTasks
{

    KILLCOUNT_1("Alchemical Master", "Killcount: Kill the Alchemical Hydra 150 times", 1),
    KILLCOUNT_2("Cerberus Master", "Killcount: Kill Cerberus 150 times", 2),
    KILLCOUNT_3("Chambers of Xeric Master", "Killcount: Complete the Chambers of Xeric 75 times", 3),
    KILLCOUNT_4("Chambers of Xeric: CM Master", "Killcount: Complete the Chambers of Xeric: Challenge Mode 10 times", 4),
    KILLCOUNT_5("Corporeal Beast Master", "Killcount: Kill the Corporeal Beast 50 times", 5),
    KILLCOUNT_6("Fight Caves Master", "Killcount: Complete the Fight Caves 5 times", 6),
    KILLCOUNT_7("Vorkath Master", "Killcount: Kill Vorkath 100 times", 7),
    KILLCOUNT_8("Zulrah Master", "Killcount: Kill Zulrah 150 times", 8),
    KILLCOUNT_9("Theatre of Blood Master", "Killcount: Complete the Theatre of Blood 75 times", 54),

    MECHANICAL_1("The Flame Skipper", "Mechnical: Kill the Alchemical Hydra without letting it spawn a flame wall attack", 9),
    MECHANICAL_2("Mixing Correctly", "Mechnical: Kill the Alchemical Hydra without empowering it", 10),
    MECHANICAL_3("Don't Flame Me", "Mechnical: Kill the Alchemical Hydra without being hit by the flame wall attack", 11),
    MECHANICAL_4("Lightning Lure", "Mechnical: Kill the Alchemical Hydra without being hit by the lightning attack", 12),
    MECHANICAL_5("Unrequired Antipoisons", "Mechnical: Kill the Alchemical Hydra without being hit by the acid pool attack", 13),
    MECHANICAL_6("Arooo No More", "Mechnical: Kill Cerberus without any of the Summoned Souls being spawned", 14),
    MECHANICAL_7("Anvil No More", "Mechnical: Kill Tekton before he returns to his anvil for a second time after the fight begins", 15),
    MECHANICAL_8("Stop Drop and Roll", "Mechnical: Kill Vasa Nistirio before he performs his teleport attack for the second time", 16),
    MECHANICAL_9("A Not So Special Lizard", "Mechnical: Kill the Great Olm in a solo raid without letting him use any of the following special attacks in his second to last phase: Crystal Burst, Lightning Walls, Teleportation Portals or left-hand autohealing", 17),
    MECHANICAL_10("Putting It Olm on the Line", "Mechnical: Complete a Chambers of Xeric solo raid with more than 50,000 points", 18),
    MECHANICAL_11("No Time for Death", "Mechnical: Clear the Tightrope room without killing any Deathly Mages or Deathly Rangers", 19),
    MECHANICAL_12("You Didn't Say Anything About a Bat", "Mechnical: Complete the Fight Caves without being attacked by a Tz-Kih", 20),
    MECHANICAL_13("Denying the Healers", "Mechnical: Complete the Fight Caves without letting any of the Yt-MejKot heal", 21),
    MECHANICAL_14("Collateral Damage", "Mechnical: Kill Kree'arra in a private instance without ever attacking him directly", 22),
    MECHANICAL_15("The Walk", "Mechnical: Hit Vorkath 12 times during the acid special without getting hit by his rapid fire or the acid pools", 23),
    MECHANICAL_16("Pop It", "Mechnical: Kill Verzik without any Nylocas being frozen and without anyone taking damage from the Nylocas", 55),
    MECHANICAL_17("A Timely Snack", "Mechnical: Kill Sotetseg after surviving at least 3 ball attacks without sharing the damage and without anyone dying throughout the fight", 56),
    MECHANICAL_18("Two-Down", "Mechnical: Kill the Pestilent Bloat before he shuts down for the third time", 57),

    PERFECTION_1("Alcleanical Hydra", "Perfection: Kill the Alchemical Hydra without taking any damage", 24),
    PERFECTION_2("Blind Spot", "Perfection: Kill Tekton without taking any damage", 25),
    PERFECTION_3("Perfect Olm (Solo)", "Perfection: Kill the Great Olm in a solo raid without taking damage from any of the following: Teleport portals, Fire Walls, Healing pools, Crystal Bombs, Crystal Burst or Prayer Orbs. You also cannot let his claws regenerate or take damage from the same acid pool back to back", 26),
    PERFECTION_4("Perfect Olm (Group)", "Perfection: Kill the Great Olm in a non solo raid without any team member taking damage from any of the following: Teleport portals, Fire Walls, Healing pools, Crystal Bombs, Crystal Burst or Prayer Orbs. You also cannot let his claws regenerate or take damage from the same acid pool back to back.", 27),
    PERFECTION_5("Playing with Lasers", "Perfection: Clear the Crystal Crabs room without wasting an orb after the first crystal has been activated", 28),
    PERFECTION_6("Undying Raider", "Perfection: Complete a Chambers of Xeric solo raid without dying", 29),
    PERFECTION_7("Immortal Raider", "Perfection: Complete a Chambers of Xeric Challenge mode (Solo) raid without dying", 30),
    PERFECTION_8("Immortal Raid Team", "Perfection: Complete a Chambers of Xeric: Challenge mode raid without anyone dying", 31),
    PERFECTION_9("Perfect Grotesque Guardians II", "Perfection: Kill the Grotesque Guardians 5 times in a row whilst completing the Perfect Grotesque Guardians task every time", 32),
    PERFECTION_10("Nibblers, Begone!", "Perfection: Kill Tzkal-Zuk without letting a pillar fall before wave 67", 33),
    PERFECTION_11("Swoop No More", "Perfection: Kill Kree'arra in a private instance without taking any melee damage from the boss or his bodyguards", 34),
    PERFECTION_12("Dodging the Dragon", "Perfection: Kill Vorkath 5 times without taking any damage from his special attacks and without leaving his area", 35),
    PERFECTION_13("Perfect Zulrah", "Perfection: Kill Zulrah whilst taking no damage from the following: Snakelings, Venom Clouds, Zulrah's Green or Crimson phase", 36),
    PERFECTION_14("Perfect Maiden", "Perfection: Kill The Maiden of Sugadinti without anyone in the team taking damage from the following sources: Blood Spawn projectiles and Blood Spawn trails. Also, without taking damage off prayer and without letting any of the Nylocas Matomenos heal The Maiden", 58),
    PERFECTION_15("Perfect Bloat", "Perfection: Kill the Pestilent Bloat without anyone in the team taking damage from the following sources: Pestilent flies, Falling body parts or The Pestilent Bloats stomp attack", 59),
    PERFECTION_16("Perfect Nylocas", "Perfection: Kill the Nylocas Vasilias without anyone in the team attacking any Nylocas with the wrong attack style, without letting a pillar collapse and without getting hit by any of the Nylocas Vasilias attacks whilst off prayer", 60),
    PERFECTION_17("Perfect Sotesteg", "Perfection: Kill Sotetseg without anyone in the team stepping on the wrong tile in the maze, without getting hit by the tornado and without taking any damage from Sotetseg's attacks whilst off prayer.", 61),
    PERFECTION_18("Perfect Xarpus", "Perfection: Kill Xarpus without anyone in the team taking any damage from Xarpus' attacks and without letting an exhumed heal Xarpus more than twice", 62),
    PERFECTION_19("Perfect Verzik", "Perfection: Defeat Verzik Vitur without anyone in the team taking damage from Verzik Vitur's attacks other than her spider form's correctly prayed against regular magical and ranged attacks", 63),

    RESTRICTION_1("Moving Collateral", "Restriction: Kill Commander Zilyana in a private instance without attacking her directly", 37),
    RESTRICTION_2("Precise Positioning", "Restriction: Kill Skotizo with the final source of damage being a Chinchompa explosion", 38),
    RESTRICTION_3("Can't Drain This", "Restriction: Kill The Maiden of Sugadinti without anyone in the team losing any prayer points", 64),
    RESTRICTION_4("Can You Dance?", "Restriction: Kill Xarpus without anyone in the team using a ranged or magic weapon", 65),
    RESTRICTION_5("Back in My Day...", "Restriction: Complete the Theatre of Blood without any member of the team equipping a Scythe of Vitur", 66),

    SPEED_1("Alchemical Speed-Chaser", "Speed: Kill the Alchemical Hydra in less than 1 minute 50 seconds", 39),
    SPEED_2("Chambers of Xeric (Trio) Speed-Chaser", "Speed: Complete a Chambers of Xeric (Trio) in less than 18 minutes", 40),
    SPEED_3("Chambers of Xeric (Solo) Speed-Chaser", "Speed: Complete a Chambers of Xeric (Solo) in less than 21 minutes", 41),
    SPEED_4("Chambers of Xeric: CM (Trio) Speed-Chaser", "Speed: Complete a Chambers of Xeric: Challenge Mode (Trio) in less than 40 minutes", 42),
    SPEED_5("Chambers of Xeric: CM (Solo) Speed-Chaser", "Speed: Complete a Chambers of Xeric: Challenge Mode (Solo) in less than 50 minutes", 43),
    SPEED_6("Fight Caves Speed-Chaser", "Speed: Complete the Fight Caves in less than 25 minutes", 44),
    SPEED_7("Grotesque Guardians Speed-Chaser", "Speed: Kill the Grotesque Guardians in less than 2 minutes", 45),
    SPEED_8("Hespori Speed-Chaser", "Speed: Kill the Hespori in less than 40 seconds", 46),
    SPEED_9("Vorkath Speed-Chaser", "Speed: Kill Vorkath in less than 1 minute and 15 seconds", 47),
    SPEED_10("Zulrah Speed-Chaser", "Speed: Kill Zulrah in less than 1 minute", 48),
    SPEED_11("Theatre (Trio) Speed-Chaser", "Speed: Complete the Theatre of Blood (Trio) in less than 20 minutes", 67),
    SPEED_12("Theatre (4-Scale) Speed-Chaser", "Speed: Complete the Theatre of Blood (4-scale) in less than 17 minutes", 68),
    SPEED_13("Theatre (5-Scale) Speed-Chaser", "Speed: Complete the Theatre of Blood (5-scale) in less than 16 minutes", 69),

    STAMINA_1("Working Overtime", "Stamina: Kill the Alchemical Hydra 15 times without leaving the room", 49),
    STAMINA_2("... 'til Dawn", "Stamina: Kill the Grotesque Guardians 20 times without leaving the instance", 50),
    STAMINA_3("One Hundred Tentacles", "Stamina: Kill the Kraken 75 times in a private instance without leaving the room", 51),
    STAMINA_4("Extended Encounter", "Stamina: Kill Vorkath 10 times without leaving his area", 52),
    ;
    private static final Int2ObjectMap<MasterTasks> tasksById = new Int2ObjectOpenHashMap<>();
    private static final ObjectList<MasterTasks> tasks = ObjectArrayList.wrap(values());

    private final String taskName;
    private String description;
    private int taskId;

    MasterTasks(String taskName, String description, int taskId) {
        this.description = description;
        this.taskId = taskId;
        this.taskName = taskName;
    }

    static {
        for (MasterTasks task : tasks) {
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
        String[] descs = new String[MasterTasks.values().length];
        for(int i = 0; i < MasterTasks.values().length; i++)
        {
            descs[i] = isDone(p, MasterTasks.values()[i]) ? "<str>" + MasterTasks.values()[i].getTaskName() : MasterTasks.values()[i].getTaskName();
        }
        return descs;
    }

    public static boolean isDone(Player player, MasterTasks task) {
        if (task.taskId == 1) {
            if (player.getNotificationSettings().getKillcount("alchemical hydra") >= 150){
                if (!player.getBooleanAttribute("master-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("master-combat-achievement" + task.getTaskId(), true);
                    MasterTasks.sendMasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 2) {
            if (player.getNotificationSettings().getKillcount("cerberus") >= 150){
                if (!player.getBooleanAttribute("master-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("master-combat-achievement" + task.getTaskId(), true);
                    MasterTasks.sendMasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 3) {
            if (player.getNumericAttribute("chambersofxeric").intValue() >= 75){
                if (!player.getBooleanAttribute("master-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("master-combat-achievement" + task.getTaskId(), true);
                    MasterTasks.sendMasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 4) {
            if (player.getNumericAttribute("challengechambersofxeric").intValue() >= 10){
                if (!player.getBooleanAttribute("master-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("master-combat-achievement" + task.getTaskId(), true);
                    MasterTasks.sendMasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 5) {
            if (player.getNotificationSettings().getKillcount("corporeal beast") >= 50){
                if (!player.getBooleanAttribute("master-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("master-combat-achievement" + task.getTaskId(), true);
                    MasterTasks.sendMasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 6) {
            if (player.getNotificationSettings().getKillcount("tztok-jad") >= 5){
                if (!player.getBooleanAttribute("master-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("master-combat-achievement" + task.getTaskId(), true);
                    MasterTasks.sendMasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 7) {
            if (player.getNotificationSettings().getKillcount("vorkath") >= 100){
                if (!player.getBooleanAttribute("master-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("master-combat-achievement" + task.getTaskId(), true);
                    MasterTasks.sendMasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 8) {
            if (player.getNotificationSettings().getKillcount("zulrah") >= 150){
                if (!player.getBooleanAttribute("master-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("master-combat-achievement" + task.getTaskId(), true);
                    MasterTasks.sendMasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        if (task.taskId == 54) {
            if (player.getNumericAttribute("theatreofblood").intValue() >= 75){
                if (!player.getBooleanAttribute("master-combat-achievement" + task.getTaskId())) {
                    player.putBooleanAttribute("master-combat-achievement" + task.getTaskId(), true);
                    MasterTasks.sendMasterCompletion(player, task.getTaskId());
                }
                return true;
            }
        }
        return player.getBooleanAttribute("master-combat-achievement" + task.getTaskId());
    }

    public static int countMasterDone(Player player) {
        int count = 0;
        for (int id = 0; id < MasterTasks.values().length; id++) {
            val task = MasterTasks.values()[id];
            if (player.getBooleanAttribute("master-combat-achievement" + task.getTaskId())) {
                count++;
            }
        }
        return count;
    }

    public static boolean allMasterCombatAchievementsDone(Player player) {
        val totalCount = MasterTasks.values().length;
        val playersCount = countMasterDone(player);
        return playersCount == totalCount;
    }

    public static void sendMasterCompletion(Player player, final int taskId) {
        val task = MasterTasks.tasksById.get(taskId);
        player.sendMessage("Congratulations, you've completed a master combat task: " + Colour.RS_GREEN.wrap(task.taskName));
        player.getNotifications().addLast(new Notification("Combat Task Completed!", "Task Completed: " + "<col=ffffff>" + task.taskName + "</col>", 0xc9af97));
    }


}