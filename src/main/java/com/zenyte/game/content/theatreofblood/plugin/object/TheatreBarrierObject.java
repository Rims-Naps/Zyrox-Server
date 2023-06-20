package com.zenyte.game.content.theatreofblood.plugin.object;

import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import lombok.val;

/**
 * @author Tommeh | 5/31/2020 | 11:11 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class TheatreBarrierObject implements ObjectAction {

    @Override
    public void handleObjectAction(final Player player, final WorldObject object, final String name, final int optionId, final String option) {
        val party = VerSinhazaArea.getParty(player);
        if (party == null) {
            return;
        }
        val raid = party.getRaid();
        if (raid == null) {
            return;
        }
        if (!(player.getArea() instanceof TheatreArea)) {
            return;
        }
        val room = (TheatreArea) player.getArea();
        room.handleBarrier(object, player);
    }

    @Override
    public Object[] getObjects() {
        return new Object[] {ObjectId.BARRIER_32755};
    }
}
