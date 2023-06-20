package com.zenyte.game.world.region.area.wilderness;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.teleportsystem.PortalTeleport;
import com.zenyte.game.world.region.RSPolygon;

/**
 * @author Kris | 31/01/2019 03:43
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ForinthryDungeon extends WildernessArea {
    @Override
    public void enter(final Player player) {
        super.enter(player);
        player.getTeleportManager().unlock(PortalTeleport.FORINTHRY_DUNGEON);
    }

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 3136, 10247 },
                        { 3136, 10036 },
                        { 3270, 10037 },
                        { 3271, 10249 }
                })
        };
    }

    @Override
    public String name() {
        return "Forinthry Dungeon";
    }
}
