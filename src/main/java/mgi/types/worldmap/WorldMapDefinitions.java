package mgi.types.worldmap;

import com.google.common.base.Preconditions;
import com.zenyte.Game;
import com.zenyte.game.world.entity.Location;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.File;
import mgi.tools.jagcached.cache.Group;
import mgi.types.config.UnderlayDefinitions;
import mgi.utilities.ByteBuffer;
import org.apache.logging.log4j.util.Strings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Kris | 07/01/2019 19:24
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * World map scrolls in script worldmap_elements_update:1702
 * World map links in script 1705.
 */
@Slf4j
public class WorldMapDefinitions {

    private int fileId;
    private String name;
    private String identifier;
    private Location location;
    private int field187;
    private boolean defaultCategory;
    private int defaultZoom;
    private int field191;
    private int field193;
    private int minX;
    private int minY;
    private LinkedList<WorldMapType> types;

    private Int2ObjectOpenHashMap<WorldMapRegion> regions;
    private HashSet<WorldMapChunk> chunks;

    private List<MapElement> elements;

    public static WorldMapDefinitions decode(final String areaName) {
        try {

            val archive = Game.getCacheMgi().getArchive(ArchiveType.WORLDMAPDATA);
            val detailsGroup = archive.findGroupByName("details");
            val compositeMapGroup = archive.findGroupByName("compositemap");

            val detailsBuffer = detailsGroup.findFileByName(areaName).getData();
            val compositeMapBuffer = compositeMapGroup.findFileByName(areaName).getData();

            val id = archive.findGroupByName(areaName).getID();

            val defs = new WorldMapDefinitions();
            defs.decode(detailsBuffer, compositeMapBuffer, id);
            return defs;
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
        throw new IllegalStateException();
    }

    private void decode(final ByteBuffer detailsBuffer, final ByteBuffer compositeMapBuffer, final int fileId) {
        decodeDetails(detailsBuffer, fileId);

        var size = compositeMapBuffer.readUnsignedShort();
        regions = new Int2ObjectOpenHashMap<>(size);
        for (int count = 0; count < size; ++count) {
            val region = new WorldMapRegion();
            region.decode(compositeMapBuffer);
            regions.put((region.regionX << 8) | region.regionY, region);
        }

        size = compositeMapBuffer.readUnsignedShort();
        chunks = new HashSet<>(size);
        for (int count = 0; count < size; ++count) {
            val chunk = new WorldMapChunk();
            chunk.decode(compositeMapBuffer);
            chunks.add(chunk);
            if (chunk.expectedRegionX == 45 && chunk.expectedRegionY == 83 && chunk.expectedChunkX == 6 && chunk.expectedChunkY == 4) {
                //System.err.println(chunk);
                /*for (int z = 0; z < chunk.objects.length; z++) {
                    for (int x = 0; x < chunk.objects[z].length; x++) {
                        for (int y = 0; y < chunk.objects[z][x].length; y++) {
                            if (chunk.objects[z][x][y] == null) {
                                continue;
                            }
                            for (int i = 0; i < chunk.objects[z][x][y].length; i++) {
                                val obj = chunk.objects[z][x][y][i];
                                if (obj != null) {
                                    obj.setId(26486);
                                    obj.setType(10);
                                    System.err.println(z + ", " + x + ", " + y + ", " + i + ", " + obj);
                                }
                            }
                        }
                    }
                }*/
            }
        }

        elements = new LinkedList<>();
        int length = compositeMapBuffer.readUnsignedShort();
        for (int count = 0; count < length; ++count) {
            val var5 = compositeMapBuffer.readBigSmart();
            val location = new Location(compositeMapBuffer.readInt());
            val serverOnly = compositeMapBuffer.readUnsignedByte() == 1;
            val element = new MapElement(serverOnly, var5, location);
            elements.add(element);
        }
    }
    public void updateFullChunks(final int regionId, final int plane) {
        updateFullChunks(regionId, regionId, plane, 0, 0);
    }
    public void updateFullChunks(final int fromRegionId, final int regionId, final int plane, final int offsetChunksX, final int offsetChunksY) {
        Preconditions.checkArgument(regionId >= 0 && regionId <= 0xFFFF, "Out of game map boundaries.");
        try {
            val rx = regionId >> 8;
            val ry = regionId & 0xFF;

            val overlayInfo = WorldMapUtils.getOverlays(fromRegionId, plane);
            val objects = WorldMapUtils.getWorldMapObjects(fromRegionId, plane);
            val underlays = WorldMapUtils.getUnderlays(fromRegionId, plane);

            val xoff = (offsetChunksX * 8);
            val yoff = (offsetChunksY * 8);

            if (xoff > 0 || yoff > 0) {
                val copyOverlayInfo = WorldMapUtils.getOverlays(fromRegionId, plane);
                val copyObjects = WorldMapUtils.getWorldMapObjects(fromRegionId, plane);
                val copyUnderlays = WorldMapUtils.getUnderlays(fromRegionId, plane);
                for (int z = 0; z < copyObjects.length; z++) {
                    for (int x = 0; x < copyObjects[z].length - xoff; x++) {
                        if (copyObjects[z][x].length - yoff >= 0) System.arraycopy(copyObjects[z][x], 0, objects[z][x + xoff], yoff, copyObjects[z][x].length - yoff);
                    }
                }

                for (int z = 0; z < copyUnderlays.length; z++) {
                    for (int x = 0; x < copyUnderlays[z].length - xoff; x++) {
                        if (copyUnderlays[z][x].length - yoff >= 0) System.arraycopy(copyUnderlays[z][x], 0, underlays[z][x + xoff], yoff, copyUnderlays[z][x].length - yoff);
                    }
                }

                for (int z = 0; z < copyOverlayInfo.getIds().length; z++) {
                    for (int x = 0; x < copyOverlayInfo.getIds()[z].length - xoff; x++) {
                        if (copyOverlayInfo.getIds()[z][x].length - yoff >= 0)
                            System.arraycopy(copyOverlayInfo.getIds()[z][x], 0, overlayInfo.getIds()[z][x + xoff], yoff, copyOverlayInfo.getIds()[z][x].length - yoff);
                    }
                }

                for (int z = 0; z < copyOverlayInfo.getRotations().length; z++) {
                    for (int x = 0; x < copyOverlayInfo.getRotations()[z].length - xoff; x++) {
                        if (copyOverlayInfo.getRotations()[z][x].length - yoff >= 0)
                            System.arraycopy(copyOverlayInfo.getRotations()[z][x], 0, overlayInfo.getRotations()[z][x + xoff], yoff, copyOverlayInfo.getRotations()[z][x].length - yoff);
                    }
                }

                for (int z = 0; z < copyOverlayInfo.getShapes().length; z++) {
                    for (int x = 0; x < copyOverlayInfo.getShapes()[z].length - xoff; x++) {
                        if (copyOverlayInfo.getShapes()[z][x].length - yoff >= 0)
                            System.arraycopy(copyOverlayInfo.getShapes()[z][x], 0, overlayInfo.getShapes()[z][x + xoff], yoff, copyOverlayInfo.getShapes()[z][x].length - yoff);
                    }
                }
            }

            for (val chunk : chunks) {
                //if (regionId == 11602)
                if (!(chunk.regionX == rx && chunk.regionY == ry)/* || chunk.minPlane != plane && chunk.maxPlane != plane*/) {
                    continue;
                }
                val min = Math.min(chunk.minPlane, chunk.maxPlane);
                val max = Math.max(chunk.minPlane, chunk.maxPlane);
                if (!(plane >= min && plane <= max)) {
                    continue;
                }
                /*if (plane != min) {
                    continue;
                }*/
                try {
                    chunk.offsetChunksX = offsetChunksX;
                    chunk.offsetChunksY = offsetChunksY;
                    chunk.setObjects(objects);
                    chunk.setUnderlays(underlays);
                    chunk.setOverlays(overlayInfo.getIds());
                    chunk.setOverlayRotations(overlayInfo.getRotations());
                    chunk.setOverlayShapes(overlayInfo.getShapes());

                    val flags = chunk.flags;
                    for (int cx = 0; cx < 8; cx++) {
                        for (int cy = 0; cy < 8; cy++) {
                            int x = cx + chunk.chunkX * 8;
                            int y = cy + chunk.chunkY * 8;
                            var flag = flags[x][y] = chunk.overlayTileHeights[x][y] = 0;
                            var objectTileHeight = -1;

                            for (int z = 3 - plane; z >= 0; z--) {
                                if (chunk.objects[z][x][y] != null) {
                                    objectTileHeight = z;
                                    break;
                                }
                            }
                            for (int z = min; z < max; z++) {
                                if (chunk.overlays[z - min][x][y] != 0) {
                                    chunk.overlayTileHeights[x][y]++;
                                }
                            }
                            flag |= objectTileHeight << 3;
                            if (objectTileHeight > -1) {
                                flag |= 0x4;
                            }
                            if (chunk.overlayTileHeights[x][y] > 0) {
                                flag |= 0x2;
                            }
                            flags[x][y] = flag;
                        }
                    }
                } catch (Exception e) {
                    log.error(Strings.EMPTY, e);
                }
            }
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }


    public void update(final int regionId, final int plane) {
        Preconditions.checkArgument(regionId >= 0 && regionId <= 0xFFFF, "Out of game map boundaries.");
        val region = regions.get(regionId);
        try {
            val overlayInfo = WorldMapUtils.getOverlays(regionId, plane);
            region.setObjects(WorldMapUtils.getWorldMapObjects(regionId, plane));
            region.setUnderlays(WorldMapUtils.getUnderlays(regionId, plane));
            region.setOverlays(overlayInfo.getIds());
            region.setOverlayRotations(overlayInfo.getRotations());
            region.setOverlayShapes(overlayInfo.getShapes());

            val flags = region.flags;
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    var flag = flags[x][y] = region.overlayTileHeights[x][y] = 0;
                    var objectTileHeight = -1;

                    for (int z = 3 - plane; z >= 0; z--) {
                        if (region.objects[z][x][y] != null) {
                            objectTileHeight = z;
                            break;
                        }
                    }
                    region.overlayTileHeights[x][y] = 1;
                    flag |= objectTileHeight << 3;
                    if (objectTileHeight > -1) {
                        flag |= 0x4;
                    }
                    if (region.overlayTileHeights[x][y] > 0) {
                        flag |= 0x2;
                    }
                    flags[x][y] = flag;
                }
            }
        } catch (IOException e) {
            log.error(Strings.EMPTY, e);
        }
    }

    private enum Side {
        field3172(5, 0),
        field3163(6, 1),
        field3164(1, 2),
        field3162(0, 3),
        field3166(4, 4),
        field3169(3, 5),
        field3168(2, 6),
        field3167(7, 7);

        public final int field3170;
        final int field3171;

        Side(int var3, int var4) {
            this.field3170 = var3;
            this.field3171 = var4;
        }
    }

    void method4161(WorldMapNode[] var1, WorldMapNode var2) {
        Side[] var3 = new Side[]{Side.field3166, Side.field3172, Side.field3162, Side.field3168, Side.field3169, Side.field3164, Side.field3163, Side.field3167};
        Side[] var5 = var3;

        for (int var6 = 0; var6 < var5.length; ++var6) {
            Side var7 = var5[var6];
            if (var1[var7.field3171] != null) {
                byte var8 = 0;
                byte var9 = 0;
                byte var10 = 64;
                byte var11 = 64;
                byte var12 = 0;
                byte var13 = 0;
                switch (var7.field3170) {
                    case 0:
                        var13 = 59;
                        var11 = 5;
                        var8 = 59;
                        var10 = 5;
                        break;
                    case 1:
                        var8 = 59;
                        var10 = 5;
                        break;
                    case 2:
                        var12 = 59;
                        var10 = 5;
                        break;
                    case 3:
                        var12 = 59;
                        var13 = 59;
                        var10 = 5;
                        var11 = 5;
                        break;
                    case 4:
                        var13 = 59;
                        var11 = 5;
                        break;
                    case 5:
                        var9 = 59;
                        var11 = 5;
                        break;
                    case 6:
                        var9 = 59;
                        var11 = 5;
                        var8 = 59;
                        var10 = 5;
                        break;
                    case 7:
                        var9 = 59;
                        var11 = 5;
                        var12 = 59;
                        var10 = 5;
                }
                smoothen(var12, var13, var8, var9, var10, var11, var1[var7.field3171], var2);
            }
        }
    }

    void method902(int var1, int var2, WorldMapNode[] var3, WorldMapNode node) {
        if (var1 == 0 && var2 == 1) {
            var3[0] = node;
        } else if (var1 == 1 && var2 == 1) {
            var3[1] = node;
        } else if (var1 == -1 && var2 == 1) {
            var3[7] = node;
        } else if (var1 == 1 && var2 == 0) {
            var3[2] = node;
        } else if (var1 == -1 && var2 == 0) {
            var3[6] = node;
        } else if (var1 == 0 && var2 == -1) {
            var3[4] = node;
        } else if (var1 == 1 && var2 == -1) {
            var3[3] = node;
        } else if (var1 == -1 && var2 == -1) {
            var3[5] = node;
        }
    }


    private void smoothen(int var1, int var2, int var3, int var4, int var5, int var6, WorldMapNode alternative, WorldMapNode var8) {
        for (int var9 = 0; var9 < var5; ++var9) {
            for (int var10 = 0; var10 < var6; ++var10) {
                if ((var9 + var1) < 0 || (var9 + var1) > 63) {
                    continue;
                }
                if ((var10 + var2) < 0 || (var10 + var2) > 63) {
                    continue;
                }
                val definition = alternative.underlays[0][var9 + var1][var10 + var2] - 1;
                if (definition != -1) {
                    val defs = UnderlayDefinitions.get(definition);
                    var8.smoothenedPixel(var3 + var9, var10 + var4, 5, defs);
                }
            }
        }
    }


    public void encode(final String areaName) {
        val compositeMapBuffer = new ByteBuffer(15 * 1024);//15 kb
        val detailsBuffer = new ByteBuffer(1024);//1 kb
        /* Details **/
        detailsBuffer.writeString(identifier);
        detailsBuffer.writeString(name);
        detailsBuffer.writeInt(location.getPositionHash());
        detailsBuffer.writeInt(field187);
        detailsBuffer.writeByte(0);
        detailsBuffer.writeByte(defaultCategory ? 1 : 0);
        detailsBuffer.writeByte(defaultZoom);
        detailsBuffer.writeByte(types.size());
        for (val type : types) {
            type.encode(detailsBuffer);
        }

        /* Compositemap & Area **/
        compositeMapBuffer.writeShort(regions.size());
        for (val region : regions.values()) {
            compositeMapBuffer.writeByte(region.compositeMapCheck);
            if ((region.compositeMapCheck & 0xFF) != 0) {
                throw new IllegalStateException();
            }
            compositeMapBuffer.writeByte(region.minPlane);
            compositeMapBuffer.writeByte(region.maxPlane);
            compositeMapBuffer.writeShort(region.centerRegionX);
            compositeMapBuffer.writeShort(region.centerRegionY);
            compositeMapBuffer.writeShort(region.regionX);
            compositeMapBuffer.writeShort(region.regionY);
            compositeMapBuffer.writeBigSmart(region.a);
            compositeMapBuffer.writeBigSmart(region.b);
            val areaBuffer = new ByteBuffer(30000);//Largest is slightly over 25kb.
            areaBuffer.writeByte(region.areaCheck);
            if ((region.areaCheck & 0xFF) != 0) {
                throw new IllegalStateException();
            }
            areaBuffer.writeByte(region.expectedRegionX);
            areaBuffer.writeByte(region.expectedRegionY);
            if (region.expectedRegionX != region.regionX || region.expectedRegionY != region.regionY) {
                throw new IllegalStateException();
            }
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    region.encode(x, y, areaBuffer);
                }
            }
            val archive = Game.getCacheMgi().getArchive(ArchiveType.WORLDMAPGEOGRAPHY);
            archive.addGroup(new Group(region.a, new File(region.b, areaBuffer)));
        }

        val regs = new ArrayList<WorldMapNode>(regions.values());

        regs.sort((c1, c2) -> {
            val comparison = Integer.compare(c2.regionY, c1.regionY);
            if (comparison == 0) {
                return Integer.compare(c2.regionX, c1.regionX);
            }
            return comparison;
        });

        for (val region : regs) {
            val surroundingRegions = new WorldMapNode[8];
            val x = region.regionX;
            val y = region.regionY;

            for (val reg : regions.values()) {
                if (reg.regionX >= (x - 1) && reg.regionX <= (x + 1) && reg.regionY >= (y - 1) && reg.regionY <= (y + 1)) {
                    method902(reg.regionX - x, reg.regionY - y, surroundingRegions, reg);
                }
            }
            smoothen(0, 0, 0, 0, 64, 64, region, region);
            method4161(surroundingRegions, region);
        }

        val pool = ForkJoinPool.commonPool();
        val taskList = new ObjectArrayList<Callable<Void>>();
        for (val region : regions.values()) {
            taskList.add(() -> {
                val image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);

                for (int x = 0; x < 64; x++) {
                    for (int y = 0; y < 64; y++) {
                        image.setRGB(x, y, region.method5557(x, y) /*| -16777216*/);
                    }
                }
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", baos);
                    baos.flush();
                    byte[] imageInByte = baos.toByteArray();
                    baos.close();
                    val archive = Game.getCacheMgi().getArchive(ArchiveType.WORLDMAPGROUND);
                    archive.addGroup(new Group(region.a, new File(region.b, new ByteBuffer(imageInByte))));
                } catch (Exception e) {
                    log.error(Strings.EMPTY, e);
                }
                return null;
            });
        }
        pool.invokeAll(taskList);

        compositeMapBuffer.writeShort(chunks.size());
        for (val chunk : chunks) {
            compositeMapBuffer.writeByte(chunk.check1);
            compositeMapBuffer.writeByte(chunk.minPlane);
            compositeMapBuffer.writeByte(chunk.maxPlane);
            compositeMapBuffer.writeShort(chunk.centerRegionX);
            compositeMapBuffer.writeShort(chunk.centerRegionY);
            compositeMapBuffer.writeByte(chunk.field332);
            compositeMapBuffer.writeByte(chunk.field328);
            compositeMapBuffer.writeShort(chunk.regionX);
            compositeMapBuffer.writeShort(chunk.regionY);
            compositeMapBuffer.writeByte(chunk.chunkX);
            compositeMapBuffer.writeByte(chunk.chunkY);
            compositeMapBuffer.writeBigSmart(chunk.a);
            compositeMapBuffer.writeBigSmart(chunk.b);
            val areaBuffer = new ByteBuffer(15 * 1024);//15 kb
            areaBuffer.writeByte(chunk.check2);
            areaBuffer.writeByte(chunk.expectedRegionX);
            areaBuffer.writeByte(chunk.expectedRegionY);
            areaBuffer.writeByte(chunk.expectedChunkX);
            areaBuffer.writeByte(chunk.expectedChunkY);
            for (int chunkXOffset = 0; chunkXOffset < 8; ++chunkXOffset) {
                for (int chunkYOffset = 0; chunkYOffset < 8; ++chunkYOffset) {
                    val x = chunkXOffset + chunk.chunkX * 8;
                    val y = chunkYOffset + chunk.chunkY * 8;
                    chunk.encode(x, y, areaBuffer);
                }
            }

            /*

        val desktopArchive = cache.getArchive(ArchiveType.BINARY);
        desktopArchive.findGroupByID(0).findFileByID(0).setData(new ByteBuffer(desktop));

        val mobileArhive = desktopArchive.findGroupByID(2);
        mobileArhive.findFileByID(0).setData(new ByteBuffer(mobile));
        mobileArhive.addFile(new mgi.tools.jagcached.cache.File(new ByteBuffer(logo)));
             */
            val archive = Game.getCacheMgi().getArchive(ArchiveType.WORLDMAPGEOGRAPHY);
            archive.findGroupByID(chunk.a).findFileByID(chunk.b).setData(areaBuffer);
            //archive.addGroup(new Group(chunk.a, new File(chunk.b, areaBuffer)));
        }

        /* Area */
        compositeMapBuffer.writeShort(elements.size());
        for (val element : elements) {
            compositeMapBuffer.writeBigSmart(element.getId());
            val location = element.getLocation();
            val labelText = MapElementDefinitions.get(element.getId()).getText();
            if ("Edgeville".equals(labelText)) {
                location.setLocation(new Location(3088, 3501, 0));
            }
            compositeMapBuffer.writeInt(location.getPositionHash());
            compositeMapBuffer.writeByte((element.isServerOnly() ? 1 : 0));
        }

        val archive = Game.getCacheMgi().getArchive(ArchiveType.WORLDMAPDATA);
        val compositeMapGroup = archive.findGroupByName("compositemap");
        compositeMapGroup.findFileByName(areaName).setData(compositeMapBuffer);
    }

    private void decodeDetails(final ByteBuffer buffer, final int fileId) {
        this.fileId = fileId;
        identifier = buffer.readString();
        name = buffer.readString();
        location = new Location(buffer.readInt());
        field187 = buffer.readInt();
        buffer.readByte();
        defaultCategory = buffer.readUnsignedByte() == 1;
        defaultZoom = buffer.readUnsignedByte();
        val size = buffer.readUnsignedByte();
        types = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            val type = loadType(buffer);
            types.add(type);
        }
    }

    private WorldMapType loadType(final ByteBuffer buffer) {
        WorldMapType type;
        val identifier = buffer.readUnsignedByte();
        switch (identifier) {
            case 0:
                type = new AdjustedRegionWMArea();
                break;
            case 1:
                type = new RegionWMArea();
                break;
            case 2:
                type = new AdjustedChunkWMArea();
                break;
            case 3:
                type = new ChunkWMArea();
                break;
            default:
                throw new IllegalStateException();
        }
        type.decode(buffer);
        return type;
    }

}
