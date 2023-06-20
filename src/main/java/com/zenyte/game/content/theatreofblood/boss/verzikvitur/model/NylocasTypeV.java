package com.zenyte.game.content.theatreofblood.boss.verzikvitur.model;

import com.zenyte.game.content.theatreofblood.boss.verzikvitur.npc.*;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NpcId;
import lombok.Getter;
import lombok.val;

@Getter
public enum NylocasTypeV {
//8076, 8077, 8078, 8079
    MELEE(HitType.MELEE, NylocasIschyrosV.class, new Animation(8006), NpcId.NYLOCAS_ISCHYROS_8342, NpcId.NYLOCAS_ISCHYROS_8345, NpcId.NYLOCAS_VASILIAS_8355),
    RANGED(HitType.RANGED, NylocasToxobolosV.class, new Animation(8000), NpcId.NYLOCAS_TOXOBOLOS_8343, NpcId.NYLOCAS_TOXOBOLOS_8346, NpcId.NYLOCAS_VASILIAS_8357),
    MAGIC(HitType.MAGIC, NylocasHagiosV.class, new Animation(7992), NpcId.NYLOCAS_HAGIOS, NpcId.NYLOCAS_HAGIOS_8347, NpcId.NYLOCAS_VASILIAS_8356),
    PURPLY(HitType.POISON, NylocasAthanatosV.class, new Animation(8078), NpcId.NYLOCAS_ATHANATOS, NpcId.NYLOCAS_ATHANATOS, NpcId.NYLOCAS_ATHANATOS),
    MATOMENOS(HitType.REGULAR, NylocasMatomenosV.class, new Animation(8097), NpcId.NYLOCAS_MATOMENOS_8385, NpcId.NYLOCAS_MATOMENOS_8385, NpcId.NYLOCAS_MATOMENOS_8385);
    private final HitType acceptableHitType;
    private final Animation explosionAnimation;
    private final Class<? extends NylocasV> clazz;

    private final int[] ids;

    public static final NylocasTypeV[] values = values();

    NylocasTypeV(final HitType acceptableHitType, final Class<? extends NylocasV> clazz, final Animation explosionAnimation, final int... ids) {
        this.acceptableHitType = acceptableHitType;
        this.clazz = clazz;
        this.explosionAnimation = explosionAnimation;
        this.ids = ids;
    }

    public static NylocasTypeV getRandom() {
        return values[Utils.random(values.length - 1)];
    }

    public static NylocasTypeV get(final int npcId) {
        for (val type : values) {
            for (val id : type.ids) {
                if (npcId == id) {
                    return type;
                }
            }
        }
        return null;
    }
}
