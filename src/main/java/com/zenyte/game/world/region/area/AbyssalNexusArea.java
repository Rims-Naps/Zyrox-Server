package com.zenyte.game.world.region.area;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.CannonRestrictionPlugin;

/**
 * @author Kris | 09/01/2019 18:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AbyssalNexusArea extends Area implements CannonRestrictionPlugin {

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[]{new RSPolygon(new int[][]{{2949, 4864}, {2949, 4736}, {3135, 4736}, {3135, 4864}, {3072, 4864}, {3072, 4802}, {3008, 4802}, {3008, 4864}}, 0)};
    }

    @Override
    public void enter(Player player) {

    }

    @Override
    public void leave(Player player, boolean logout) {

    }

    @Override
    public String restrictionMessage() {
        return "That horrible slime on the ground makes this area unsuitable for a cannon.";
    }

    @Override
    public String name() {
        return "Abyssal Nexus";
    }
}
