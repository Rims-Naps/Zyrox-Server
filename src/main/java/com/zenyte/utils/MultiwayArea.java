package com.zenyte.utils;

import com.zenyte.Game;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.region.Chunk;
import com.zenyte.game.world.region.RSPolygon;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.val;
import lombok.var;
import mgi.tools.jagcached.ArchiveType;

import java.awt.*;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kris | 26. sept 2018 : 23:07:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a> TODO: Convert force multi area method from entity/region
 */
@Log
public class MultiwayArea implements MapPrinter {

    private static final Area[] MULTIWAY_AREA_POLYGONS = new Area[4];

    @Getter
    private static final List<List<Area>> polygons = new ArrayList<>(4);
    private static final int MIN_PLANE = 0;

    private static final int MAX_PLANE = 1;
    private static final Int2ObjectOpenHashMap<List<EfficientArea>> region2polygonmultis = new Int2ObjectOpenHashMap<>();

    static {
        for (int i = 0; i < 4; i++)
            polygons.add(new ArrayList<>());
    }

    public static final void loadAndMap() {
        load();
        map();
    }

    public static final void load() {
        for (int i = 0; i < 4; i++) {
            MULTIWAY_AREA_POLYGONS[i] = MapLocations.getMulticombat(i);
        }
        for (int z = 0; z < 4; z++) {
            val area = MULTIWAY_AREA_POLYGONS[z];
            val list = new ArrayList<int[]>();
            val it = area.getPathIterator(null);
            while (!it.isDone()) {
                val coords = new float[6];
                it.currentSegment(coords);
                list.add(new int[]{(int) coords[0], (int) coords[1]});
                it.next();
            }

            val coords = new int[list.size()][2];
            int i = 0;
            for (val intarr : list) {
                coords[i++] = intarr;
            }

            val polygonList = new ArrayList<RSPolygon>();
            List<int[]> currentPolygon = new ArrayList<>();
            for (i = coords.length - 1; i >= 0; i--) {
                int x = coords[i][0];
                int y = coords[i][1];
                if (x == 0 && y == 0) {
                    constructAndAddPolygon(currentPolygon, polygonList);
                    currentPolygon = new ArrayList<>();
                    continue;
                }
                currentPolygon.add(coords[i]);
            }
            constructAndAddPolygon(currentPolygon, polygonList);
            for (RSPolygon rsPolygon : polygonList) {
                polygons.get(z).add(new Area(rsPolygon.getPolygon()));
            }
        }
    }

    private static final void constructAndAddPolygon(final List<int[]> currentPolygon,
                                                     final List<RSPolygon> polygonList) {
        if (!currentPolygon.isEmpty()) {
            int[][] currentPolyLines = new int[currentPolygon.size()][2];
            for (int a = 0; a < currentPolygon.size(); a++) {
                int[] ppp = currentPolygon.get(a);
                currentPolyLines[a] = ppp;
            }
            polygonList.add(new RSPolygon(currentPolyLines, 0));
        }
    }

    public static final void map() {
        val regionList = new IntOpenHashSet(2000);
        val allPolygons = MultiwayArea.getPolygons();
        val cache = Game.getCacheMgi();
        val archive = cache.getArchive(ArchiveType.MAPS);
        for (int rx = 0; rx < 100; rx++) {
            for (int ry = 0; ry < 256; ry++) {
                val group = archive.findGroupByName("m" + rx + "_" + ry);
                val id = group == null ? -1 : group.getID();
                if (id != -1) {
                    regionList.add(rx << 8 | ry);
                }
            }
        }
        for (int z = 0; z < 4; z++) {
            val polygons = allPolygons.get(z);
            for (int id : regionList) {
                val x = (id >> 8) << 6;
                val y = (id & 0xFF) << 6;
                val chunkPolygon = new RSPolygon(new int[][]{new int[]{x, y}, new int[]{x + 64, y}, new int[]{x + 64,
                        y + 64}, new int[]{x, y + 64}}, 0).getPolygon();
                val chunkArea = new Area(chunkPolygon);
                for (int i = polygons.size() - 1; i >= 0; i--) {
                    val polygon = polygons.get(i);
                    if (!polygon.getBounds2D().intersects(chunkArea.getBounds2D())) {
                        continue;
                    }
                    val area = new Area(polygon);
                    area.intersect(chunkArea);

                    if (!area.isEmpty()) {
                        val regionId = regionHash(x, y, z);
                        var list = region2polygonmultis.get(regionId);
                        if (list == null) {
                            list = new ArrayList<>();
                            region2polygonmultis.put(regionId, list);
                        }
                        list.add(new EfficientArea(polygon));
                    }
                }
            }
        }
        WorldTasksManager.schedule(() -> {
            for (val player : World.getPlayers()) {
                player.checkMultiArea();
            }
            for (val npc : World.getNPCs()) {
                npc.checkMultiArea();
            }
        });
    }

    private static int regionHash(final int x, final int y, final int z) {
        return (((x >> 6) << 8) + (y >> 6) | z << 16);
    }

    public static List<EfficientArea> addDynamicMultiArea(final Area area, final int plane) {
        val efficientAreas = new ArrayList<EfficientArea>();
        val bounds = area.getBounds2D();
        final int minRX = (int) bounds.getMinX() >> 6;
        final int minRY = (int) bounds.getMinY() >> 6;
        final int width = (int) Math.ceil(bounds.getWidth() / 64F);
        final int height = (int) Math.ceil(bounds.getHeight() / 64F);
        for (int rx = minRX; rx < (minRX + width); rx++) {
            for (int ry = minRY; ry < (minRY + height); ry++) {
                val x = rx << 6;
                val y = ry << 6;
                val chunkPolygon = new RSPolygon(new int[][]{new int[]{x, y}, new int[]{x + 64, y}, new int[]{x + 64, y + 64}, new int[]{x, y + 64}}, 0).getPolygon();
                val chunkArea = new Area(chunkPolygon);

                chunkArea.intersect(area);

                if (!area.isEmpty()) {
                    val regionId = regionHash(x, y, plane);
                    var list = region2polygonmultis.get(regionId);
                    if (list == null) {
                        list = new ArrayList<>();
                        region2polygonmultis.put(regionId, list);
                    }
                    val a = new EfficientArea(area);
                    list.add(a);
                    efficientAreas.add(a);
                }
            }
        }
        return efficientAreas;
    }

    public static void removeDynamicMultiArea(final Chunk.RSArea area) {
        val bounds = area.getArea().getBounds2D();
        final int minRX = (int) bounds.getMinX() >> 6;
        final int minRY = (int) bounds.getMinY() >> 6;
        final int width = (int) Math.ceil(bounds.getWidth() / 64F);
        final int height = (int) Math.ceil(bounds.getHeight() / 64F);
        for (int rx = minRX; rx < (minRX + width); rx++) {
            for (int ry = minRY; ry < (minRY + height); ry++) {
                val regionId = regionHash(rx << 6, ry << 6, area.getHeight());
                val list = region2polygonmultis.get(regionId);
                if (list != null) {
                    list.remove(area.getArea());
                    if (list.isEmpty()) {
                        region2polygonmultis.remove(regionId);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the location is in a multi zone or not.
     *
     * @param location the location to check.
     * @return whether the location is in a multi zone or not.
     */
    public static boolean isMultiArea(final Location location) {
        val regionId = regionHash(location.getX(), location.getY(), location.getPlane());
        val list = region2polygonmultis.get(regionId);
        if (list == null) {
            return false;
        }

        val x = location.getX();
        val y = location.getY();

        EfficientArea polygon;
        for (int i = list.size() - 1; i >= 0; i--) {
            polygon = list.get(i);
            if (polygon.contains(x, y))
                return true;
        }

        return false;
    }

    public static void main(final String[] args) throws IOException {
        load();
        for (int i = MIN_PLANE; i < MAX_PLANE; i++)
            new MultiwayArea().load(i);
    }

    @Override
    public void draw(final Graphics2D graphics, final int plane) throws IOException {
        log.info("Drawing map image");
        val cyan = new Color(0, 255, 255, 127);
        val purple = new Color(140, 0, 116);
        val area = MULTIWAY_AREA_POLYGONS[plane];
        val list = new ArrayList<int[]>();
        val it = area.getPathIterator(null);
        while (!it.isDone()) {
            val coords = new float[6];
            it.currentSegment(coords);
            list.add(new int[]{(int) coords[0], (int) coords[1]});
            it.next();
        }

        val coords = new int[list.size()][2];
        int i = 0;
        for (val intarr : list) {
            coords[i++] = intarr;
        }

        val polygonList = new ArrayList<RSPolygon>();
        List<int[]> currentPolygon = null;
        for (i = coords.length - 1; i >= 0; i--) {
            int x = coords[i][0];
            int y = coords[i][1];
            if (x == 0 && y == 0) {
                if (currentPolygon != null) {
                    int[][] currentPolyLines = new int[currentPolygon.size()][2];
                    for (int a = 0; a < currentPolygon.size(); a++) {
                        int[] ppp = currentPolygon.get(a);
                        currentPolyLines[a] = ppp;
                    }
                    polygonList.add(new RSPolygon(currentPolyLines, 0));
                    currentPolygon = new ArrayList<>();
                    continue;
                }
                currentPolygon = new ArrayList<>();
                continue;
            }
            coords[i][0] = getX(x);
            coords[i][1] = getY(y);
            currentPolygon.add(coords[i]);
        }
        for (RSPolygon polygon : polygonList) {
            graphics.setColor(cyan);
            graphics.fillPolygon(polygon.getPolygon());
            graphics.setColor(purple);
            graphics.drawPolygon(polygon.getPolygon());
        }
    }

    @Override
    public String path(final int plane) {
        return "data/map/produced map image " + plane + ".png";
    }

}
