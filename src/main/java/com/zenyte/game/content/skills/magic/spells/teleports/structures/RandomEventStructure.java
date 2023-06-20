package com.zenyte.game.content.skills.magic.spells.teleports.structures;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.area.bobsisland.EvilBobIsland;
import com.zenyte.utils.ProjectileUtils;
import lombok.val;

/**
 * @author Kris | 25/06/2019 20:49
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RandomEventStructure extends RegularStructure {

    private static final Location edgeville = new Location(3088, 3489, 0);

    @Override
    public boolean isAreaPrevented(final Player player, final Teleport teleport) {
        return false;
    }

    @Override
    public Location getRandomizedLocation(final Player player, final Teleport teleport) {
        val dest = teleport.getDestination();
        if (dest != null) {
            val area = GlobalAreaManager.getArea(dest);
            if (area != null && area.getClass().equals(EvilBobIsland.class)) {
                dest.setLocation(edgeville);
            }
        }
        return new Location(Utils.getOrDefault(randomize(player, teleport), edgeville));
    }

    private Location randomize(final Player player, final Teleport teleport) {
        val randomization = teleport.getRandomizationDistance();
        val destination = new Location(teleport.getDestination());
        int count = RANDOMIZATION_ATTEMPT_COUNT;
        while (--count > 0) {
            val tile = destination.random(randomization);
            World.loadRegion(tile.getRegionId());
            if (ProjectileUtils.isProjectileClipped(player, null, destination, tile, true) || !World.isFloorFree(tile, player.getSize()))
                continue;
            return tile;
        }
        return null;
    }

}
