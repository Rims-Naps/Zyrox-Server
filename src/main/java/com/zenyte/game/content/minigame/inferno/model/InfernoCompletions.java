package com.zenyte.game.content.minigame.inferno.model;

import com.google.common.reflect.TypeToken;
import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import com.zenyte.game.world.entity.player.GameMode;
import com.zenyte.game.world.entity.player.Player;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.util.*;

/**
 * @author Tommeh | 15/12/2019 | 21:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class InfernoCompletions implements ScheduledExternalizable {

    public static Map<Object, List<String>> info = new HashMap<>();

    public static void add(final Player player) {
        info.get("completions_" + player.getGameMode()).add(player.getName());
    }

    public static void addPracticeRun(@NotNull final Player player) {
        var list = info.computeIfAbsent("practice mode runs", k -> new ArrayList<>());
        list.add(new Date().toString() + ": " + player.getUsername());
    }

    public static void setBroadcasted(final Player player) {
        val modeCompletions = info.get("broadcasted_" + player.getGameMode());
        if (!modeCompletions.contains(player.getName())) {
            modeCompletions.add(player.getName());
        }
    }

    public static boolean isBroadcasted(final Player player) {
        return info.get("broadcasted_" + player.getGameMode()).contains(player.getName());
    }

    public static int getCompletions(final GameMode mode) {
        return info.get("completions_" + mode).size();
    }

    @Override
    public int writeInterval() {
        return 10;
    }

    @Override
    public void read(BufferedReader reader) {
        info = gson.fromJson(reader, new TypeToken<Map<Object, List<String>>>(){}.getType());
        if (info.isEmpty()) {
            setDefaults();
        }
    }

    @Override
    public void write() {
        if (info.isEmpty()) {
            setDefaults();
        }
        out(gson.toJson(info));
    }

    private static void setDefaults() {
        for (val mode : GameMode.values) {
            info.put("completions_" + mode, new ArrayList<>());
            info.put("broadcasted_" + mode, new ArrayList<>());
        }
    }

    @Override
    public String path() {
        return "data/inferno completions.json";
    }
}
