package com.zenyte.game.content.area.tarnslair.object;

import com.zenyte.game.world.entity.Location;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum Passage {

    FLOOR_1_PASSAGE_1(new PassageObject(16132, new Location(3158, 4553, 0)), new Location(3158, 4557, 1)),
    FLOOR_1_PASSAGE_2(new PassageObject(16132, new Location(3184, 4553, 0)), new Location(3184, 4557, 1)),
    FLOOR_1_PASSAGE_3(new PassageObject(20515, new Location(3178, 4561, 0)), new Location(3174, 4561, 1)),
    FLOOR_1_PASSAGE_4(new PassageObject(20517, new Location(3195, 4571, 0)), new Location(3195, 4575, 1)),

    FLOOR_1_PASSAGE_5(new PassageObject(16132, new Location(3147, 4548, 0)), new Location(3143, 4548, 1)),
    FLOOR_1_PASSAGE_6(new PassageObject(20492, new Location(3156, 4559, 0)), new Location(3161, 4559, 0)),

    FLOOR_1_PASSAGE_7(new PassageObject(20491, new Location(3160, 4559, 0)), new Location(3155, 4559, 0)),
    FLOOR_1_PASSAGE_8(new PassageObject(20490, new Location(3163, 4561, 0)), new Location(3163, 4565, 0)),

    FLOOR_1_PASSAGE_9(new PassageObject(20489, new Location(3163, 4564, 0)), new Location(3163, 4560, 0)),
    FLOOR_1_PASSAGE_10(new PassageObject(16132, new Location(3168, 4569, 0)), new Location(3172, 4569, 1)),

    FLOOR_1_PASSAGE_11(new PassageObject(20510, new Location(3184, 4570, 0)), new Location(3184, 4566, 1)),
    FLOOR_1_PASSAGE_12(new PassageObject(20511, new Location(3184, 4580, 0)), new Location(3184, 4585, 1)),

    FLOOR_1_PASSAGE_13(new PassageObject(20514, new Location(3171, 4577, 0)), new Location(3175, 4577, 1)),
    FLOOR_1_PASSAGE_14(new PassageObject(20523, new Location(3168, 4580, 0)), new Location(3168, 4586, 0)),
    FLOOR_1_PASSAGE_15(new PassageObject(20506, new Location(3165, 4577, 0)), new Location(3161, 4577, 0)),

    FLOOR_1_PASSAGE_16(new PassageObject(20505, new Location(3162, 4577, 0)), new Location(3166, 4577, 0)),
    FLOOR_1_PASSAGE_17(new PassageObject(20504, new Location(3150, 4583, 0)), new Location(3146, 4583, 1)),

    FLOOR_1_PASSAGE_18(new PassageObject(20524, new Location(3168, 4585, 0)), new Location(3168, 4579, 0)),
    FLOOR_1_PASSAGE_19(new PassageObject(20525, new Location(3165, 4589, 0)), new Location(3161, 4589, 1)),
    FLOOR_1_PASSAGE_20(new PassageObject(20532, new Location(3168, 4593, 0)), new Location(3168, 4597, 0)),

    FLOOR_1_PASSAGE_21(new PassageObject(20531, new Location(3168, 4596, 0)), new Location(3168, 4592, 0)),
    FLOOR_1_PASSAGE_22(new PassageObject(20530, new Location(3159, 4598, 0)), new Location(3149, 4598, 0)),

    FLOOR_1_PASSAGE_23(new PassageObject(20529, new Location(3150, 4598, 0)), new Location(3160, 4598, 0)),
    FLOOR_1_PASSAGE_24(new PassageObject(20528, new Location(3145, 4593, 0)), new Location(3145, 4589, 1)),

    FLOOR_1_PASSAGE_25(new PassageObject(16132, new Location(3190, 4598, 0)), new Location(3194, 4598, 1)),
    FLOOR_1_PASSAGE_26(new PassageObject(20539, new Location(3185, 4602, 0)), new Location(3149, 4644, 0)),

    FLOOR_1_PASSAGE_27(new PassageObject(15771, new Location(3149, 4643, 0)), new Location(3185, 4601, 0)),
    FLOOR_1_PASSAGE_28(new PassageObject(16129, new Location(3149, 4659, 0)), new Location(3149, 4664, 0)),

    FLOOR_1_PASSAGE_29(new PassageObject(16130, new Location(3149, 4663, 0)), new Location(3149, 4658, 0)),

    FLOOR_2_PASSAGE_1(new PassageObject(18308, new Location(3141, 4551, 1)), new Location(3141, 4555, 2)),
    FLOOR_2_PASSAGE_2(new PassageObject(17098, new Location(3144, 4548, 1)), new Location(3148, 4548, 0)),

    FLOOR_2_PASSAGE_3(new PassageObject(17098, new Location(3158, 4556, 1)), new Location(3158, 4552, 0)),
    FLOOR_2_PASSAGE_4(new PassageObject(18308, new Location(3158, 4564, 1)), new Location(3158, 4568, 2)),

    FLOOR_2_PASSAGE_5(new PassageObject(20516, new Location(3175, 4561, 1)), new Location(3179, 4561, 0)),
    FLOOR_2_PASSAGE_6(new PassageObject(17098, new Location(3171, 4569, 1)), new Location(3167, 4569, 0)),

    FLOOR_2_PASSAGE_7(new PassageObject(17098, new Location(3184, 4556, 1)), new Location(3184, 4552, 0)),
    FLOOR_2_PASSAGE_8(new PassageObject(20509, new Location(3184, 4567, 1)), new Location(3184, 4571, 0)),

    FLOOR_2_PASSAGE_9(new PassageObject(20518, new Location(3195, 4574, 1)), new Location(3195, 4570, 0)),
    FLOOR_2_PASSAGE_10(new PassageObject(20519, new Location(3193, 4577, 1)), new Location(3189, 4577, 2)),

    FLOOR_2_PASSAGE_11(new PassageObject(20522, new Location(3186, 4584, 1)), new Location(3186, 4578, 2)),
    FLOOR_2_PASSAGE_12(new PassageObject(20512, new Location(3184, 4584, 1)), new Location(3184, 4579, 0)),

    FLOOR_2_PASSAGE_13(new PassageObject(18308, new Location(3177, 4577, 1)), new Location(3181, 4577, 2)),
    FLOOR_2_PASSAGE_14(new PassageObject(20513, new Location(3174, 4577, 1)), new Location(3170, 4577, 0)),

    FLOOR_2_PASSAGE_15(new PassageObject(20503, new Location(3147, 4583, 1)), new Location(3151, 4583, 0)),
    FLOOR_2_PASSAGE_16(new PassageObject(20502, new Location(3144, 4581, 1)), new Location(3144, 4577, 2)),

    FLOOR_2_PASSAGE_17(new PassageObject(20527, new Location(3145, 4590, 1)), new Location(3145, 4594, 0)),
    FLOOR_2_PASSAGE_18(new PassageObject(20526, new Location(3162, 4589, 1)), new Location(3166, 4589, 0)),
    FLOOR_2_PASSAGE_19(new PassageObject(20533, new Location(3154, 4597, 1)), new Location(3151, 4597, 1)),

    FLOOR_2_PASSAGE_20(new PassageObject(20534, new Location(3152, 4597, 1)), new Location(3155, 4597, 1)),
    FLOOR_2_PASSAGE_21(new PassageObject(20535, new Location(3176, 4598, 1)), new Location(3179, 4598, 1)),

    FLOOR_2_PASSAGE_22(new PassageObject(20536, new Location(3178, 4598, 1)), new Location(3175, 4598, 1)),
    FLOOR_2_PASSAGE_23(new PassageObject(17098, new Location(3193, 4598, 1)), new Location(3189, 4598, 0)),

    FLOOR_2_PASSAGE_24(new PassageObject(20498, new Location(3141, 4564, 1)), new Location(3141, 4560, 2)),
    FLOOR_2_PASSAGE_25(new PassageObject(20499, new Location(3144, 4567, 1)), new Location(3144, 4571, 2)),

    FLOOR_3_PASSAGE_1(new PassageObject(18973, new Location(3141, 4554, 2)), new Location(3141, 4550, 1)),
    FLOOR_3_PASSAGE_2(new PassageObject(20497, new Location(3141, 4561, 2)), new Location(3141, 4565, 1)),

    FLOOR_3_PASSAGE_3(new PassageObject(18973, new Location(3158, 4567, 2)), new Location(3158, 4563, 1)),

    FLOOR_3_PASSAGE_4(new PassageObject(20500, new Location(3144, 4570, 2)), new Location(3144, 4566, 1)),
    FLOOR_3_PASSAGE_5(new PassageObject(20501, new Location(3144, 4578, 2)), new Location(3144, 4582, 1)),

    FLOOR_3_PASSAGE_6(new PassageObject(19029, new Location(3180, 4577, 2)), new Location(3176, 4577, 1)),
    FLOOR_3_PASSAGE_7(new PassageObject(20521, new Location(3186, 4579, 2)), new Location(3186, 4585, 1)),
    FLOOR_3_PASSAGE_8(new PassageObject(20520, new Location(3190, 4577, 2)), new Location(3194, 4577, 1)),

    ;

    @Getter private final PassageObject passage;
    @Getter private final Location to;

    Passage(final PassageObject passage, final Location to) {
        this.passage = passage;
        this.to = to;
    }

    public static final Passage[] VALUES = values();
    public static final Map<Integer, Passage> PASSAGE_MAP = new HashMap<Integer, Passage>(VALUES.length);

    static {
        for (final Passage passage: VALUES) {
            PASSAGE_MAP.put(passage.getPassage().getLocation().getPositionHash(), passage);
        }
    }
}
