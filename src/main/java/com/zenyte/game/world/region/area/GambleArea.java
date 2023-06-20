package com.zenyte.game.world.region.area;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.RandomEventRestrictionPlugin;

/**
 * @author Corey
 * @since 15/06/2020
 */
public class GambleArea extends KingdomOfKandarin implements RandomEventRestrictionPlugin {
    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[]{
                new RSPolygon(new int[][]{
                        {2435, 3099},
                        {2447, 3099},
                        {2447, 3091},
                        {2452, 3091},
                        {2453, 3095},
                        {2453, 3100},
                        {2461, 3100},
                        {2461, 3081},
                        {2453, 3081},
                        {2447, 3084},
                        {2445, 3085},
                        {2443, 3084},
                        {2442, 3083},
                        {2442, 3081},
                        {2435, 3081}
                }),
        };
    }
    
    @Override
    public void enter(Player player) {
    }
    
    @Override
    public void leave(Player player, boolean logout) {
    }
    
    @Override
    public String name() {
        return "Gamble Zone";
    }
}
