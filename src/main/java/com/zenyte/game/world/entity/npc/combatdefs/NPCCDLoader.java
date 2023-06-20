package com.zenyte.game.world.entity.npc.combatdefs;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Kris | 18/11/2018 02:58
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class NPCCDLoader {

    public static Int2ObjectOpenHashMap<NPCCombatDefinitions> definitions;

    public static void parse() {
        try {
            val br = new BufferedReader(new FileReader("data/npcs/combatDefs.json"));
            val array = World.getGson().fromJson(br, NPCCombatDefinitions[].class);
            Utils.populateMap(array, definitions = new Int2ObjectOpenHashMap<>(array.length), NPCCombatDefinitions::getId);
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public static void save() {
        val defs = new ArrayList<>(definitions.values());
        defs.sort(Comparator.comparingInt(NPCCombatDefinitions::getId));
        final String toJson = World.getGson().toJson(defs);
        try {
            final PrintWriter pw = new PrintWriter("data/npcs/combatDefs.json", "UTF-8");
            pw.println(toJson);
            pw.close();
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public static NPCCombatDefinitions get(final int id) {
        return definitions.get(id);
    }

}
