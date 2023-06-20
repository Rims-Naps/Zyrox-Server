package com.zenyte.game.content.minigame.tithefarm;

import com.zenyte.game.world.object.ObjectId;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public enum TitheStatus {

    SEEDLING_UNWATERED(ObjectId.GOLOVANOVA_SEEDLING, ObjectId.BOLOGANO_SEEDLING, ObjectId.LOGAVANO_SEEDLING),
    SEEDLING_WATERED(ObjectId.GOLOVANOVA_SEEDLING + 1, ObjectId.BOLOGANO_SEEDLING + 1, ObjectId.LOGAVANO_SEEDLING + 1),
    SEEDLING_BLIGHTED(ObjectId.BLIGHTED_GOLOVANOVA_SEEDLING,  ObjectId.BLIGHTED_BOLOGANO_SEEDLING, ObjectId.BLIGHTED_LOGAVANO_SEEDLING),

    GROWING_UNWATERED(ObjectId.GOLOVANOVA_PLANT, ObjectId.BOLOGANO_PLANT, ObjectId.LOGAVANO_PLANT),
    GROWING_WATERED(ObjectId.GOLOVANOVA_PLANT + 1, ObjectId.BOLOGANO_PLANT + 1, ObjectId.LOGAVANO_PLANT + 1),
    GROWING_BLIGHTED(ObjectId.BLIGHTED_GOLOVANOVA_PLANT, ObjectId.BLIGHTED_BOLOGANO_PLANT, ObjectId.BLIGHTED_LOGAVANO_PLANT),

    MATURE_UNWATERED(ObjectId.GOLOVANOVA_PLANT + 3, ObjectId.BOLOGANO_PLANT + 3, ObjectId.LOGAVANO_PLANT + 3),
    MATURE_WATERED(ObjectId.GOLOVANOVA_PLANT + 4, ObjectId.BOLOGANO_PLANT + 4, ObjectId.LOGAVANO_PLANT + 4),
    MATURE_BLIGHTED(ObjectId.BLIGHTED_GOLOVANOVA_PLANT + 3, ObjectId.BLIGHTED_BOLOGANO_PLANT + 3, ObjectId.BLIGHTED_LOGAVANO_PLANT + 3),

    READY(ObjectId.GOLOVANOVA_PLANT + 6, ObjectId.BOLOGANO_PLANT + 6, ObjectId.LOGAVANO_PLANT + 6),
    READY_BLIGHTED(ObjectId.BLIGHTED_GOLOVANOVA_PLANT + 5, ObjectId.BLIGHTED_BOLOGANO_PLANT + 5, ObjectId.BLIGHTED_LOGAVANO_PLANT + 5),
    ;

    private final int[] ids;
    private final static TitheStatus[] VALUES = values();
    public final static Map<Integer, TitheStatus> STATUS_MAP = new HashMap<>();

    TitheStatus(final int... ids) {
        this.ids = ids;
    }

    static {
        for(final TitheStatus status : VALUES) {
            for(final int id : status.ids) {
                STATUS_MAP.put(id, status);
            }
        }
    }

    public static final String getStatus(final int id) {
        return STATUS_MAP.get(id).toString();
    }

    public final int getObjectId(final TithePlantType type) {
        switch(type.toString().toLowerCase()) {
            case "golovanova":
                return this.ids[0];
            case "bologano":
                return this.ids[1];
            case "logavano":
                return this.ids[2];
            default:
                return ObjectId.TITHE_PATCH;
        }
    }
}
