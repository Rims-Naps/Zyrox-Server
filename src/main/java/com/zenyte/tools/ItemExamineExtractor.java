package com.zenyte.tools;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.zenyte.Constants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Tommeh | 18/11/2019 | 18:11
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ItemExamineExtractor {

    @Getter
    @RequiredArgsConstructor
    public static class ItemExamine {
        private final int id;
        private final String examine;
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final ItemExamine[] customExamines = {
        new ItemExamine(30050, "A pair of ten-sided dice, for results between 1 and 100.")
    };

    public static void main(String[] args) throws Exception {
        val is = new URL("https://www.osrsbox.com/osrsbox-db/items-complete.json").openStream();
        val br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        final Map<String, Object> map = new Gson().fromJson(br, new TypeToken<HashMap<String, Object>>() {}.getType());
        val list = new ArrayList<ItemExamine>();
        map.forEach((k, v) -> {
            val id = Integer.parseInt(k);
            val innerMap = (LinkedTreeMap<String, Object>) v;
            list.add(new ItemExamine(id, (String) innerMap.get("examine")));
        });
        Collections.addAll(list, customExamines);
        list.removeIf(Objects::isNull);
        list.removeIf(summary -> summary.getExamine() == null || summary.getExamine().isEmpty());
        list.sort(Comparator.comparingInt(summary -> summary.getId()));
        val bw = new FileWriter("data/examines/#" + Constants.REVISION + "-item-examines.json");
        GSON.toJson(list, bw);
        bw.close();
    }
}
