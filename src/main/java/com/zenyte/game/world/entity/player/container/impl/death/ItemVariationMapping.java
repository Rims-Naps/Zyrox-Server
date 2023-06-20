package com.zenyte.game.world.entity.player.container.impl.death;

import com.google.gson.reflect.TypeToken;
import com.zenyte.game.parser.scheduled.ScheduledExternalizable;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Kris | 19/06/2019 12:08
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class ItemVariationMapping implements ScheduledExternalizable {
    private static final Int2IntMap MAPPINGS = new Int2IntOpenHashMap();

    /**
     * Get base item id for provided variation item id.
     *
     * @param itemId the item id
     * @return the base item id
     */
    public static int map(int itemId)
    {
        return MAPPINGS.getOrDefault(itemId, itemId);
    }

    @Override
    public int writeInterval() {
        return -1;
    }

    @Override
    public void read(final BufferedReader reader) {
        try {
            final TypeToken<Map<String, Collection<Integer>>> typeToken = new TypeToken<Map<String, Collection<Integer>>>() {
            };

            final InputStream geLimitData = new FileInputStream(path());
            final Map<String, Collection<Integer>> itemVariations = gson.fromJson(new InputStreamReader(geLimitData), typeToken.getType());

            for (Collection<Integer> value : itemVariations.values()) {
                final Iterator<Integer> iterator = value.iterator();
                final int base = iterator.next();

                while (iterator.hasNext()) {
                    MAPPINGS.put(iterator.next().intValue(), base);
                }
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    @Override
    public void write() {

    }

    @Override
    public String path() {
        return "data/item_variations.json";
    }
}
