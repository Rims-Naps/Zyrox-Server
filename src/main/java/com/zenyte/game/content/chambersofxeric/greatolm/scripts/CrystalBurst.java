package com.zenyte.game.content.chambersofxeric.greatolm.scripts;

import com.zenyte.game.content.chambersofxeric.greatolm.CrystalCluster;
import com.zenyte.game.content.chambersofxeric.greatolm.GreatOlm;
import com.zenyte.game.content.chambersofxeric.greatolm.LeftClaw;
import com.zenyte.game.content.chambersofxeric.greatolm.OlmCombatScript;
import com.zenyte.game.world.entity.Location;
import lombok.val;

/**
 * @author Kris | 16. jaan 2018 : 1:10.37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class CrystalBurst implements OlmCombatScript {

    @Override
    public void handle(final GreatOlm olm) {
        val room = olm.getRoom();
        val leftClaw = room.getLeftClaw();
        if (leftClaw != null) {
            leftClaw.displaySign(LeftClaw.CRYSTAL_SIGN);
        }
        if (room.getRaid().isDestroyed()) {
            return;
        }
        for (val player : olm.everyone(GreatOlm.ENTIRE_CHAMBER)) {
            val tile = new Location(player.getLocation());
            if (room.containsCrystalCluster(tile)) {
                continue;
            }
            val cluster = new CrystalCluster(room, tile);
            cluster.process();
        }
    }

}
