package com.zenyte.game.content.boss.bryophyta;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import lombok.val;

/**
 * @author Tommeh | 17/05/2019 | 19:15
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class Growthling extends NPC {

    public Growthling(int id, Location tile) {
        super(id, tile, Direction.SOUTH, 3);
        setSpawned(true);
        deathDelay--;
    }

    @Override
    public void sendDeath() {
        val source = getMostDamagePlayerCheckIronman();
        if (getHitpoints() == 0) {
            if (source != null) {
                if (!source.getBooleanTemporaryAttribute("growthling_info_msg")) {
                    source.sendMessage("Cut the growthling down with an axe or secateurs.");
                    source.addTemporaryAttribute("growthling_info_msg", 1);
                }
            }
            heal(1);
        } else {
            super.sendDeath();
        }
    }
}
