package com.zenyte.game.world.region.dynamicregion;

import com.zenyte.game.world.Position;
import com.zenyte.game.world.entity.Location;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;
import lombok.ToString;
import lombok.val;

/**
 * @author Kris | 29. juuli 2018 : 18:57:56
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
@ToString
public class AllocatedArea {

    public AllocatedArea(final int minRegionX, final int minRegionY, final int widthInRegions, final int heightInRegions, final int sizeX, final int sizeY) {
        this.minRegionX = minRegionX;
        this.minRegionY = minRegionY;
        this.widthInRegions = widthInRegions;
        this.heightInRegions = heightInRegions;
        val padding = MapBuilder.PADDING << 3;
        this.minX = (minRegionX << 6) + padding;
        this.minY = (minRegionY << 6) + padding;
        this.maxX = minX + (sizeX << 3);
        this.maxY = minY + (sizeY << 3);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

	public final int getChunkX() {
		return (minRegionX << 3) + MapBuilder.PADDING;
	}

	public final int getChunkY() {
		return (minRegionY << 3) + MapBuilder.PADDING;
	}

	@Getter
	private final int minRegionX, minRegionY, widthInRegions, heightInRegions;

	private final int minX;
	private final int minY;

	private final int maxX;
	private final int maxY;

	@Getter private final int sizeX, sizeY;

	public void verify(final int chunkX, final int chunkY) throws OutOfBoundaryException {
		val minChunkX = minRegionX << 3;
		val minChunkY = minRegionY << 3;

		val maxChunkX = ((minRegionX + widthInRegions) << 3);
		val maxChunkY = ((minRegionY + heightInRegions) << 3);

		if (chunkX < minChunkX || chunkY < minChunkY || chunkX > maxChunkX || chunkY > maxChunkY) {
			throw new OutOfBoundaryException("Attempting to copy a piece of land outside of the allocated area!");
		}
	}

	public Location getMinLocation() {
		return new Location(minRegionX << 6, minRegionY << 6, 0);
	}

	public Location getCenterLocation() {
		return new Location((minRegionX << 6) + (int) (widthInRegions / 2F * 64), (minRegionY << 6) + (int) (heightInRegions / 2F * 64), 0);
	}

	public Location getMaxLocation() {
		return new Location((minRegionX + widthInRegions) << 6, (minRegionY + heightInRegions) << 6, 0);
	}

	public IntOpenHashSet getAllocatedRegions() {
		val set = new IntOpenHashSet();
		for (int x = minRegionX; x < (minRegionX + widthInRegions); x++) {
			for (int y = minRegionY; y < (minRegionY + heightInRegions); y++) {
				set.add(x << 8 | y);
			}
		}
		return set;
	}

	public final boolean inside(final Position tile) {
	    val location = tile.getPosition();
	    val x = location.getX();
	    val y = location.getY();
	    return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

}
