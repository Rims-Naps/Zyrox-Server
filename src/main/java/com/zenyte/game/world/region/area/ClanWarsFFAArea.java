package com.zenyte.game.world.region.area;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.DeathPlugin;
import com.zenyte.game.world.region.area.plugins.RandomEventRestrictionPlugin;

/**
 * @author Kris | 02/05/2019 23:00
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ClanWarsFFAArea extends Area implements RandomEventRestrictionPlugin, DeathPlugin {
    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[] {
                new RSPolygon(new int[][]{
                        { 3264, 4864 },
                        { 3264, 4736 },
                        { 3392, 4736 },
                        { 3392, 4864 }
                })
        };
    }

    @Override
    public void enter(final Player player) {

    }

    @Override
    public void leave(final Player player, final boolean logout) {
        for (int i = 0; i < Skills.SKILLS.length; i++) {
            if (player.getSkills().getLevel(i) < player.getSkills().getLevelForXp(i)) {
                player.getSkills().setLevel(i, player.getSkills().getLevelForXp(i));
            }
        }
        player.blockIncomingHits();
        player.getReceivedHits().clear();
        player.getReceivedDamage().clear();
        player.getNextHits().clear();
        player.getCombatDefinitions().setSpecialEnergy(100);
        player.getVariables().setRunEnergy(100);
        player.getToxins().reset();
        player.getVariables().resetScheduled();
        player.getPrayerManager().deactivateActivePrayers();
        player.resetFreeze();
    }

    @Override
    public String name() {
        return "Clan Wars: FFA";
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    @Override
    public String getDeathInformation() {
        return "Deaths within the free-for-all zone are always safe.";
    }

    @Override
    public Location getRespawnLocation() {
        return new Location(3361, 3156, 0);
    }
}
