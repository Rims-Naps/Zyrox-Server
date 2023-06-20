package com.zenyte.game.content.theatreofblood.boss.nylocas.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static com.zenyte.game.content.theatreofblood.boss.nylocas.model.NylocasType.*;
import static com.zenyte.game.content.theatreofblood.boss.nylocas.model.PillarLocation.*;

/**
 * @author Tommeh | 6/7/2020 | 3:55 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
@RequiredArgsConstructor
public enum WaveDefinition {

    WAVE_1(1, Segment.of(Spawn.of(MELEE).target(NORTH_WEST)),
            Segment.of(Spawn.of(MAGIC).target(NORTH_EAST)),
            Segment.of(Spawn.of(RANGED).aggressive())),

    WAVE_2(2, Segment.of(Spawn.of(RANGED).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MELEE).aggressive()),
            Segment.of(Spawn.of(MAGIC).target(NORTH_EAST))
    ),

    WAVE_3(3, Segment.of(Spawn.of(MAGIC).aggressive()),
            Segment.of(Spawn.of(RANGED).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MELEE).target(NORTH_EAST))
    ),

    WAVE_4(4, Segment.of(Spawn.of(MELEE).target(NORTH_EAST)),
            Segment.of(Spawn.of(MAGIC).large().target(SOUTH_WEST)),
            Segment.of(Spawn.of(RANGED).target(NORTH_WEST))
    ),

    WAVE_5(5, Segment.of(Spawn.of(MAGIC).target(NORTH_EAST)),
            Segment.of(Spawn.of(MELEE).target(SOUTH_EAST)),
            Segment.of(Spawn.of(RANGED).large().target(SOUTH_WEST))
    ),

    WAVE_6(6, Segment.of(Spawn.of(MELEE).large().target(SOUTH_EAST)),
            Segment.of(Spawn.of(RANGED).target(NORTH_WEST)),
            Segment.of(Spawn.of(MAGIC).target(NORTH_EAST))
    ),

    WAVE_7(7, Segment.of(Spawn.of(MELEE).target(NORTH_WEST)),
            Segment.of(Spawn.of(RANGED).large().aggressive(), Spawn.of(MAGIC).target(NORTH_EAST)),
            Segment.of()
    ),

    WAVE_8(8, Segment.of(Spawn.of(RANGED).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MELEE).target(NORTH_WEST)),
            Segment.of(Spawn.of(MAGIC).large().aggressive())
    ),

    WAVE_9(9, Segment.of(Spawn.of(MAGIC).target(NORTH_WEST)),
            Segment.of(),
            Segment.of(Spawn.of(RANGED).large().aggressive(), Spawn.of(MELEE).target(NORTH_EAST))
    ),

    WAVE_10(10, Segment.of(Spawn.of(RANGED).large().target(NORTH_EAST), Spawn.of(RANGED).aggressive()),
            Segment.of(Spawn.of(RANGED).target(SOUTH_WEST), Spawn.of(RANGED).target(SOUTH_EAST)),
            Segment.of(Spawn.of(RANGED).target(NORTH_WEST), Spawn.of(RANGED).aggressive())
    ),

    WAVE_11(11, Segment.of(Spawn.of(MAGIC).target(SOUTH_EAST), Spawn.of(MAGIC).aggressive()),
            Segment.of(Spawn.of(MAGIC).target(NORTH_WEST), Spawn.of(MAGIC).target(NORTH_EAST)),
            Segment.of(Spawn.of(MAGIC).aggressive())
    ),

    WAVE_12(12, Segment.of(Spawn.of(MELEE).target(NORTH_EAST), Spawn.of(MELEE).aggressive()),
            Segment.of(Spawn.of(MELEE).large().target(SOUTH_WEST)),
            Segment.of(Spawn.of(MELEE).target(NORTH_WEST), Spawn.of(MELEE).aggressive())
    ),

    WAVE_13(13, Segment.of(Spawn.of(MELEE).large().aggressive()),
            Segment.of(Spawn.of(RANGED).target(SOUTH_WEST), Spawn.of(MELEE).target(SOUTH_EAST)),
            Segment.of(Spawn.of(RANGED).target(NORTH_WEST), Spawn.of(MAGIC).aggressive())
    ),

    WAVE_14(14, Segment.of(Spawn.of(RANGED).large().aggressive()),
            Segment.of(Spawn.of(RANGED).target(SOUTH_WEST), Spawn.of(MAGIC).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MAGIC).target(NORTH_WEST), Spawn.of(MELEE).aggressive())
    ),

    WAVE_15(15, Segment.of(Spawn.of(RANGED).target(NORTH_EAST), Spawn.of(MAGIC).aggressive()),
            Segment.of(Spawn.of(MAGIC).large().target(SOUTH_WEST)),
            Segment.of(Spawn.of(MELEE).target(NORTH_WEST), Spawn.of(RANGED).aggressive())
    ),

    WAVE_16(16, Segment.of(Spawn.of(MAGIC).transformations(MELEE, RANGED).target(NORTH_EAST)),
            Segment.of(Spawn.of(MELEE).transformations(MAGIC, RANGED).target(SOUTH_WEST)),
            Segment.of(Spawn.of(RANGED).transformations(MAGIC, RANGED).target(NORTH_WEST))
    ),

    WAVE_17(17, Segment.of(Spawn.of(MAGIC).large().transformations(MELEE, MAGIC).target(NORTH_EAST)),
            Segment.of(Spawn.of(MAGIC).large().transformations(MELEE, MAGIC).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MAGIC).large().transformations(MELEE, MAGIC).target(SOUTH_WEST))
    ),

    WAVE_18(18, Segment.of(Spawn.of(RANGED).large().transformations(MAGIC, RANGED).target(NORTH_WEST)),
            Segment.of(Spawn.of(RANGED).large().transformations(MAGIC, RANGED).target(SOUTH_WEST)),
            Segment.of(Spawn.of(RANGED).large().aggressive().transformations(MAGIC, RANGED))
    ),

    WAVE_19(19, Segment.of(Spawn.of(MAGIC).large().aggressive().transformations(MELEE, MAGIC)),
            Segment.of(Spawn.of(MAGIC).large().transformations(MELEE, MAGIC).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MAGIC).large().transformations(MELEE, MAGIC).target(NORTH_WEST))
    ),

    WAVE_20(20, Segment.of(Spawn.of(MELEE).large().transformations(RANGED, MELEE).target(NORTH_EAST)),
            Segment.of(Spawn.of(MAGIC).large().aggressive().transformations(RANGED, MELEE)),
            Segment.of(Spawn.of(MELEE).large().transformations(RANGED, MELEE).target(NORTH_WEST))
    ),

    WAVE_21(21, Segment.of(Spawn.of(RANGED).transformations(MELEE, RANGED).target(NORTH_EAST), Spawn.of(RANGED).aggressive().transformations(MELEE, RANGED)),
            Segment.of(Spawn.of(MELEE).transformations(RANGED, MELEE).aggressive(), Spawn.of(MELEE).transformations(MAGIC, MELEE).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MAGIC).transformations(RANGED, MAGIC).target(NORTH_WEST), Spawn.of(MAGIC).transformations(MELEE, RANGED).target(SOUTH_WEST))
    ),

    WAVE_22(22, Segment.of(Spawn.of(MAGIC).aggressive().transformations(RANGED, MELEE)),
            Segment.of(Spawn.of(MAGIC).transformations(RANGED, MELEE).target(SOUTH_WEST), Spawn.of(RANGED).transformations(MAGIC, MELEE).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MELEE).large().transformations(RANGED, MELEE).target(SOUTH_WEST))
    ),

    WAVE_23(23, Segment.of(Spawn.of(MAGIC).large().transformations(RANGED, MELEE).target(NORTH_EAST)),
            Segment.of(Spawn.of(RANGED).large().aggressive().transformations(MAGIC, MELEE)),
            Segment.of(Spawn.of(MAGIC).transformations(RANGED, MELEE).target(NORTH_WEST), Spawn.of(RANGED).transformations(MAGIC, RANGED).target(SOUTH_WEST))
    ),

    WAVE_24(24, Segment.of(Spawn.of(MELEE).large().aggressive()),
            Segment.of(Spawn.of(MAGIC).large().aggressive()),
            Segment.of(Spawn.of(RANGED).large().aggressive().transformations(MAGIC, MELEE))
    ),

    WAVE_25(25, Segment.of(Spawn.of(MAGIC).large().transformations(MELEE, MAGIC).target(SOUTH_EAST)),
            Segment.of(Spawn.of(RANGED).large().target(SOUTH_WEST)),
            Segment.of(Spawn.of(MELEE).large().aggressive())
    ),

    WAVE_26(26, Segment.of(Spawn.of(MAGIC).large().target(NORTH_EAST)),
            Segment.of(Spawn.of(MELEE).large().transformations(MAGIC, MELEE).target(SOUTH_WEST)),
            Segment.of(Spawn.of(MAGIC).large().aggressive())
    ),

    WAVE_27(27, Segment.of(Spawn.of(MAGIC).large().transformations(MELEE, MAGIC).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MELEE).large().aggressive().transformations(MAGIC, RANGED)),
            Segment.of(Spawn.of(MAGIC).target(NORTH_WEST))
    ),

    WAVE_28(28, Segment.of(Spawn.of(RANGED).transformations(MAGIC, MELEE).target(SOUTH_EAST), Spawn.of(MAGIC).transformations(MELEE, MAGIC).target(NORTH_EAST)),
            Segment.of(Spawn.of(MAGIC).transformations(MELEE, RANGED).target(SOUTH_WEST), Spawn.of(MELEE).transformations(MAGIC, MELEE).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MELEE).transformations(MAGIC, MELEE).target(NORTH_WEST), Spawn.of(RANGED).aggressive().transformations(MELEE, MAGIC))
    ),

    WAVE_29(29, Segment.of(Spawn.of(RANGED).transformations(MELEE, MAGIC).aggressive(), Spawn.of(MAGIC).aggressive().transformations(RANGED, MELEE).target(NORTH_EAST)),
            Segment.of(Spawn.of(MELEE).large().target(SOUTH_EAST)),
            Segment.of(Spawn.of(MELEE).large().transformations(RANGED, MAGIC).aggressive(), Spawn.of(RANGED).transformations(MELEE, RANGED).target(SOUTH_WEST))
    ),

    WAVE_30(30, Segment.of(Spawn.of(MAGIC).large().aggressive()),
            Segment.of(Spawn.of(MAGIC).transformations(RANGED, MELEE).target(SOUTH_WEST), Spawn.of(MELEE).transformations(RANGED, MELEE).target(SOUTH_EAST)),
            Segment.of(Spawn.of(RANGED).large().transformations(MELEE, RANGED).target(SOUTH_WEST))
    ),

    WAVE_31(31, Segment.of(Spawn.of(RANGED).transformations(MELEE, RANGED).target(SOUTH_EAST), Spawn.of(MAGIC).transformations(RANGED, MAGIC).target(NORTH_EAST)),
            Segment.of(Spawn.of(MAGIC).transformations(MELEE, RANGED).target(SOUTH_WEST), Spawn.of(MELEE).transformations(MAGIC, RANGED).target(SOUTH_EAST)),
            Segment.of(Spawn.of(MELEE).transformations(RANGED, MAGIC).target(NORTH_WEST), Spawn.of(RANGED).transformations(MAGIC, RANGED).target(SOUTH_WEST))
    );

    private final int wave;
    private final Segment east, south, west;

    private static final WaveDefinition[] values = values();
    private static final Map<Integer, WaveDefinition> waves = new HashMap<>();

    public int getNylocasCap() {
        return wave >= 21 ? 24 : 12;
    }

    public WaveDefinition getNext() {
        return waves.get(wave + 1);
    }

    public static WaveDefinition get(final int wave) {
        return waves.get(wave);
    }

    static {
        for (val wave : values) {
            waves.put(wave.wave, wave);
        }
    }
}
