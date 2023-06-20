package com.zenyte.game.content.chambersofxeric.map;

import com.zenyte.Constants;
import com.zenyte.game.util.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

import static com.zenyte.game.content.chambersofxeric.map.ChunkDirection.*;

/**
 * @author Kris | 28/06/2019 14:26
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
class MapPalette implements Iterable<MapChunk> {

    /**
     * Keeps a linked list of all the chunks to preserve their proper ordering.
     */
    @NotNull
    private final LinkedList<MapChunk> paletteChunks;
    /**
     * Raid map can only ever be up to 8 chunks long, meaning if we start from the center point of 7, 7
     * it can only ever go to 0, 0 or 14, 14.
     */
    @NotNull
    private MapChunk[][] palette;
    /**
     * The current x/y positions of the chunk.
     */
    private int x, y;
    /**
     * The last direction in which way the chunk was facing.
     */
    @NotNull
    @Getter
    @Setter
    private ChunkDirection lastDirection;

    MapPalette(final boolean challenge) {
        palette = new MapChunk[15][15];
        x = y = 7;
        lastDirection = challenge ? ChunkDirection.NORTH : Utils.getRandomElement(ChunkDirection.values);
        this.paletteChunks = new LinkedList<>();
    }

    /**
     * Adds a chunk to the requested transformation point.
     *
     * @param chunk     the chunk to add next.
     * @param direction the direction to which the chunk goes.
     */
    void add(@NotNull final MapChunk chunk, @NotNull final ChunkDirection direction) {
        add(chunk, direction, false);
    }

    /**
     * Adds a chunk to the requested transformation point.
     *
     * @param chunk     the chunk to add next.
     * @param direction the direction to which the chunk goes.
     * @param forceEast whether or not to force the chunk type to eastern one.
     */
    void add(@NotNull final MapChunk chunk, @NotNull final ChunkDirection direction, final boolean forceEast) {
        palette[x][y] = chunk;
        chunk.x = x;
        chunk.y = y;
        if (chunk.room == RaidRoom.RAID_START) {
            chunk.direction = NORTH;
            chunk.chunkDirection = direction == WEST ? 0 : direction == NORTH ? 1 : 2;
        } else {
            chunk.direction = lastDirection;
            chunk.chunkDirection = forceEast ? 0 : chunk.room == RaidRoom.FLOOR_END_DOWNSTAIRS ? Utils.random(1) ://Floor end room only has two versions of it, unlike the rest.
                                   lastDirection == direction ? 1 : direction == values[(lastDirection.ordinal() - 1) & 0x3] ? 0 : 2;
        }

        x += direction.getXOffset();
        y += direction.getYOffset();
        lastDirection = direction;
        paletteChunks.add(chunk);
    }

    /**
     * Crops the palette by resizing it so the map is shifted to the south-western corner, as much as possible. Also crops out unnecessary extra space.
     */
    void crop() {
        val minimum = getMinimumPoint();
        val maximum = getMaximumPoint();
        val minX = (int) minimum.getX();
        val minY = (int) minimum.getY();
        val maxX = (int) maximum.getX() + 1;
        val maxY = (int) maximum.getY() + 1;
        val croppedPalette = new MapChunk[maxX - minX][maxY - minY];
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                val chunk = palette[x][y];
                croppedPalette[x - minX][y - minY] = chunk;
                if (chunk == null) {
                    continue;
                }
                chunk.x -= minX;
                chunk.y -= minY;
            }
        }
        this.palette = croppedPalette;
        print();
    }

    /**
     * Prints the palette layout in the console.
     */
    private final void print() {
        if (!Constants.WORLD_PROFILE.isDevelopment()) {
            return;
        }
        for (val room : this) {
            System.err.println("Room: " + room.room + ", " + room.y + "-" + room.x);
        }
        System.err.println();

        for (int i = 0; i < palette.length; i++) {
            for (int x = 0; x < palette[i].length; x++) {
                if (palette[i][x] == null) {
                    System.err.print(String.format("%-10s", ""));
                } else {
                    System.err.print(String.format("%-10s", x + "-" + i));
                }
            }
            System.err.println();
            System.err.println();
            System.err.println();
        }

        System.err.println();
        System.err.println();
        System.err.println();
        System.err.println();
    }

    /**
     * Gets the minimum point where a chunk exists.
     *
     * @return the minimum point where a chunk exists.
     */
    @NotNull
    private final Point getMinimumPoint() {
        int offsetX = Integer.MAX_VALUE;
        int offsetY = Integer.MAX_VALUE;
        for (int x = 0; x < palette.length; x++) {
            for (int y = 0; y < palette[x].length; y++) {
                val chunk = palette[x][y];
                if (chunk == null) {
                    continue;
                }
                if (x < offsetX) {
                    offsetX = x;
                }
                if (y < offsetY) {
                    offsetY = y;
                }
            }
        }
        return new Point(offsetX, offsetY);
    }

    /**
     * Gets the maximum point where a chunk exists.
     *
     * @return the maximum point where a chunk exists.
     */
    @NotNull
    private final Point getMaximumPoint() {
        int offsetX = Integer.MIN_VALUE;
        int offsetY = Integer.MIN_VALUE;
        for (int x = 0; x < palette.length; x++) {
            for (int y = 0; y < palette[x].length; y++) {
                val chunk = palette[x][y];
                if (chunk == null) {
                    continue;
                }
                if (offsetX < x) {
                    offsetX = x;
                }
                if (offsetY < y) {
                    offsetY = y;
                }
            }
        }
        return new Point(offsetX, offsetY);
    }

    /**
     * The iterator to iterate over all the rooms seamlessly in the exact order they were constructed in.
     *
     * @return the map chunk iterator.
     */
    @NotNull
    @Override
    public Iterator<MapChunk> iterator() {
        return paletteChunks.iterator();
    }
}
