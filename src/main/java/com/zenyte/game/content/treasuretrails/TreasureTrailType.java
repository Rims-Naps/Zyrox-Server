package com.zenyte.game.content.treasuretrails;

import com.zenyte.game.content.treasuretrails.clues.*;
import com.zenyte.game.util.Utils;
import com.zenyte.utils.StaticInitializer;
import it.unimi.dsi.fastutil.objects.*;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Set;

/**
 * @author Kris | 06/04/2019 17:40
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
@StaticInitializer
public enum TreasureTrailType {

    ANAGRAM(Anagram.class),
    CIPHER(CipherClue.class),
    COORDINATE(CoordinateClue.class),
    CRYPTIC(CrypticClue.class),
    EMOTE(EmoteClue.class),
    MAP(MapClue.class),
    HOT_COLD(HotColdClue.class),
    SHERLOCK(SherlockTask.class),
    MUSIC(MusicClue.class),
    CHARLIE(CharlieTask.class),
    FALO_THE_BARD(FaloTheBardClue.class);

    private final Class<? extends Enum<? extends Clue>> internalEnum;
    private final Enum<? extends Clue>[] constants;
    private final Object2IntOpenHashMap<ClueLevel> weights;
    private final Object2ObjectOpenHashMap<ClueLevel, Set<Clue>> clues;

    private static final Object2IntOpenHashMap<ClueLevel> totalWeights;
    private static final EnumMap<ClueLevel, EnumMap<TreasureTrailType, Set<Clue>>> allClues;

    private static final Object2ObjectMap<String, Clue> namedClues;

    public static final Object2ObjectMap<String, Clue> getNamedClues() {
        return namedClues;
    }

    TreasureTrailType(final Class<? extends Enum<? extends Clue>> e) {
        this.internalEnum = e;
        this.constants = internalEnum.getEnumConstants();
        weights = new Object2IntOpenHashMap<>(ClueLevel.values.length);
        this.clues = new Object2ObjectOpenHashMap<>();
        int weight;
        for (val level : ClueLevel.values) {
            weight = 0;
            for (val constant : constants) {
                val clue = ((Clue) constant);
                val constantLevel = clue.level();
                if (constantLevel == level) {
                    weight++;
                }
                clues.computeIfAbsent(clue.level(), f -> new ObjectOpenHashSet<>()).add(clue);
            }
            weights.put(level, weight);
        }
    }

    static TreasureTrailType[] values = values();

    static {
        totalWeights = new Object2IntOpenHashMap<>();
        allClues = new EnumMap<>(ClueLevel.class);
        val labelledClues = new Object2ObjectOpenHashMap<String, Clue>();
        for (val value : values) {
            for (val weightMap : value.weights.object2IntEntrySet()) {
                val level = weightMap.getKey();
                totalWeights.put(level, totalWeights.getInt(level) + weightMap.getIntValue());
                val values = value.clues.get(level);
                if (values != null) {
                    for (val clue : values) {
                        val name = clue.getEnumName();
                        if (labelledClues.containsKey(name)) {
                            System.err.println("Overlapping clue: " + name + ", " + clue.toString());
                        }
                        labelledClues.put(name, clue);
                    }
                    allClues.computeIfAbsent(level, __ -> new EnumMap<>(TreasureTrailType.class))
                            .computeIfAbsent(value, __ -> new ObjectOpenHashSet<>()).addAll(values);
                }
            }
        }
        namedClues = Object2ObjectMaps.unmodifiable(labelledClues);
    }

    @NotNull
    public static final Clue random(@NotNull final ClueLevel level) {
        val typeMap = allClues.get(level);
        val randomEntry = Utils.getRandomCollectionElement(typeMap.keySet());
        val entries = typeMap.get(randomEntry);
        return Utils.getRandomCollectionElement(entries);
    }

}
