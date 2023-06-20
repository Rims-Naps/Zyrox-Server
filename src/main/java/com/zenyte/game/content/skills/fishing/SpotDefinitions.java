package com.zenyte.game.content.skills.fishing;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.zenyte.game.content.skills.fishing.FishingTool.*;
import static com.zenyte.game.world.entity.npc.NpcId.*;

/**
 * @author Kris | 04/03/2019 22:56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
public enum SpotDefinitions {

    NET(870330, new int[] { FISHING_SPOT_1530, FISHING_SPOT_7947, FISHING_SPOT_1544, FISHING_SPOT_1517, FISHING_SPOT_1514, FISHING_SPOT_1518, FISHING_SPOT_1521, FISHING_SPOT_1523, FISHING_SPOT_1524, FISHING_SPOT_1532, FISHING_SPOT_1525, FISHING_SPOT_7155, FISHING_SPOT_1528, FISHING_SPOT_7467, FISHING_SPOT_7462, FISHING_SPOT_1517, FISHING_SPOT_3913,
                            FISHING_SPOT_1530, FISHING_SPOT_7459, ROD_FISHING_SPOT_7468, FISHING_SPOT_7469, CORSAIR_TRAITOR_HARD }, new String[] { "Net", "Small Net" }, SMALL_FISHING_NET,
            FishDefinitions.SHRIMPS, FishDefinitions.ANCHOVIES),

    BASIC_BAIT(1056000, new int[] { FISHING_SPOT_7947, FISHING_SPOT_1544, FISHING_SPOT_1514, FISHING_SPOT_1517, FISHING_SPOT_1518, FISHING_SPOT_1532, FISHING_SPOT_1521, FISHING_SPOT_1523, FISHING_SPOT_1524, FISHING_SPOT_1525, FISHING_SPOT_7155, FISHING_SPOT_1528, FISHING_SPOT_7467, FISHING_SPOT_7462, FISHING_SPOT_1517,
                                    FISHING_SPOT_3913, FISHING_SPOT_1530, FISHING_SPOT_7459, ROD_FISHING_SPOT_7468, FISHING_SPOT_7469, CORSAIR_TRAITOR_HARD }, new String[] { "Bait" }, FISHING_ROD, FishingBait.FISHING_BAIT,
            FishDefinitions.SARDINE, FishDefinitions.HERRING),

    BRAINDEATH(-1, new int[] { FISHING_SPOT }, new String[] { "Fish" }, SMALL_FISHING_NET,
            FishDefinitions.SEA_SLUG, FishDefinitions.KARAMTHULU),

    RIVER_LURE(923616, new int[] { ROD_FISHING_SPOT_1512, ROD_FISHING_SPOT_3418, ROD_FISHING_SPOT_1529, ROD_FISHING_SPOT_1531, ROD_FISHING_SPOT_1513, ROD_FISHING_SPOT_1507, ROD_FISHING_SPOT_1527, ROD_FISHING_SPOT_1515, ROD_FISHING_SPOT, ROD_FISHING_SPOT_1508, ROD_FISHING_SPOT_1509, ROD_FISHING_SPOT_1516, ROD_FISHING_SPOT_1526, ROD_FISHING_SPOT_3417, ROD_FISHING_SPOT_7468, ROD_FISHING_SPOT_8524 },
            new String[] { "Lure" }, FLY_FISHING_ROD, FishingBait.FEATHER,
            FishDefinitions.TROUT, FishDefinitions.SALMON),

    RIVER_BAIT(305792, new int[] { ROD_FISHING_SPOT_1512, ROD_FISHING_SPOT_1513, ROD_FISHING_SPOT, ROD_FISHING_SPOT_1529, ROD_FISHING_SPOT_1531, ROD_FISHING_SPOT_3418, ROD_FISHING_SPOT_1515, ROD_FISHING_SPOT_1508, ROD_FISHING_SPOT_1507, ROD_FISHING_SPOT_1527, ROD_FISHING_SPOT_1509, ROD_FISHING_SPOT_1516, ROD_FISHING_SPOT_1526, ROD_FISHING_SPOT_3417 },
            new String[] { "Bait" }, FISHING_ROD, FishingBait.FISHING_BAIT, FishDefinitions.PIKE),

    CAGE(116129, new int[] { FISHING_SPOT_1533, FISHING_SPOT_1510, FISHING_SPOT_2146, FISHING_SPOT_1519, FISHING_SPOT_1522, FISHING_SPOT_7460, FISHING_SPOT_7465, FISHING_SPOT_7199, FISHING_SPOT_3914, FISHING_SPOT_3657, FISHING_SPOT_1535, FISHING_SPOT_7470, FISHING_SPOT_5820 },
            new String[] { "Cage" }, LOBSTER_POT, FishDefinitions.LOBSTER),

    BASIC_HARPOON(257770, new int[] { FISHING_SPOT_1533, FISHING_SPOT_2146, FISHING_SPOT_1510, FISHING_SPOT_1519, FISHING_SPOT_1522, FISHING_SPOT_7460, FISHING_SPOT_7465, FISHING_SPOT_7199, FISHING_SPOT_3914, FISHING_SPOT_3657, FISHING_SPOT_7470, FISHING_SPOT_5820, FISHING_SPOT_4316 },
            new String[] { "Harpoon" }, HARPOON, FishDefinitions.TUNA, FishDefinitions.SWORDFISH),

    KARAMBWAN(170874, new int[] { FISHING_SPOT_4712, FISHING_SPOT_4713, FISHING_SPOT_4714 }, new String[] { "Fish" }, KARAMBWAN_VESSEL,
            FishingBait.RAW_KARAMBWANJI, FishDefinitions.KARAMBWAN),

    KARAMBWANJI(443697, new int[] { FISHING_SPOT_4710 }, new String[] { "Net" }, SMALL_FISHING_NET, FishDefinitions.KARAMBWANJI),

    FROG_SPAWN(-1, new int[] { FISHING_SPOT_1499, FISHING_SPOT_1500, FISHING_SPOT_1497, FISHING_SPOT_1498 }, new String[] { "Net", "Small Net" },
            SMALL_FISHING_NET, FishDefinitions.FROG_SPAWN),

    SLIMY_AND_CAVE_EEL(-1, new int[] { FISHING_SPOT_1497, FISHING_SPOT_1498, FISHING_SPOT_1499, FISHING_SPOT_1500 }, new String[] { "Bait" }, FISHING_ROD,
            FishingBait.FISHING_BAIT, FishDefinitions.SLIMY_EEL, FishDefinitions.CAVE_EEL),

    SLIMY_EEL(-1, new int[] { FISHING_SPOT_2653, FISHING_SPOT_2654, FISHING_SPOT_2655 }, new String[] { "Bait" }, FISHING_ROD,
            FishingBait.FISHING_BAIT, FishDefinitions.SLIMY_EEL),

    LAVA_EEL(-1, new int[] { FISHING_SPOT_4928 }, new String[] { "Bait" }, OILY_FISHING_ROD, FishingBait.FISHING_BAIT, FishDefinitions.LAVA_EEL),

    INFERNAL_EEL(165000, new int[] {ROD_FISHING_SPOT_7676}, new String[] {"Bait"}, OILY_FISHING_ROD, FishingBait.FISHING_BAIT, FishDefinitions.INFERNAL_EEL),

    SACRED_EEL(99000, new int[] { FISHING_SPOT_6488 }, new String[] { "Bait" }, FISHING_ROD,
            FishingBait.FISHING_BAIT, FishDefinitions.SACRED_EEL),

    MONKFISH(138583, new int[] { FISHING_SPOT_4316 }, new String[] { "Net" }, SMALL_FISHING_NET, FishDefinitions.MONKFISH),

    MINNOW(-1, new int[] { FISHING_SPOT_7730, FISHING_SPOT_7731, FISHING_SPOT_7732, FISHING_SPOT_7733 }, new String[] { "Small Net" }, SMALL_FISHING_NET, FishDefinitions.MINNOW),

    BARBARIAN_FISH(1280862, new int[] { FISHING_SPOT_1542, FISHING_SPOT_7323 }, new String[] { "Use-rod" }, BARBARIAN_ROD,
            new FishingBait[] { FishingBait.ROE, FishingBait.FISH_OFFCUTS, FishingBait.CAVIAR, FishingBait.FEATHER, FishingBait.FISHING_BAIT },
            FishDefinitions.LEAPING_TROUT, FishDefinitions.LEAPING_SALMON, FishDefinitions.LEAPING_STURGEON),

    BIG_NET(1147827, new int[] { FISHING_SPOT_1511, FISHING_SPOT_5821, FISHING_SPOT_1534, FISHING_SPOT_1520, FISHING_SPOT_7200, FISHING_SPOT_7466, FISHING_SPOT_3915, FISHING_SPOT_7461, FISHING_SPOT_4477, FISHING_SPOT_4476, FISHING_SPOT_5233, FISHING_SPOT_3419 },
            new String[] { "Net", "Big Net" }, BIG_FISHING_NET,
            FishDefinitions.MACKERAL, FishDefinitions.COD, FishDefinitions.BASS),

    SHARK_HARPOON(82243, new int[] { FISHING_SPOT_1511, FISHING_SPOT_1534, FISHING_SPOT_7200, FISHING_SPOT_1520, FISHING_SPOT_7466, FISHING_SPOT_3915, FISHING_SPOT_7461, FISHING_SPOT_4477, FISHING_SPOT_4476, FISHING_SPOT_5233, FISHING_SPOT_3419 },
            new String[] { "Harpoon" }, HARPOON, FishDefinitions.SHARK),

    ANGLERFISH(78649, new int[] { ROD_FISHING_SPOT_6825 }, new String[] { "Bait" }, FISHING_ROD, FishingBait.SANDWORMS, FishDefinitions.ANGLERFISH),

    DARK_CRAB(149434, new int[] { FISHING_SPOT_1536 }, new String[] { "Cage" }, LOBSTER_POT,
            FishingBait.DARK_FISHING_BAIT, FishDefinitions.DARK_CRAB),

    CRYSTAL_AREA(1147827, new int[] { FISHING_SPOT_3419 },
            new String[] { "Big Net" }, BIG_FISHING_NET,
            FishDefinitions.MANTA_RAY, FishDefinitions.MANTA_RAY);

    public static final SpotDefinitions[] values = values();
    private static final Map<String, SpotDefinitions> map = new Object2ObjectOpenHashMap<>();
    @Getter
    private static final IntOpenHashSet npcs = new IntOpenHashSet();

    static {
        for (val spot : values) {
            for (val id : spot.npcIds) {
                npcs.add(id);
                for (val action : spot.actions) {
                    map.put(id + "|" + action, spot);
                }
            }
        }
    }

    public static final SpotDefinitions get(final String query) {
        return map.get(query);
    }

    private final int baseClueBottleChance;
    private final int[] npcIds;
    private final String[] actions;
    private final FishingTool tool;
    private final FishingBait[] bait;
    private final FishDefinitions[] fish;
    private final FishDefinitions lowestTierFish;

    SpotDefinitions(final int baseClueBottleChance, final int[] npcIds, final String[] actions, final FishingTool tool, final FishDefinitions... fish) {
        this(baseClueBottleChance, npcIds, actions, tool, (FishingBait[]) null, fish);
    }

    SpotDefinitions(final int baseClueBottleChance, final int[] npcIds, final String[] actions, final FishingTool tool, final FishingBait bait,
                    final FishDefinitions... fish) {
        this(baseClueBottleChance, npcIds, actions, tool, new FishingBait[] { bait }, fish);
    }

    SpotDefinitions(final int baseClueBottleChance, final int[] npcIds, final String[] actions, final FishingTool tool, final FishingBait[] bait,
                    final FishDefinitions... fish) {
        this.baseClueBottleChance = baseClueBottleChance;
        this.npcIds = npcIds;
        this.actions = actions;
        this.tool = tool;
        this.bait = bait;
        this.fish = fish;
        this.lowestTierFish = calculateLowestTierFish();
    }

    @NotNull
    private FishDefinitions calculateLowestTierFish() {
        FishDefinitions lowestTierFish = null;
        for (val f : fish) {
            if (lowestTierFish == null || lowestTierFish.getLevel() > f.getLevel()) {
                lowestTierFish = f;
            }
        }
        assert lowestTierFish != null;
        return lowestTierFish;
    }
}
