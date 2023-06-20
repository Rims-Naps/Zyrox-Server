package com.zenyte.game.world.region.area;

import com.zenyte.game.HintArrow;
import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.prayer.Prayer;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.PrayerPlugin;
import com.zenyte.game.world.region.area.plugins.RandomEventRestrictionPlugin;
import com.zenyte.game.world.region.area.plugins.TeleportPlugin;
import com.zenyte.game.world.region.area.plugins.TradePlugin;

/**
 * @author Tommeh | 1-2-2019 | 16:03
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class RegisterIslandArea extends Area implements TeleportPlugin, TradePlugin, PrayerPlugin, RandomEventRestrictionPlugin {

    private static final Location ZENYTE_GUIDE_LOCATION = new Location(2325, 2917, 0);

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] { new RSPolygon(new int[][]{ { 2304, 2944 }, { 2304, 2880 }, { 2368, 2880 }, { 2368, 2944 }})};
    }

    @Override
    public void enter(Player player) {
        World.findNPC(3308, ZENYTE_GUIDE_LOCATION, 10)
                .ifPresent(npc -> player.getPacketDispatcher().sendHintArrow(new HintArrow(npc)));
    }

    @Override
    public void leave(Player player, boolean logout) {

    }

    @Override
    public String name() {
        return "Register Island";
    }

    @Override
    public boolean canTeleport(final Player player, final Teleport teleport) {
        return false;
    }

    @Override
    public boolean canTrade(final Player player, final Player partner) {
        return false;
    }

    @Override
    public boolean activatePrayer(final Player player, final Prayer prayer) {
        return false;
    }

}

