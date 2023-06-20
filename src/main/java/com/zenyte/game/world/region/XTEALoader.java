package com.zenyte.game.world.region;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Kris | 7. jaan 2018 : 21:44.02
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class XTEALoader {

	public static final Map<Integer, XTEA> DEFINITIONS = new HashMap<Integer, XTEA>();
	private static final Gson GSON = new Gson();
	private static final int[] DEFAULT_KEYS = new int[4];

    public static void load() throws FileNotFoundException {
        val br = new BufferedReader(new FileReader("data/objects/xteas.json"));
        val xteas = GSON.fromJson(br, XTEA[].class);
        for (val xtea : xteas) {
            if (xtea == null) {
				continue;
			}
            DEFINITIONS.put(xtea.getRegion(), xtea);
        }
    }

	public static void loadNewRevision() throws FileNotFoundException {
		val br = new BufferedReader(new FileReader("data/objects/xteas 201.json"));
		val xteas = GSON.fromJson(br, XTEA[].class);
		for (val xtea : xteas) {
			if (xtea == null) {
				continue;
			}
			DEFINITIONS.put(xtea.getRegion(), xtea);
		}
	}

    /**
     * Gets the default xtea keys of {0, 0, 0, 0} or
	 * the correct keys for the home area (egdeville)
	 * as we repack the maps upon cache update.
     * @param region
     * @return
     */
	public static final int[] getXTEAs(final int region) {
		if (region == 12342) {
			return getXTEAKeys(region);
		}
		return DEFAULT_KEYS;
	}

    /**
     * Gets the actual xtea keys.
     * @param regionId
     * @return
     */
	public static int[] getXTEAKeys(final int regionId) {
        val xtea = DEFINITIONS.get(regionId);
        if (xtea == null) {
			return DEFAULT_KEYS;
		}
        return xtea.getKeys();
    }
	
	
	public static void save() {
		final File saveFile = new File("filtered xteas.json");
		
		final Map<Integer, XTEA> xteas = new TreeMap<Integer, XTEA>();
		DEFINITIONS.forEach((k, v) -> {
			if (v.getKeys()[0] != 0 && v.getKeys()[1] != 0 && v.getKeys()[2] != 0 && v.getKeys()[3] != 0) {
				xteas.put(k, v);
			}
		});
		
		final String toJson = new GsonBuilder().setPrettyPrinting().create().toJson(xteas.values());
		PrintWriter writer;
		try {
			writer = new PrintWriter(saveFile, "UTF-8");
			writer.println(toJson);
			writer.close();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}
	
}
