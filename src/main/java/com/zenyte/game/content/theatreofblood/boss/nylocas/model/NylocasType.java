package com.zenyte.game.content.theatreofblood.boss.nylocas.model;

import com.zenyte.game.content.theatreofblood.boss.nylocas.npc.Nylocas;
import com.zenyte.game.content.theatreofblood.boss.nylocas.npc.NylocasHagios;
import com.zenyte.game.content.theatreofblood.boss.nylocas.npc.NylocasIschyros;
import com.zenyte.game.content.theatreofblood.boss.nylocas.npc.NylocasToxobolos;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.masks.HitType;
import com.zenyte.game.world.entity.npc.NpcId;
import lombok.Getter;
import lombok.val;

/**
 * @author Tommeh | 6/7/2020 | 3:59 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
@Getter
public enum NylocasType {

    MELEE(HitType.MELEE, NylocasIschyros.class, new Animation(8006), NpcId.NYLOCAS_ISCHYROS_8342, NpcId.NYLOCAS_ISCHYROS_8345, NpcId.NYLOCAS_VASILIAS_8355),
    RANGED(HitType.RANGED, NylocasToxobolos.class, new Animation(8000), NpcId.NYLOCAS_TOXOBOLOS_8343, NpcId.NYLOCAS_TOXOBOLOS_8346, NpcId.NYLOCAS_VASILIAS_8357),
    MAGIC(HitType.MAGIC, NylocasHagios.class, new Animation(7992), NpcId.NYLOCAS_HAGIOS, NpcId.NYLOCAS_HAGIOS_8347, NpcId.NYLOCAS_VASILIAS_8356);

    private final HitType acceptableHitType;
    private final Animation explosionAnimation;
    private final Class<? extends Nylocas> clazz;

    private final int[] ids;

    public static final NylocasType[] values = values();

    NylocasType(final HitType acceptableHitType, final Class<? extends Nylocas> clazz, final Animation explosionAnimation, final int... ids) {
        this.acceptableHitType = acceptableHitType;
        this.clazz = clazz;
        this.explosionAnimation = explosionAnimation;
        this.ids = ids;
    }

    public static NylocasType getRandom() {
        return values[Utils.random(values.length - 1)];
    }

    public static NylocasType get(final int npcId) {
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
