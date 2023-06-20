package com.zenyte.game.world.entity.player;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonDeserializer;
import com.zenyte.Game;
import com.zenyte.plugins.events.InitializationEvent;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.types.config.VarbitDefinitions;

import java.util.Objects;
import java.util.function.IntConsumer;

/**
 * @author Kris | 25. jaan 2018 : 23:59.25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@SuppressWarnings("all")
public class VarManager {

    /**
     * A set of varps that are to be saved on the character file.
     * Set is populated from across the source, generally through enums or other similar collections.
     */
    public static final IntSet persistentVars = new IntOpenHashSet();

    public static final void appendPersistentVarp(final int varp) {
        persistentVars.add(varp);
    }

    public static final void appendPersistentVarbit(final int varbit) {
        VarbitDefinitions.findVarp(varbit).ifPresent((IntConsumer) varp -> persistentVars.add(varp));
    }

    private static final int[] MASK_LOOKUP = new int[32];

    static {
        int i = 2;
        for (int i2 = 0; i2 < 32; i2++) {
            MASK_LOOKUP[i2] = i - 1;
            i += i;
        }
    }

    private transient final int[] values;
    private transient final Player player;

    public static final JsonDeserializer<VarManager> deserializer() {
        return (json, typeOfT, context) -> {
            assert json.isJsonObject();
            val elementsMap = Objects.requireNonNull(json.getAsJsonObject().get("serializedVars"));
            assert elementsMap.isJsonObject();
            val manager = new VarManager(null);
            elementsMap.getAsJsonObject().entrySet().forEach(element -> {
                val key = Integer.parseInt(element.getKey());
                val value = Integer.parseInt(element.getValue().getAsString());
                manager.serializedVars.put(key, value);
            });
            return manager;
        };
    }

    @Getter private Int2IntMap serializedVars = new Int2IntOpenHashMap();

    @Subscribe
    public static final void onInitialization(final InitializationEvent event) {
        val player = event.getPlayer();
        val saved = event.getSavedPlayer();

        if (saved == null || saved.getVarManager() == null || saved.getVarManager().serializedVars == null) {
            return;
        }
        player.getVarManager().serializedVars.putAll(saved.getVarManager().serializedVars);
    }

    public final void refreshSerializedVars() {
        for (val entry : serializedVars.int2IntEntrySet()) {
            sendVar(entry.getIntKey(), entry.getIntValue());
        }
    }

    VarManager(final Player player) {
        this.player = player;
        values = new int[Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.VARBIT).getHighestFileId() + 1];
    }

    public void sendVar(final Varp varp, final int value) {
        if (varp instanceof Varbit) {
            setBit(varp.getId(), value);
        } else {
            setVar(varp.getId(), value);
        }
    }

    public void sendVar(final Varp varp, final boolean value) {
        if (varp instanceof Varbit) {
            setBit(varp.getId(), value ? 1 : 0);
        } else {
            setVar(varp.getId(), value ? 1 : 0);
        }
    }

    public void sendVarInstant(final int id, final int value) {
        if (id == -1) {
            return;
        }
        values[id] = value;
        if (persistentVars.contains(id)) {
            serializedVars.put(id, value);
        }
        player.getPacketDispatcher().sendConfig(id, value);
    }

    public void sendVar(final int id, final int value) {
        setVar(id, value);
    }

    private void setVar(final int id, final int value) {
        if (id == -1) {
            return;
        }
        values[id] = value;
        if (persistentVars.contains(id)) {
            serializedVars.put(id, value);
        }
        player.getPendingVars().add(id);
    }

    public int getValue(final int id) {
        return values[id];
    }


    public void incrementBit(final int id, final int amount) {
        sendBit(id, getBitValue(id) + amount);
    }

    public void decrementBit(final int id) {
        val result = getBitValue(id) - 1;
        sendBit(id, result < 0 ? 0 : result);
    }

    public void sendBit(final int id, final int value) {
        setBit(id, value);
    }

    public void sendBit(final int id, final boolean value) {
        setBit(id, value ? 1 : 0);
    }

    public int getBitValue(final int id) {
        final VarbitDefinitions defs = VarbitDefinitions.get(id);
        if (defs == null) {
            return 0;
        }
        return values[defs.getBaseVar()] >> defs.getStartBit() & MASK_LOOKUP[defs.getEndBit() - defs.getStartBit()];
    }

    private void setBit(final int id, int value) {
        if (id == -1) {
            return;
        }
        final VarbitDefinitions defs = VarbitDefinitions.get(id);
        if (defs == null) {
            return;
        }
        int mask = MASK_LOOKUP[defs.getEndBit() - defs.getStartBit()];
        if (value < 0 || value > mask) {
            value = 0;
        }
        mask <<= defs.getStartBit();
        final int varpValue = (values[defs.getBaseVar()] & (~mask) | value << defs.getStartBit() & mask);
        setVar(defs.getBaseVar(), varpValue);
    }
}