package com.zenyte.game.world.region;

import com.zenyte.Game;
import com.zenyte.game.world.World;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.dynamicregion.CoordinateUtilities;
import mgi.types.config.ObjectDefinitions;
import it.unimi.dsi.fastutil.bytes.Byte2ByteOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import mgi.tools.jagcached.ArchiveType;
import mgi.utilities.ByteBuffer;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kris | 28. juuli 2018 : 18:39:38
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
@Slf4j
public final class DynamicRegion extends Region {

    private static final int MAX_UNSIGNED_BYTE = 256;
    private static final int INITIAL_CAPACITY = 1000;
    private static final byte IS_TILE_CLIPPED_FLAG = 0x1;
    private static final byte IS_TILE_INCREASED_HEIGHT_FLAG = 0x2;
    private static final Int2ObjectOpenHashMap<InnerChunk> MAPPED_CHUNKS = new Int2ObjectOpenHashMap<>(INITIAL_CAPACITY);
    private final ByteOpenHashSet reloadChunkList;
    private final Byte2IntOpenHashMap chunks;
    public DynamicRegion(final int regionId) {
        super(regionId);
        chunks = new Byte2IntOpenHashMap(MAX_UNSIGNED_BYTE);
        reloadChunkList = new ByteOpenHashSet(MAX_UNSIGNED_BYTE);
        for (int i = 0; i < MAX_UNSIGNED_BYTE; i++) {
            reloadChunkList.add((byte) i);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        reloadChunkList.clear();
        chunks.clear();
    }

    @Override
    public void load() {
        if (!reloadChunkList.isEmpty()) {
            setLoadStage(0);
        }
        super.load();
    }

    @Override
    public void loadRegionMap() {
        val iterator = reloadChunkList.iterator();
        val map = getRegionMap(true);
        val baseX = (regionId >> 8) << 6;
        val baseY = (regionId & 0xff) << 6;
        while (iterator.hasNext()) {
            /* Next hash points to the chunk within this region that has been updated. */
            val nextHash = iterator.nextByte();
            val chunkHash = chunks.get(nextHash);
            if (chunkHash == 0) {
                continue;
            }

            val chunkXInRegion = (nextHash & 0x7) << 3;
            val chunkYInRegion = ((nextHash >> 3) & 0x7) << 3;
            val z = (nextHash >> 6) & 0x3;
           addMultiZone(chunkXInRegion >> 3, chunkYInRegion >> 3, z, chunkHash);

            /* From chunk hash contains a hashcode pointer to the chunk that we're copying. */
            val fromChunkHash = chunks.get(nextHash);

            if (!MAPPED_CHUNKS.containsKey(fromChunkHash & 0x3FFFFF)) {
                loadRegionFromChunk(fromChunkHash & 0x3FFFFF);
            }
            try {
                val chunkX = chunkHash & 0x7FF;
                val chunkY = (chunkHash >> 11) & 0x7FF;
                val regionId = (chunkX >> 3) << 8 | (chunkY >> 3);
                getMusicTracks().addAll(World.getRegion(regionId).getMusicTracks());
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
            val innerChunk = MAPPED_CHUNKS.get(fromChunkHash & 0x3FFFFF);
            if (innerChunk != null) {
                val fromPlane = (fromChunkHash >> 22) & 0x3;
                val rotation = (fromChunkHash >> 24) & 0x3;
                for (int x = 0; x < 8; x++) {
                    for (int y = 0; y < 8; y++) {
                        val hash = getChunkHashInRegion(x, y, fromPlane);
                        val tileHash = innerChunk.fullyClipped ? IS_TILE_CLIPPED_FLAG : innerChunk.clipSettings.get(hash);
                        val objects = innerChunk.objects.get(hash);
                        int plane = z;

                        val firstLevelHash = innerChunk.fullyClipped ? IS_TILE_CLIPPED_FLAG : innerChunk.clipSettings.get(getChunkHashInRegion(x, y, 1));

                        if ((firstLevelHash & IS_TILE_INCREASED_HEIGHT_FLAG) == IS_TILE_INCREASED_HEIGHT_FLAG) {
                            plane--;
                        }
                        if (plane < 0 || plane > 3)
                            continue;

                        if ((tileHash & IS_TILE_CLIPPED_FLAG) == IS_TILE_CLIPPED_FLAG) {
                            val coordinates = CoordinateUtilities.translate(x, y, rotation);
                            val xInRegion = chunkXInRegion + coordinates[0];
                            val yInRegion = chunkYInRegion + coordinates[1];
                            map.setFloor(plane, xInRegion, yInRegion, true);
                        }
                        if (objects != null) {
                            for (val object : objects) {
                                val definitions = ObjectDefinitions.get(object.getId());
                                if (definitions == null)
                                    continue;
                                val coordinates = CoordinateUtilities.translate(x, y, rotation, definitions.getSizeX(), definitions.getSizeY(), object.getRotation());
                                val xInRegion = chunkXInRegion + coordinates[0];
                                val yInRegion = chunkYInRegion + coordinates[1];
                                val obj = new WorldObject(object.getId(), object.getType(), (rotation + object.getRotation()) & 0x3, xInRegion + baseX, yInRegion + baseY, plane);
                                if (obj.getRegionId() != regionId) {
                                    World.getRegion(obj.getRegionId(), true).spawnObject(obj, plane, obj.getXInRegion(), obj.getYInRegion(), true, true);
                                    continue;
                                }
                                spawnObject(obj, plane, obj.getXInRegion(), obj.getYInRegion(), true, true);
                            }
                        }
                    }
                }

            }
        }
        reloadChunkList.clear();
    }

    public final int getHash(final int x, final int y, final int z, final int rotation) {
        return x | y << 11 | z << 22 | rotation << 24;
    }

    public final void setChunk(final int x, final int y, final int z, final int hash) {
        val mapHash = (byte) (x & 0x7 | (y & 0x7) << 3 | (z & 0x3) << 6);
        if (hash == 0) {
            chunks.remove(mapHash);
        } else {
            chunks.put(mapHash, hash);
        }
        reloadChunkList.add(mapHash);
    }

    public final int getLocationHash(final int x, final int y, final int z) {
        return chunks.get((byte) (x | y << 3 | z << 6));
    }

    public final void clearMultiZones() {
        val keyset = chunks.keySet();
        val iterator = keyset.iterator();
        while (iterator.hasNext()) {
            val nextHash = iterator.nextByte();
            val chunkX = nextHash & 0x7;
            val chunkY = (nextHash >> 3) & 0x7;
            val z = (nextHash >> 6) & 0x3;
            removeMultiZone(((regionId >> 8) << 3) + chunkX, ((regionId & 0xFF) << 3) + chunkY, z);
        }
    }

    private final void loadRegionFromChunk(final int chunkHash) {
        val chunkX = chunkHash & 0x7FF;
        val chunkY = (chunkHash >> 11) & 0x7FF;
        val regionId = (chunkX >> 3) << 8 | (chunkY >> 3);
        cacheRegionAsChunks(regionId);
    }

    private final void cacheRegionAsChunks(final int regionId) {
        try {
            val xteas = XTEALoader.getXTEAs(regionId);

            val cache = Game.getCacheMgi();
            val archive = cache.getArchive(ArchiveType.MAPS);

            val mapGroup = archive.findGroupByName("m" + (regionId >> 8) + "_" + (regionId & 0xFF));
            val landGroup = archive.findGroupByName("l" + (regionId >> 8) + "_" + (regionId & 0xFF), xteas);

            val mapBuffer = mapGroup == null ? null : mapGroup.findFileByID(0).getData();
            val landBuffer = landGroup == null ? null : landGroup.findFileByID(0).getData();

            if (mapBuffer != null) {
                mapBuffer.setPosition(0);
                splitRegionClipSettingsIntoChunks(regionId, mapBuffer);
            }
            if (landBuffer != null) {
                landBuffer.setPosition(0);
                splitRegionObjectsIntoChunks(regionId, landBuffer);
            }

        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    private void splitRegionObjectsIntoChunks(final int regionId, final ByteBuffer buffer) {
        val minChunkX = (regionId >> 8) << 3;
        val minChunkY = (regionId & 0xFF) << 3;
        int objectId = -1;
        int objectIdDifference;
        while ((objectIdDifference = buffer.readHugeSmart()) != 0) {
            objectId += objectIdDifference;
            int location = 0;
            int locationHashDifference;
            while ((locationHashDifference = buffer.readUnsignedSmart()) != 0) {
                location += locationHashDifference - 1;
                val hash = buffer.readUnsignedByte();
                val x = (location >> 6 & 0x3F);
                val y = (location & 0x3F);
                val z = location >> 12 & 0x3;
                val type = hash >> 2;
                val rotation = hash & 0x3;

                val innerChunk = getInnerChunk(getChunkHash(minChunkX + (x >> 3), minChunkY + (y >> 3)));
                val object = new DynamicObject(objectId, type, rotation);
                val localTileHash = getChunkHashInRegion(x - (x >> 3 << 3), y - (y >> 3 << 3), z);
                var list = innerChunk.objects.get(localTileHash);
                if (list == null) {
                    list = new ArrayList<>();
                    innerChunk.objects.put(localTileHash, list);
                }
                list.add(object);
            }
        }
    }

    private final void splitRegionClipSettingsIntoChunks(final int regionId, final ByteBuffer mapBuffer) {
        val minChunkX = regionId >> 8 << 3;
        val minChunkY = (regionId & 0xFF) << 3;
        val increment = mapBuffer == null ? 8 : 1;
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 64; x += increment) {
                for (int y = 0; y < 64; y += increment) {
                    val innerChunk = getInnerChunk(getChunkHash(minChunkX + (x >> 3), minChunkY + (y >> 3)));
                    if (mapBuffer == null) {
                        innerChunk.fullyClipped = true;
                        continue;
                    }
                    while (true) {
                        val value = mapBuffer.readUnsignedByte();
                        if (value == 0) {
                            break;
                        } else if (value == 1) {
                            mapBuffer.readByte();
                            break;
                        } else if (value <= 49) {
                            mapBuffer.readByte();
                        } else if (value <= 81) {
                            innerChunk.clipSettings.put(getChunkHashInRegion(x - (x >> 3 << 3), y - (y >> 3 << 3), z), (byte) (value - 49));
                        }
                    }
                }
            }
        }
    }

    private final InnerChunk getInnerChunk(final int hash) {
        InnerChunk chunk = MAPPED_CHUNKS.get(hash);
        if (chunk == null) {
            MAPPED_CHUNKS.put(hash, chunk = new InnerChunk());
            return chunk;
        }
        return chunk;
    }

    private final int getChunkHash(final int x, final int y) {
        return x | y << 11;
    }

    private final byte getChunkHashInRegion(final int x, final int y, final int z) {
        return (byte) (x | y << 3 | z << 6);
    }

    private static final class InnerChunk {

        private final Byte2ByteOpenHashMap clipSettings;
        private final Byte2ObjectOpenHashMap<List<DynamicObject>> objects;
        private boolean fullyClipped;

        InnerChunk() {
            clipSettings = new Byte2ByteOpenHashMap(MAX_UNSIGNED_BYTE);
            objects = new Byte2ObjectOpenHashMap<>(MAX_UNSIGNED_BYTE);
        }

    }

    private static final class DynamicObject {

        private final int objectHash;

        DynamicObject(final int id, final int type, final int rotation) {
            objectHash = (id & 0xFFFF) | ((type & 0x1F) << 16) | ((rotation & 0x3) << 21);
        }

        public int getId() {
            return objectHash & 0xFFFF;
        }

        public final int getType() {
            return (objectHash >> 16) & 0x1F;
        }

        public final int getRotation() {
            return (objectHash >> 21) & 0x3;
        }
    }

}
