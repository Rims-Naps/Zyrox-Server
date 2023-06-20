package com.zenyte.game.world.entity.npc.combatdefs;

import com.zenyte.game.world.entity.Toxins;
import lombok.Getter;
import lombok.val;

/**
 * @author Kris | 18/11/2018 02:50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Getter
public class ToxinDefinitions {
    private Toxins.ToxinType type;
    private int damage;

    public ToxinDefinitions clone() {
        val defs = new ToxinDefinitions();
        defs.type = type;
        defs.damage = damage;
        return defs;
    }

}
