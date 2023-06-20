package com.zenyte.game.content.grandexchange;

import com.zenyte.Constants;
import com.zenyte.api.client.query.SendItemPrices;
import com.zenyte.game.parser.Parse;
import com.zenyte.game.world.World;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;

@Slf4j
public class JSONGEItemDefinitionsLoader implements Parse {
    public static final Int2ObjectMap<JSONGEItemDefinitions> definitions = new Int2ObjectOpenHashMap<>();

    @Override
    public void parse() throws Throwable {
        val br = new BufferedReader(new FileReader("data/grandexchange/prices.json"));
        val priceDefinitions = World.getGson().fromJson(br, JSONGEItemDefinitions[].class);
        for (val def : priceDefinitions) {
            if (def == null) {
                continue;
            }
            definitions.put(def.getId(), def);
        }
    }
    
    public static final void main(String[] args) {
    	try {
			new JSONGEItemDefinitionsLoader().parse();
			val reader = new BufferedReader(new FileReader(new File("newprices.txt")));
			String line;
			while((line = reader.readLine()) != null) {
			    val split = line.split("\t");
			    val id = Integer.parseInt(split[0].trim());
			    val price = Integer.parseInt(split[1].replaceAll("[,.]", "").trim());
                var definition = lookup(id);
                if (definition == null) {
                    definition = new JSONGEItemDefinitions();
                    definition.setId(id);
                    definition.setTime(Instant.now());
                }
                definition.setPrice(price);
			}
			save();
		} catch (Throwable e) {
            log.error(Strings.EMPTY, e);
		}
    }

    public static final void save() {
        val list = new ArrayList<>(definitions.values());
        list.sort(Comparator.comparingInt(JSONGEItemDefinitions::getId));
        final String toJson = World.getGson().toJson(list);
        try {
            final PrintWriter pw = new PrintWriter("data/grandexchange/prices.json", "UTF-8");
            pw.println(toJson);
            pw.close();
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
        if (Constants.WORLD_PROFILE.isDevelopment() || Constants.WORLD_PROFILE.isPrivate() || Constants.WORLD_PROFILE.isBeta()) {
            return;
        }
        new SendItemPrices(list).execute();
    }

    /**
     * Looks up a definition based on the key value in the map.
     *
     * @param itemId the key value we using to search for the respective
     *               definition.
     * @return
     */
    public static JSONGEItemDefinitions lookup(int itemId) {
        return definitions.get(itemId);
    }

}
