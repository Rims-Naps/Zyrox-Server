package com.zenyte.game.world.region.area.wilderness;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.teleportsystem.PortalTeleport;
import com.zenyte.game.world.region.RSPolygon;

public class HighRevenantsArea extends WildernessArea {
    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[]{
                new RSPolygon(new int[][]{
                        {3074, 3652},
                        {3075, 3651},
                        {3076, 3652},
                        {3076, 3651}
                })
        };
    }

    @Override
    public void enter(final Player player) {
        player.getTeleportManager().unlock(PortalTeleport.HIGH_REVENANTS);
    }

    @Override
    public void leave(final Player player, final boolean logout) {

    }

    @Override
    public String name() {
        return "Level: 39 Revenants";
    }
}
