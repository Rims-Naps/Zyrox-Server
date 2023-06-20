package com.zenyte.game.world.region.area.wilderness;

import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.CannonRestrictionPlugin;

/**
 * @author Kris | 31/01/2019 03:44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ForinthryDungeonRevenantsSection extends ForinthryDungeon implements CannonRestrictionPlugin {

    @Override
    public String name() {
        return "Forinthry Dungeon: Revenants";
    }

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 3226, 10188 },
                        { 3219, 10180 },
                        { 3239, 10148 },
                        { 3254, 10135 },
                        { 3261, 10138 },
                        { 3262, 10195 }
                })
        };
    }

}
