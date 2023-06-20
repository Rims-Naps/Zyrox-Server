package com.zenyte.game.world.region.dynamicregion;

import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.skills.hunter.npc.HunterDummyNPC;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.region.CharacterLoop;
import com.zenyte.game.world.region.DynamicRegion;
import com.zenyte.game.world.region.Region;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.val;

import java.util.ArrayList;

/**
 * @author Kris | 29. juuli 2018 : 15:21:31
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class MapBuilder {

    private static final int MIN_REGION_X = 0x64;
    private static final int MIN_REGION_Y = 0x1;

    private static final int MAX_REGION_X = 0xFF;
    private static final int MAX_REGION_Y = 0xFF;
    private static final IntOpenHashSet ALLOCATED_REGIONS = new IntOpenHashSet(Short.MAX_VALUE);
    private static final int AMOUNT_OF_REGIONS = (MAX_REGION_X - MIN_REGION_X) * (MAX_REGION_Y - MIN_REGION_Y);
    private static int pointerX = MIN_REGION_X, pointerY = MIN_REGION_Y;

    public static final int PADDING = 13;

    /**
     * Finds the coordinates to an empty area large enough to support the specifications.
     *
     * @param sizeX the X size the empty area needs to be in chunks.
     * @param sizeY the Y size the empty area needs to be in chunks.
     * @throws OutOfSpaceException if there isn't enough free map remaining for the specified boundary, an out of space exception is thrown.
     */
    @Synchronized
    public static AllocatedArea findEmptyChunk(final int sizeX, final int sizeY) throws OutOfSpaceException,
            NullPointerException {
        val widthInRegions = (int) Math.ceil((sizeX + PADDING) / 8F);
        val heightInRegions = (int) Math.ceil((sizeY + PADDING) / 8F);

        int pointer = 0;

        int regionX = pointerX;
        int regionY = pointerY;

        while(true) {
            if (++pointer >= AMOUNT_OF_REGIONS) {
                throw new OutOfSpaceException("Out of free map space when trying to allocate map.");
            }
            if (++regionY + heightInRegions >= MAX_REGION_Y) {
                regionY = MIN_REGION_Y;
                if (++regionX + widthInRegions >= MAX_REGION_X) {
                    regionX = MIN_REGION_X;
                }
            }

            if (!isAreaFree(regionX, regionY, widthInRegions, heightInRegions)) {
                continue;
            }
            pointerX = regionX;
            //Append the height of the instance to the pointer so that if the latest instance is wiped, it doesn't overlap.
            pointerY = regionY + heightInRegions;
            return allocate(regionX, regionY, widthInRegions, heightInRegions, sizeX, sizeY);
        }
    }

    private static boolean isAreaFree(final int minRegionX, final int minRegionY, final int widthInRegions, final int heightInRegions) {
        for (int x = minRegionX; x < (minRegionX + widthInRegions); x++) {
            for (int y = minRegionY; y < (minRegionY + heightInRegions); y++) {
                if (ALLOCATED_REGIONS.contains(x << 8 | y)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static AllocatedArea allocate(final int minRegionX, final int minRegionY, final int widthInRegions,
                                          final int heightInRegions, final int sizeX, final int sizeY) {
        for (int x = minRegionX; x < (minRegionX + widthInRegions); x++) {
            for (int y = minRegionY; y < (minRegionY + heightInRegions); y++) {
                ALLOCATED_REGIONS.add(x << 8 | y);
            }
        }
        return new AllocatedArea(minRegionX, minRegionY, widthInRegions, heightInRegions, sizeX, sizeY);
    }

    @Synchronized
    private static DynamicRegion getDynamicRegion(final int regionId) {
        val region = World.regions.get(regionId);
        if (region instanceof DynamicRegion) {
            return (DynamicRegion) region;
        }

        val newRegion = new DynamicRegion(regionId);
        World.regions.put(regionId, newRegion);
        return newRegion;
    }

    private static void destroyRegion(@NonNull final Region region) {
        ALLOCATED_REGIONS.remove(region.getId());
        World.regions.remove(region.getId());
        if (region instanceof DynamicRegion) {
            ((DynamicRegion) region).clearMultiZones();
        }

        //Destroy the region object as it is being completely overridden anyways, and will be inaccessible in memory.
        region.destroy();

        CharacterLoop.forEachChunk(region.getId(), chunk -> {
            chunk.getSpawnedObjects().clear();
            chunk.getOriginalObjects().clear();
            chunk.clearFloorItems();
            chunk.getPlayers().forEach(player -> {
                player.setLoadingRegion(true);
                player.setNeedRegionUpdate(true);
            });
            World.deallocateChunk(chunk.getChunkId());
            val npcs = chunk.getNPCs();
            if (npcs.isEmpty()) return;
            //To prevent concurrent modifications, we construct our own list of the entities and iterate it.
            val list = new ArrayList<NPC>(npcs);

            for (NPC next : list) {
                //Unlink the region just in case the NPC prevents the region from being wiped.
                next.setRegion(null);
                if (next instanceof Follower) {
                    next.setLocation(new Location(0, 0, 0));
                    continue;
                } else if (next instanceof HunterDummyNPC) {
                    val dummy = (HunterDummyNPC) next;
                    val trapWeakReference = dummy.getTrap();
                    if (trapWeakReference != null) {
                        val trap = trapWeakReference.get();
                        if (trap != null) {
                            trap.remove();
                            continue;
                        }
                    }
                }
                next.remove();
            }
            npcs.clear();
            list.clear();
        });
    }

    @Synchronized
    public static void destroy(final AllocatedArea area) {
        for (int x = area.getMinRegionX(); x < (area.getMinRegionX() + area.getWidthInRegions()); x++) {
            for (int y = area.getMinRegionY(); y < (area.getMinRegionY() + area.getHeightInRegions()); y++) {
                val regionId = x << 8 | y;
                val region = World.getRegion(regionId);
                destroyRegion(region);
            }
        }
    }

    public static void copyAllPlanesMap(final AllocatedArea allocated, final int fromChunkX, final int fromChunkY, final int toChunkX, final int toChunkY, final int width, final int height) throws OutOfBoundaryException {
        copyMap(allocated, fromChunkX, fromChunkY, toChunkX, toChunkY, width, height, 0, 1, 2, 3);
    }

    public static void copySquare(final AllocatedArea allocated, final int ratio, final int fromChunkX, final int fromChunkY, final int fromPlane, final int toChunkX, final int toChunkY, final int toPlane, final int rotation) throws OutOfBoundaryException {
        val ratioOffset = ratio - 1;
        for (int x = 0; x < ratio; x++) {
            for (int y = 0; y < ratio; y++) {
                val xOffset = rotation == 0 || rotation == 1 ? x : rotation == 2 ? (ratioOffset - y) : (ratioOffset - x);
                val yOffset = rotation == 0 || rotation == 3 ? y : rotation == 1 ? (ratioOffset - y) : (ratioOffset - x);
                copyChunk(allocated, fromChunkX + (rotation == 0 ? x : y), fromChunkY + (rotation == 0 ? y : x), fromPlane, toChunkX + xOffset, toChunkY + yOffset, toPlane, rotation);
            }
        }
    }

    private static void copyChunk(final AllocatedArea allocated, final int fromChunkX, final int fromChunkY, final int fromPlane, final int toChunkX, final int toChunkY, final int toPlane, final int rotation) throws OutOfBoundaryException {
        val toRegion = getDynamicRegion(((toChunkX >> 3) << 8) + (toChunkY >> 3));
        allocated.verify(toChunkX, toChunkY);
        toRegion.setChunk(toChunkX - ((toChunkX >> 3) << 3), toChunkY - ((toChunkY >> 3) << 3), toPlane, toRegion.getHash(fromChunkX, fromChunkY, fromPlane, rotation));
    }

    private static void copyMap(final AllocatedArea allocated, final int fromChunkX, final int fromChunkY, final int toChunkX, final int toChunkY, final int width, final int height, final int... planes) throws OutOfBoundaryException {
        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int yOffset = 0; yOffset < height; yOffset++) {
                val toThisChunkX = toChunkX + xOffset;
                val toThisChunkY = toChunkY + yOffset;
                allocated.verify(toThisChunkX, toThisChunkY);
                val toRegion = getDynamicRegion(((toThisChunkX >> 3) << 8) + (toThisChunkY >> 3));
                for (val plane : planes) {
                    toRegion.setChunk((toThisChunkX - ((toThisChunkX >> 3) << 3)), (toThisChunkY - ((toThisChunkY >> 3) << 3)), plane, toRegion.getHash(fromChunkX + xOffset, fromChunkY + yOffset, plane, 0));
                }
            }
        }
    }

}
