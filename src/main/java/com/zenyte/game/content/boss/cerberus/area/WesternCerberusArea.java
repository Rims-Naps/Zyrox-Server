package com.zenyte.game.content.boss.cerberus.area;

import com.zenyte.game.world.region.RSPolygon;

/**
 * @author Kris | 07/08/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class WesternCerberusArea extends CerberusLair {

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 1215, 1274 },
                        { 1215, 1243 },
                        { 1259, 1243 },
                        { 1259, 1274 }
                })
        };
    }

}
