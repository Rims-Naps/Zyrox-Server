package com.zenyte.game.content.theatreofblood.plugin.object.scoreboard;

import com.google.common.collect.ImmutableList;
import com.zenyte.game.content.achievementdiary.Diary;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;


import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Cresinkel
 */

public class ScoreBoardInterface {

    public static HashMap<String,Integer> tobSpeedsSolo = new HashMap<String,Integer>();
    public static HashMap<String,Integer> tobSpeedsDuo = new HashMap<String,Integer>();
    public static HashMap<String,Integer> tobSpeedsTrio = new HashMap<String,Integer>();
    public static HashMap<String,Integer> tobSpeedsQuad = new HashMap<String,Integer>();
    public static HashMap<String,Integer> tobSpeedsQuint = new HashMap<String,Integer>();

    public static ArrayList<HashMap<String,Integer>> tobSpeedsArray = new ArrayList<HashMap<String,Integer>>(Arrays.asList(tobSpeedsSolo,tobSpeedsDuo,tobSpeedsTrio,tobSpeedsQuad,tobSpeedsQuint));

    private static String ticksToTime(int ticks) {
        val minutes = TimeUnit.TICKS.toMinutes(ticks);
        val seconds = TimeUnit.TICKS.toSeconds(ticks - TimeUnit.MINUTES.toTicks(minutes));
        return Colour.RS_RED.wrap(minutes + ":" + (seconds > 9 ? seconds : "0" + seconds));
    }

    public static void handleBoard(Player player, Integer teamSize) {
        player.setFaceLocation(player.getLocation().transform(Direction.NORTH));
        refreshBoard(teamSize);
        var tobSpeeds = teamSize == 1 ? tobSpeedsSolo :
                teamSize == 2 ? tobSpeedsDuo :
                teamSize == 3 ? tobSpeedsTrio :
                teamSize == 4 ? tobSpeedsQuad : tobSpeedsQuint;
        HashMap<String, Integer> sortedMap = sortByValue(tobSpeeds, true);
        val entries = ImmutableList.<String>builder()
                .add("1) " + sortedMap.keySet().toArray()[0] + ". Time: " + ticksToTime((int) sortedMap.values().toArray()[0]))
                .add("2) " + sortedMap.keySet().toArray()[1] + ". Time: " + ticksToTime((int) sortedMap.values().toArray()[1]))
                .add("3) " + sortedMap.keySet().toArray()[2] + ". Time: " + ticksToTime((int) sortedMap.values().toArray()[2]))
                .add("4) " + sortedMap.keySet().toArray()[3] + ". Time: " + ticksToTime((int) sortedMap.values().toArray()[3]))
                .add("5) " + sortedMap.keySet().toArray()[4] + ". Time: " + ticksToTime((int) sortedMap.values().toArray()[4]))
                .build();
        Diary.sendJournal(player, "Fastest TOB runs (" + (
                teamSize == 1 ? "Solo" :
                teamSize == 2 ? "Duo" :
                teamSize == 3 ? "Trio" :
                teamSize == 4 ? "4-Man" : "5-Man"
                ) + ")", new ArrayList<>(entries));
    }

    public static void addToMap() {
        tobSpeedsSolo.put("#1", 12000);
        tobSpeedsSolo.put("#2", 12000);
        tobSpeedsSolo.put("#3", 12000);
        tobSpeedsSolo.put("#4", 12000);
        tobSpeedsSolo.put("#5", 12000);
        tobSpeedsDuo.put("#1", 12000);
        tobSpeedsDuo.put("#2", 12000);
        tobSpeedsDuo.put("#3", 12000);
        tobSpeedsDuo.put("#4", 12000);
        tobSpeedsDuo.put("#5", 12000);
        tobSpeedsTrio.put("#1", 12000);
        tobSpeedsTrio.put("#2", 12000);
        tobSpeedsTrio.put("#3", 12000);
        tobSpeedsTrio.put("#4", 12000);
        tobSpeedsTrio.put("#5", 12000);
        tobSpeedsQuad.put("#1", 12000);
        tobSpeedsQuad.put("#2", 12000);
        tobSpeedsQuad.put("#3", 12000);
        tobSpeedsQuad.put("#4", 12000);
        tobSpeedsQuad.put("#5", 12000);
        tobSpeedsQuint.put("#1", 12000);
        tobSpeedsQuint.put("#2", 12000);
        tobSpeedsQuint.put("#3", 12000);
        tobSpeedsQuint.put("#4", 12000);
        tobSpeedsQuint.put("#5", 12000);
    }

    public static void refreshBoard(Integer teamSize) {
        if (ScoreBoardInterface.tobSpeedsSolo.isEmpty()) {
            addToMap();
        }

        for (val member : World.getPlayers()) {
            val scores = tobSpeedsArray.get(teamSize - 1);
            if (member.getAttributes().containsKey("tobpb" + teamSize)) {
                val total = member.getNumericAttribute("tobpb" + teamSize).intValue();
                if (scores.containsKey(member.getName())) {
                    if (scores.get(member.getName()) > total) {
                        tobSpeedsArray.get(teamSize - 1).put(member.getName(), total);
                    }
                } else {
                    for (val key : scores.keySet().toArray()) {
                        val value = scores.get(key);
                        if (value > total) {
                            tobSpeedsArray.get(teamSize - 1).remove(key);
                            tobSpeedsArray.get(teamSize - 1).put(member.getName(), total);
                            break;
                        }
                    }
                }
            } else {
                if (scores.containsKey(member.getName())) {
                    tobSpeedsArray.get(teamSize - 1).remove(member.getName());
                    for (int index = 1; index < 6; index++) {
                        if (!scores.containsKey("#" + index)) {
                            tobSpeedsArray.get(teamSize - 1).put("#" + index, 12000);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static HashMap<String, Integer> sortByValue(Map<String, Integer> unsortMap, final boolean order) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }
}
