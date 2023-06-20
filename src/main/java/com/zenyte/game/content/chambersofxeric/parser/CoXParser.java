package com.zenyte.game.content.chambersofxeric.parser;

import com.zenyte.game.content.chambersofxeric.rewards.RaidNormalReward;
import com.zenyte.game.content.chambersofxeric.rewards.RaidRareReward;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.items.ItemDefinitions;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kotlin.Pair;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

public class CoXParser {
    private static ArrayList<CoXLogModel> raids = new ArrayList<>();

    public static ArrayList<CoXLogModel> parseData(String fileName) {
        try {
            raids.clear();
            Scanner scan = new Scanner(new File(fileName));

            while (scan.hasNext()) {
                String line = scan.nextLine();

                String host = "MISSING", mode = "MISSING", status = "MISSING";
                long totalPoints = 0L;
                CoXLogModel raid = null;
                ArrayList<RaidPlayer> raidPlayers = new ArrayList<>();
                ArrayList<RaidDeath> raidDeaths = new ArrayList<>();
                ArrayList<RaidRewardInfo> raidRaidRewardInfos = new ArrayList<>();
                ArrayList<RaidFloorTime> raidFloorTimes = new ArrayList<>();
                while (!line.equals("---End---")) {
                    if (line.contains("Chambers of Xeric: ")) {
                        host = line.split(": ")[1].split("'")[0];
                    }
                    if (line.contains("Mode: "))
                        mode = line.split(": ")[1];
                    if (line.contains("Status: "))
                        status = line.split(": ")[1];
                    if (line.contains("Total points: "))
                        totalPoints = Long.parseLong(line.split(": ")[1]);

                    raid = new CoXLogModel(host, mode, status, totalPoints);

                    if (line.contains("Rooms")) {
                        line = scan.nextLine();
                        ArrayList<RaidRoomInfo> raidRoomInfos = new ArrayList<>();
                        while (!line.equals("")) {
                            String roomLabel = line.replaceAll("\t", "").substring(0, 3);
                            String roomType = line.split("]")[1].split(" - ")[0];
                            String[] roomInfo = line.split(" - ")[1].split(", ");
                            ArrayList<Integer> roomInfoParsed = new ArrayList<>();
                            for (String info : roomInfo)
                                roomInfoParsed.add(Integer.parseInt(info));
                            RaidRoomInfo raidRoomInfo = new RaidRoomInfo(roomType, roomInfoParsed.get(0), roomInfoParsed.get(1),
                                    roomInfoParsed.get(2), roomInfoParsed.get(3), roomInfoParsed.get(4));
                            if (line.contains("time: ")) {
                                int startTime = Integer.parseInt(line.split(": ")[1].split("-")[0]);
                                int endTime = -1;
                                try {
                                    val string = line.split(": ")[1].split("-")[1];
                                    val index = string.indexOf('(');
                                    if (index != -1) {
                                        endTime = Integer.parseInt(string.substring(0, index));
                                    }
                                } catch (NumberFormatException e) {

                                }
                                raidRoomInfo.setStartTime(startTime);
                                raidRoomInfo.setEndTime(endTime);
                                raidRoomInfo.setLabel(roomLabel);
                            }
                            raidRoomInfos.add(raidRoomInfo);
                            line = scan.nextLine();
                        }
                        raid.setRaidRoomInfos(raidRoomInfos);
                    }

                    if (line.contains("Players(")) {
                        line = scan.nextLine();
                        if (!line.replaceAll("\t", "").isEmpty()){
                            for(String playerInfo : line.replaceAll("\t","").split(", ")){
                                int points = Integer.parseInt(playerInfo.substring(playerInfo.indexOf("(")+1, playerInfo.indexOf(")")));
                                raidPlayers.add(new RaidPlayer(playerInfo.substring(0, playerInfo.indexOf("(")), points));
                            }
                        }
                    }
                    if(line.contains("who have left")) {
                        line = scan.nextLine();
                        for (String playerName : line.replaceAll("\t", "").split(", ")) {
                            raidPlayers.add(new RaidPlayer(playerName));
                        }
                    }
                    raid.setRaidPlayers(raidPlayers);

                    if(line.contains("Rewards")){
                        line = scan.nextLine();

                        while(!line.isEmpty()){
                            line = line.replaceAll("\t", "");
                            ArrayList<RaidRewardItem> playerRewards = new ArrayList<>();
                            String playerName = line.split(": ")[0];
                            String[] rewardString = line.split(": ")[1].split(", ");
                            for(String reward : rewardString)
                            {
                                playerRewards.add(new RaidRewardItem(reward.split(" x ")[0], Integer.parseInt(reward.split(" x ")[1])));
                            }
                            raidRaidRewardInfos.add(new RaidRewardInfo(playerName, playerRewards));
                            line = scan.nextLine();
                        }
                    }
                    raid.setRaidRewardInfos(raidRaidRewardInfos);

                    if(line.contains("Deaths"))
                    {
                        line = scan.nextLine();
                        while(!line.isEmpty()) {
                            line = line.replaceAll("\t", "");
                            String playerName = line.split(": ")[0];
                            String deathRoom = line.split("at ")[1].split(" room")[0];
                            String deathBy = line.split("by ")[1].split(",")[0];
                            int pointsLost = Integer.parseInt(line.split("losing ")[1].split((line.contains("personal") ? " personal" : " group"))[0]);
                            raidDeaths.add(new RaidDeath(playerName, deathRoom, deathBy, pointsLost));
                            line = scan.nextLine();
                        }
                    }
                    raid.setRaidDeaths(raidDeaths);

                    if(line.contains("Time on floor completion")){
                        line = scan.nextLine();

                        while(!line.isEmpty()){
                            line = line.replaceAll("\t", "");
                            String floorName = line.split(": ")[0];
                            String floorTimeStr = line.split(": ")[1];
                            int floorTime = Integer.parseInt(floorTimeStr.substring(0, floorTimeStr.indexOf("(")));
                            raidFloorTimes.add(new RaidFloorTime(floorName, floorTime));
                            line = scan.nextLine();
                        }
                    }
                    raid.setRaidFloorTimes(raidFloorTimes);

                    line = scan.nextLine();
                }
                raids.add(raid);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return raids;
    }

    public static final void printCompletions() {
        int regularCompletions = 0;
        int regularFailures = 0;
        int regularScouts = 0;
        int cmCompletions = 0;
        int cmFailures = 0;
        int cmScouts = 0;

        for (val raid : raids) {
            if (raid.getMode().toLowerCase().contains("normal")) {
                switch(raid.getCompletionStatus()) {
                    case "Complete":
                        regularCompletions++;
                        break;
                    case "Incomplete":
                        regularFailures++;
                        break;
                    default:
                        regularScouts++;
                        break;
                }
            } else {
                switch(raid.getCompletionStatus()) {
                    case "Complete":
                        cmCompletions++;
                        break;
                    case "Incomplete":
                        cmFailures++;
                        break;
                    default:
                        cmScouts++;
                        break;
                }
            }
        }
        System.err.println("Regular completions: " + regularCompletions);
        System.err.println("Regular failures: " + regularFailures);
        System.err.println("Regular unbegun raids: " + regularScouts);
        System.err.println("Challenge mode completions: " + cmCompletions);
        System.err.println("Challenge mode failures: " + cmFailures);
        System.err.println("Challenge mode unbegun raids: " + cmScouts);
    }

    public static final void printFastestRoomCompletionTimes() {
        val map = new HashMap<String, Pair<CoXLogModel, Long>>();
        for (CoXLogModel raid : raids) {
            if (raid.getTotalPoints() == 7684) {
                continue;//Ziniy's bugged mass raid
            }
            if (raid.getRaidRoomInfos() == null) {
                continue;
            }
            for (val room : raid.getRaidRoomInfos()) {
                val time = room.getEndTime() - room.getStartTime();
                if (time <= 0) {
                    continue;
                }
                val timeInMap = map.get(room.getType());
                if (timeInMap == null || timeInMap.getSecond() > time) {
                    map.put(room.getType(), new Pair<CoXLogModel, Long>(raid, (long) time));
                }
            }
        }
        for (val entry : map.entrySet()) {
            System.err.println(entry.getKey() + ": " + (entry.getValue().getSecond() / 60) + " minutes" + ", " + (entry.getValue().getSecond() % 60) + " seconds" + ", " + entry.getValue().getFirst().getRaidPlayers());
        }
    }

    public static final void printFastestRaid(final String floor) {
        CoXLogModel raid = null;
        RaidFloorTime t = null;
        for (val r : raids) {
            if (r.getTotalPoints() == 7684) {
                continue;//Ziniy's bugged mass raid
            }
            if (!r.getCompletionStatus().equalsIgnoreCase("Complete")) {
                continue;
            }
            val time = Utils.findMatching(r.getRaidFloorTimes(), a -> a.getLevel().equalsIgnoreCase(floor));
            if (time == null) {
                continue;
            }
            if (t == null || (t.getTime() > time.getTime())) {
                raid = r;
                t = time;
            }
        }
        System.err.println(floor + ", " + ": " + t.getTime());
        for (val participant : raid.getRaidPlayers()) {
            System.err.println(participant.playerName);
        }
    }

    public static final void printDeathInfo() {
        int mostDeaths = 0;
        int totalDeaths = 0;
        val roomDeaths = new Object2IntOpenHashMap<String>();
        for (val raid : raids) {
            if (mostDeaths < raid.getRaidDeaths().size()) {
                mostDeaths = raid.getRaidDeaths().size();
            }
            for (val death : raid.getRaidDeaths()) {
                 totalDeaths++;
                 roomDeaths.put(death.getRoom(), roomDeaths.getOrDefault(death.getRoom(), 0) + 1);
            }
        }

        System.err.println("Most deaths in a raid: " + mostDeaths);
        System.err.println("Total deaths in all raids combined: " + totalDeaths);

        val stream = roomDeaths.object2IntEntrySet().stream().sorted(Comparator.comparingInt(c -> -c.getIntValue()));
        stream.forEachOrdered(entry -> System.err.println(entry.getKey() + ": " + entry.getIntValue()));
    }

    public static final void printRareDrops() {
        val map = new Object2IntOpenHashMap<String>();
        for (val rareReward : RaidRareReward.values()) {
            map.put(rareReward.getItem().getName(), rareReward.getItem().getId());
        }

        map.put("Olmlet", ItemId.OLMLET);
        map.put("Metamorphic dust", ItemId.METAMORPHIC_DUST);

        val list = new ObjectArrayList<Pair<String, String>>();

        for (val raid : raids) {
            for (val entry : raid.getRaidRewardInfos()) {
                for (val reward : entry.getRewards()) {
                    if (!map.containsKey(reward.getItemName())) {
                        continue;
                    }
                    list.add(new Pair<>(reward.getItemName(), entry.getPlayerName()));
                }
            }
        }

        list.forEach(entry -> System.err.println(entry.getSecond() + ": " + entry.getFirst()));

    }

    public static final void populateRewards(@NotNull final Player player) {
        player.getBank().resetBank();
        val map = new Object2IntOpenHashMap<String>();
        for (val rareReward : RaidRareReward.values()) {
            map.put(rareReward.getItem().getName(), rareReward.getItem().getId());
        }

        for (val reward : RaidNormalReward.values()) {
            map.put(ItemDefinitions.getOrThrow(reward.getId()).getName(), reward.getId());
        }

        map.put("Ancient tablet", ItemId.ANCIENT_TABLET);
        map.put("Olmlet", ItemId.OLMLET);
        map.put("Metamorphic dust", ItemId.METAMORPHIC_DUST);

        val container = player.getBank().getContainer();

        val rewardMap = new Object2IntOpenHashMap<String>();

        for (val raid : raids) {
            for (val entry : raid.getRaidRewardInfos()) {
                for (val reward : entry.getRewards()) {
                    rewardMap.put(reward.getItemName(), rewardMap.getOrDefault(reward.getItemName(), 0) + reward.getItemAmount());
                }
            }
        }

        val stream = rewardMap.object2IntEntrySet().stream().sorted(Comparator.comparingInt(c -> -c.getIntValue()));

        stream.forEachOrdered(entry -> {
            val item = new Item(map.getInt(entry.getKey()), entry.getIntValue());
            player.getBank().add(item);
        });

        container.refresh(player);
    }
}
