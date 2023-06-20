package mgi.types.worldmap;

import com.zenyte.Game;
import com.zenyte.game.world.region.XTEALoader;
import mgi.types.config.ObjectDefinitions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kris | 11/12/2018 20:07
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class WorldMapUtils {

    public static WorldMapGameObject[][][][] getWorldMapObjects(final int regionId, int plane) throws IOException {
        val cache = Game.getCacheMgi();
        val archive = cache.getArchive(ArchiveType.MAPS);
        val xteas = XTEALoader.getXTEAs(regionId);

        val landGroup = archive.findGroupByName("l" + (regionId >> 8) + "_" + (regionId & 0xFF), xteas);
        if (landGroup == null) {
            throw new RuntimeException("Map file is null!");
        }
        val buffer = landGroup.findFileByID(0).getData();
        final WorldMapGameObject[][][][] objects = new WorldMapGameObject[4][64][64][];
        int lastX = 0, lastY = 0, lastZ = 0;
        List<WorldMapGameObject> list = new ArrayList<>(4);
        val settings = getClipSettings(regionId);
        int objectId = -1;
        int objectIdDifference;
        buffer.setPosition(0);
        while ((objectIdDifference = buffer.readHugeSmart()) != 0) {
            objectId += objectIdDifference;
            int location = 0;
            int locationHashDifference;
            while ((locationHashDifference = buffer.readUnsignedSmart()) != 0) {
                location += locationHashDifference - 1;
                val hash = buffer.readUnsignedByte();
                val x = (location >> 6 & 0x3F);
                val y = (location & 0x3F);
                int realZ = location >> 12 & 0x3;
                if ((settings[1][x][y] & 0x2) == 2) {
                    realZ--;
                }
                if (realZ != plane)
                    continue;
                val type = hash >> 2;
                val rotation = hash & 0x3;
                if (lastX != x || lastY != y || lastZ != realZ) {
                    if (!list.isEmpty()) {
                        objects[lastZ - plane][lastX][lastY] = list.toArray(new WorldMapGameObject[0]);
                        list.clear();
                    }
                    lastX = x;
                    lastY = y;
                    lastZ = realZ;
                }
                val definitions = ObjectDefinitions.get(objectId);
                /*if (definitions == null) {
                    throw new RuntimeException("Object id " + objectId + " does not exist.");
                }*/
                if (definitions != null) {
                    if (definitions.getMapSceneId() != -1 || definitions.getMapIconId() != -1 || type == 0 || type == 2 || type == 3 || type == 9) {
                        list.add(new WorldMapGameObject(objectId, type, rotation));
                    }
                }
            }
        }
        if (!list.isEmpty()) {
            objects[lastZ][lastX][lastY] = list.toArray(new WorldMapGameObject[0]);
        }

        return objects;
    }

    public static OverlayInformation getOverlays(final int regionId, int baseHeight) throws IOException {
        val cache = Game.getCacheMgi();
        val archive = cache.getArchive(ArchiveType.MAPS);

        val mapGroup = archive.findGroupByName("m" + (regionId >> 8) + "_" + (regionId & 0xFF));
        if (mapGroup == null) {
            throw new RuntimeException("Map file is null!");
        }
        val buffer = mapGroup.findFileByID(0).getData();
        buffer.setPosition(0);
        short[][][] ids = new short[4 - baseHeight][64][64];
        byte[][][] rotations = new byte[4 - baseHeight][64][64];
        byte[][][] shapes = new byte[4 - baseHeight][64][64];
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 64; x ++) {
                for (int y = 0; y < 64; y ++) {
                    while (true) {
                        val attribute = buffer.readUnsignedByte();
                        if (attribute == 0) {
                            break;
                        } else if (attribute == 1) {
                            buffer.readByte();
                            break;
                        } else if (attribute <= 49) {
                            val height = buffer.readByte();
                            if (z < baseHeight)
                                continue;
                            ids[z - baseHeight][x][y] = height;
                            shapes[z - baseHeight][x][y] = (byte) ((attribute - 2) / 4);
                            rotations[z - baseHeight][x][y] = (byte) (attribute - 2 & 0x3);
                        }
                    }
                }
            }
        }
        val settings = getClipSettings(regionId);
        for (int z = 0; z < 3 - baseHeight; z++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    if ((settings[1][x][y] & 0x2) == 2) {
                        if (z <= 0) continue;
                        ids[z][x][y] = ids[z - 1][x][y];
                        shapes[z][x][y] = shapes[z - 1][x][y];
                        rotations[z][x][y] = rotations[z - 1][x][y];
                    }
                }
            }
        }
        return new OverlayInformation(ids, shapes, rotations);
    }

    public static int[][][] getClipSettings(final int regionId) throws IOException {
        val cache = Game.getCacheMgi();
        val archive = cache.getArchive(ArchiveType.MAPS);

        val mapGroup = archive.findGroupByName("m" + (regionId >> 8) + "_" + (regionId & 0xFF));
        if (mapGroup == null) {
            throw new RuntimeException("Map file is null!");
        }
        val buffer = mapGroup.findFileByID(0).getData();
        buffer.setPosition(0);
        val settings = new int[4][64][64];
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 64; x ++) {
                for (int y = 0; y < 64; y ++) {
                    while (true) {
                        val attribute = buffer.readUnsignedByte();
                        if (attribute == 0) {
                            break;
                        } else if (attribute == 1) {
                            buffer.readByte();
                            break;
                        } else if (attribute <= 49) {
                            buffer.readByte();
                        } else if (attribute <= 81) {
                            settings[z][x][y] = (byte) (attribute - 49);
                        }
                    }
                }
            }
        }
        return settings;
    }


    public static short[][][] getUnderlays(final int regionId, int baseHeight) throws IOException {
        val cache = Game.getCacheMgi();
        val archive = cache.getArchive(ArchiveType.MAPS);

        val mapGroup = archive.findGroupByName("m" + (regionId >> 8) + "_" + (regionId & 0xFF));
        if (mapGroup == null) {
            throw new RuntimeException("Map file is null!");
        }
        val buffer = mapGroup.findFileByID(0).getData();
        buffer.setPosition(0);
        val increment = 1;
        val underlays = new short[4 - baseHeight][64][64];
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 64; x += increment) {
                for (int y = 0; y < 64; y += increment) {
                    while (true) {
                        val attribute = buffer.readUnsignedByte();
                        if (attribute == 0) {
                            break;
                        } else if (attribute == 1) {
                            buffer.readByte();
                            break;
                        } else if (attribute > 81) {
                            if (z < baseHeight)
                                continue;
                            underlays[z - baseHeight][x][y] = (byte) (attribute - 81);
                        }
                    }
                }
            }
        }
        return underlays;
    }


    @Getter
    @RequiredArgsConstructor
    public static class OverlayInformation {

        private final short[][][] ids;
        private final byte[][][] shapes, rotations;
    }


}
