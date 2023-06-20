package mgi.tools.dumpers;

import com.google.common.base.Preconditions;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.game.world.region.MapUtils;
import com.zenyte.game.world.region.XTEALoader;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.Cache;
import mgi.utilities.ByteBuffer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class MapDumper {

    public static int[] regionsToDump = { 12637, 12638, 12639, 12640,
                                          12893, 12894, 12895, 12896,
                                          13149, 13150, 13151, 13152,
                                          13405, 13406, 13407, 13408};
    public static boolean print = true;
    public static boolean dump = true;
    public static ArrayList<Integer> uniqueIds = new ArrayList<Integer>();

    public static void main(String[] args) throws IOException {
        val cache = Cache.openCache(Constants.CACHE_LOCATION);
        XTEALoader.loadNewRevision();
        for(int regionToDump : regionsToDump) {
            File directory = new File(Constants.MAPS_DUMP_LOCATION + regionToDump + "/");

            if (!directory.exists()) {
                directory.mkdirs();
            }
            val xteas = XTEALoader.getXTEAKeys(regionToDump);
            System.out.print("XTEA KEYS: [" );
            for(int i = 0; i < 4; i++) {
                System.out.print(xteas[i] + " ");
            }
            System.out.print("]");
            System.out.println();
            val archive = cache.getArchive(ArchiveType.MAPS);

            val regionX = regionToDump >> 8;
            val regionY = regionToDump & 0xFF;

            val mapGroup = archive.findGroupByName("m" + regionX + "_" + regionY);

            val landGroup = archive.findGroupByName("l" + regionX + "_" + regionY, xteas);
            val locFileId = landGroup == null ? -1 : landGroup.getID();
            Preconditions.checkArgument(locFileId != -1);
            ByteBuffer landBuffer = landGroup == null ? null : landGroup.findFileByID(0).getData();


            if(print) {
                Collection<WorldObject> mapObjects = MapUtils.decodeNew(landBuffer);

                if(mapObjects.size() > 0) {
                    System.out.println("Printing out object id's used in region: " + regionToDump);
                    System.out.println("Region ID: " + regionToDump + " - Object count: " + mapObjects.size());
                    for(WorldObject o : mapObjects) {
                        if(!uniqueIds.contains(o.getId())) {
                            uniqueIds.add(o.getId());
                        }
                    };
                } else {
                    System.out.println("No map objects detected in region " + regionToDump);
                }
            }

            if(dump) {
                val mapdos = new DataOutputStream(new FileOutputStream(new File(directory, "m" + regionX + "_" + regionY + ".dat")));
                val landdos = new DataOutputStream(new FileOutputStream(new File(directory, "l" + regionX + "_" + regionY + ".dat")));
                mapdos.write(mapGroup.findFileByID(0).getData().getBuffer());
                landdos.write(landGroup.findFileByID(0).getData().getBuffer());
            }


            System.out.println("Finished dumping region " + regionToDump);
        }


        if(print) {
            if(uniqueIds.size() > 0) {
                uniqueIds.sort(Comparator.comparingInt(i -> i));
                for(int id : uniqueIds) {
                    System.out.println("Unique ID: " + id);
                }
            }
        }

    }
}
