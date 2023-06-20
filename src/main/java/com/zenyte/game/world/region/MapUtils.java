package com.zenyte.game.world.region;

import com.google.common.base.Preconditions;
import com.zenyte.Game;
import com.zenyte.game.world.object.WorldObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import lombok.Data;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.utilities.ByteBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Kris | 03/04/2019 14:20
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class MapUtils {

    public static final ByteBuffer encode(@NotNull final Collection<WorldObject> objects) {
        val buffer = new ByteBuffer(1024 * 10 * 10);
        val map = new Int2ObjectLinkedOpenHashMap<List<WorldObject>>();
        val list = new ArrayList<WorldObject>(objects);
        list.sort((c1, c2) -> {
            val c1id = c1.getId();
            val c2id = c2.getId();
            if (c1id == c2id) {
                return Integer.compare(c1.hashInRegion(), c2.hashInRegion());
            }
            return Integer.compare(c1id, c2id);
        });

        for (val o : list) {
            map.computeIfAbsent(o.getId(), i -> new ArrayList<>()).add(o);
        }

        int lastId = -1;
        int lastHash = 0;

        for (val entry : map.int2ObjectEntrySet()) {
            buffer.writeHugeSmart(entry.getIntKey() - lastId);
            lastId = entry.getIntKey();
            for (val value : entry.getValue()) {
                val x = value.getX() & 0x3F;
                val y = value.getY() & 0x3F;
                val z = value.getPlane() & 0x3;
                val hash = (x << 6) | (y) | (z << 12);
                buffer.writeSmart(1 + hash - lastHash);
                lastHash = hash;
                buffer.writeByte(value.getRotation() | (value.getType() << 2));
            }
            lastHash = 0;
            buffer.writeHugeSmart(0);
        }
        buffer.writeHugeSmart(0);
        return buffer;
    }

    public static final Collection<WorldObject> decode(@NotNull final ByteBuffer buffer) {
        val collection = new ArrayList<WorldObject>();
        int objectId = -1;
        int incr;
        buffer.setPosition(0);
        while ((incr = buffer.readHugeSmart()) != 0) {
            objectId += incr;
            int location = 0;
            int incr2;
            while ((incr2 = buffer.readUnsignedSmart()) != 0) {
                location += incr2 - 1;
                val localX = (location >> 6 & 0x3f);
                val localY = (location & 0x3f);
                val plane = location >> 12;
                val objectData = buffer.readUnsignedByte();
                val type = objectData >> 2;
                val rotation = objectData & 0x3;
                collection.add(new WorldObject(objectId, type, rotation, localX, localY, plane));
            }
        }
        return collection;
    }

    public static final Collection<WorldObject> decodeNew(@NotNull final ByteBuffer buffer) {
        System.out.println("Buffer size: " + buffer.getBuffer().length);
        val collection = new ArrayList<WorldObject>();
        int objectId = -1;
        int incr;
        buffer.setPosition(0);

        while ((incr = buffer.readHugeSmart()) != 0) {
            System.out.println("reading...");
            objectId += incr;
            int location = 0;
            int incr2;
            while ((incr2 = buffer.readUnsignedSmart()) != 0) {
                location += incr2 - 1;
                val localX = (location >> 6 & 0x3f);
                val localY = (location & 0x3f);
                val plane = location >> 12;
                val objectData = buffer.readUnsignedByte();
                val type = objectData >> 2;
                val rotation = objectData & 0x3;
                collection.add(new WorldObject(objectId, type, rotation, localX, localY, plane));
            }
        }
        return collection;
    }

    @Data
    public static class Tile
    {
        private final int x, y, z;
        public Integer height;
        public byte settings;
        public byte overlayId;
        public byte overlayPath;
        public byte overlayRotation;
        public boolean forceOverlay;
        public byte underlayId;
    }

    public static ByteBuffer processTiles(@NotNull final ByteBuffer buffer, @NotNull final Consumer<Tile> consumer) {
        Tile[][][] tiles = new Tile[4][64][64];
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    Tile tile = tiles[z][x][y] = new Tile(x, y, z);
                    while (true) {
                        int attribute = buffer.readUnsignedByte();
                        if (attribute == 0) {
                            break;
                        } else if (attribute == 1) {
                            int height = buffer.readUnsignedByte();
                            tile.height = height;
                            break;
                        } else if (attribute <= 49) {
                            //tile.attrOpcode = attribute;
                            tile.overlayId = buffer.readByte();
                            tile.overlayPath = (byte) ((attribute - 2) / 4);
                            tile.overlayRotation = (byte) (attribute - 2 & 3);
                        } else if (attribute <= 81) {
                            tile.settings = (byte) (attribute - 49);
                        } else {
                            tile.underlayId = (byte) (attribute - 81);
                        }
                    }
                }
            }
        }
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    consumer.accept(tiles[z][x][y]);
                }
            }
        }
        ByteBuffer buf = new ByteBuffer(1024 * 10 * 10);
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    val tile = tiles[z][x][y];
                    if (tile.overlayId != 0 || tile.forceOverlay) {
                        val opcode = 2 + ((tile.overlayRotation & 3) | (tile.overlayPath << 2));
                        buf.writeByte(opcode);
                        buf.writeByte(tile.overlayId);
                    }
                    if (tile.settings != 0) {
                        buf.writeByte(tile.settings + 49);
                    }
                    if (tile.underlayId != 0) {
                        buf.writeByte(tile.underlayId + 81);
                    }
                    if (tile.height != null) {
                        buf.writeByte(1);
                        buf.writeByte(tile.height.intValue());
                        continue;
                    }
                    buf.writeByte(0);
                }
            }
        }
        return buf;
    }

    public static final byte[] inject(final byte[] buffer, @Nullable final Predicate<WorldObject> filter, @NotNull final WorldObject... objects) {
        try {
            val landBuffer = new ByteBuffer(buffer);
            val mapObjects = decode(landBuffer);
            mapObjects.removeIf(obj -> {
                val mapObjHash = obj.hashInRegion();
                val mapObjSlot = Region.OBJECT_SLOTS[obj.getType()];
                for (val o : objects) {
                    val hash = o.hashInRegion();
                    val slot = Region.OBJECT_SLOTS[o.getType()];
                    if (hash == mapObjHash && slot == mapObjSlot) {
                        return true;
                    }
                }
                return false;
            });
            if (filter != null) {
                mapObjects.removeIf(filter);
            }
            mapObjects.addAll(Arrays.asList(objects));
            val encoded = encode(mapObjects);
            val newBuffer = new ByteBuffer(encoded.getBuffer());
            return newBuffer.getBuffer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final byte[] inject(final int regionId, @Nullable final Predicate<WorldObject> filter, @NotNull final WorldObject... objects) {
        try {
            val xteas = XTEALoader.getXTEAs(regionId);
            val cache = Game.getCacheMgi();
            val archive = cache.getArchive(ArchiveType.MAPS);
            val landGroup = archive.findGroupByName("l" + (regionId >> 8) + "_" + (regionId & 0xFF), xteas);
            val locFileId = landGroup == null ? -1 : landGroup.getID();
            Preconditions.checkArgument(locFileId != -1);
            ByteBuffer landBuffer = landGroup == null ? null : landGroup.findFileByID(0).getData();
            val mapObjects = decode(landBuffer);
            mapObjects.removeIf(obj -> {
                val mapObjHash = ((obj.getX() & 0x3F) << 6) | (obj.getY() & 0x3F) | ((obj.getPlane() & 0x3) << 12);
                val mapObjSlot = Region.OBJECT_SLOTS[obj.getType()];
                for (val o : objects) {
                    val hash = ((o.getX() & 0x3F) << 6) | (o.getY() & 0x3F) | ((o.getPlane() & 0x3) << 12);
                    val slot = Region.OBJECT_SLOTS[o.getType()];
                    if (hash == mapObjHash && slot == mapObjSlot) {
                        return true;
                    }
                }
                return false;
            });
            if (filter != null) {
                mapObjects.removeIf(filter);
            }
            mapObjects.addAll(Arrays.asList(objects));
            val encoded = encode(mapObjects);
            val newBuffer = new ByteBuffer(encoded.getBuffer());
            return newBuffer.getBuffer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
