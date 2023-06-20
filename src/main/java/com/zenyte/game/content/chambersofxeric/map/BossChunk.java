package com.zenyte.game.content.chambersofxeric.map;

import com.zenyte.game.content.chambersofxeric.Raid;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.region.GlobalAreaManager;
import com.zenyte.game.world.region.dynamicregion.MapBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 15. nov 2017 : 0:36.40
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public abstract class BossChunk extends RaidArea {

	public BossChunk(final RaidRoom type, final Raid raid, final int x, final int y) {
		super(type, raid, 0, 7, 402, 713, x, y, 0, 0);
		this.sizeX = 5;
	}

    @Override
    public void constructRegion() {
        try {
            if (constructed) {
                return;
            }
            GlobalAreaManager.add(this);
            try {
                MapBuilder.copyAllPlanesMap(area, staticChunkX, staticChunkY, chunkX, chunkY, sizeX, sizeY);
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            constructed = true;
            constructed();
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

	@Override
	public final Location getLocation(final int x, final int y, final int height) {
		val offsetX = x - (staticChunkX << 3);
		val offsetY = y - (staticChunkY << 3);
		return new Location((chunkX << 3) + offsetX, (chunkY << 3) + offsetY, 0);
	}

	@Override
	public final Location getLocation(final Location tile) {
	    return getLocation(tile.getX(), tile.getY(), tile.getPlane());
	}

	@Deprecated
	@Override
	public final Location getNPCLocation(final Location tile, final int npcSize) {
		throw new RuntimeException("Method only available to default raid chunks - Olm doesn't rotate.");
	}

    @Deprecated
	@Override
	public Location getObjectLocation(final Location tile, final int sizeX, final int sizeY, final int rotation) {
		throw new RuntimeException("Method only available to default raid chunks - Olm doesn't rotate.");
	}

    @Deprecated
	@Override
	public Location getRespectiveTile(final Location west, final Location north, final Location east) {
		throw new RuntimeException("Method only available to default raid chunks - Olm doesn't rotate.");
	}

}
