package com.zenyte.game.parser.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zenyte.game.parser.Parse;
import lombok.val;
import mgi.types.config.items.JSONItemDefinitions;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

public class JSONItemDefinitionsLoader implements Parse {
	
    public static final Map<Integer, JSONItemDefinitions> DEFINITIONS = new HashMap<>();

    private static final Gson GSON = new Gson();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JSONItemDefinitionsLoader.class);

    @Override
    public void parse() throws Throwable {
        val reader = new BufferedReader(new FileReader("data/items/ItemDefinitions.json"));
        val definitions = GSON.fromJson(reader, JSONItemDefinitions[].class);
        for (final JSONItemDefinitions def : definitions) {
            if (def != null) {
                DEFINITIONS.put(def.getId(), def);
            }
        }
    }
    
    public static final void main(final String[] args) {
    	try {
			new JSONItemDefinitionsLoader().parse();
		} catch (final Throwable e) {
            log.error(Strings.EMPTY, e);
		}
    }

    /**
     * Looks up a definition based on the key value in the map.
     *
     * @param itemId the key value we using to search for the respective
     *               definition.
     * @return
     */
    public static JSONItemDefinitions lookup(final int itemId) {
        return getDefinitions().get(itemId);
    }

    /**
     * Gets the definitions map.
     *
     * @return
     */
    public static Map<Integer, JSONItemDefinitions> getDefinitions() {
        return DEFINITIONS;
    }
    
    public static final void save() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final Collection<JSONItemDefinitions> values = DEFINITIONS.values();
		final Comparator<JSONItemDefinitions> comparator = (npc1, npc2) -> {
			if (npc1 == null || npc2 == null) {
				return 0;
			}
			return npc1.getId() > npc2.getId() ? 1 : -1;
		};
		final List<JSONItemDefinitions> list = new ArrayList<JSONItemDefinitions>(values);
		Collections.sort(list, comparator);
        final String toJson = gson.toJson(list);
        try {
            final PrintWriter pw = new PrintWriter("data/items/ItemDefinitions.json", "UTF-8");
            pw.println(toJson);
            pw.close();
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }
    
}
