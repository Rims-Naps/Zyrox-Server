package com.zenyte.game.world.region.area.wilderness;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.teleportsystem.PortalTeleport;
import com.zenyte.game.world.region.RSPolygon;

public class LowRevenantsArea  extends WildernessArea {
@Override
public RSPolygon[] polygons() {
        return new RSPolygon[]{
        new RSPolygon(new int[][]{
        {3129, 3832},
        {3127, 3832},
        {3129, 3834},
        {3126, 3831}
        })
        };
        }

@Override
public void enter(final Player player) {
        player.getTeleportManager().unlock(PortalTeleport.LOW_REVENANTS);
        }

@Override
public void leave(final Player player, final boolean logout) {

        }

@Override
public String name() {
        return "Level: 17 Revenants";
        }
        }
