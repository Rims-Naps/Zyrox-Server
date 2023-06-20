package mgi.tools.dumpers;



import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.Archive;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.Group;
import mgi.types.config.items.ItemDefinitions;
import mgi.utilities.ByteBuffer;

import java.io.*;
import java.util.ArrayList;

public class ItemModelPrinter {

    private static final int revision = 2052;
    private static final boolean dumpAfterPrinting = true;
    private static final String nameToSearch = "Hat of the eye"; //change to whatever you want to look for

    //put cache files in ./data/cache205/
    private static final String cacheDir = "./data/cache" + revision + "/";
    private static File directory = new File("./dumps/" + revision + "/item/");
    private static ArrayList<Integer> itemsMatching = new ArrayList<>();
    private static ItemDefinitions[] definitions;
    private static Cache cache;

    public static void main(String[] args) throws IOException {
        if(dumpAfterPrinting) {
            if(!directory.exists()) {
                directory.mkdir();
            }
        }
        cache = Cache.openCache(cacheDir);

        Archive configArchive = cache.getArchive(ArchiveType.CONFIGS);
        Group group = configArchive.findGroupByID(GroupType.ITEM);
        definitions = new ItemDefinitions[group.getHighestFileId()];

        System.out.println("Looking for matches for '" + nameToSearch + "'");
        for(int id = 0; id < group.getHighestFileId(); id++) {
            val config = group.findFileByID(id);
            if(config == null) {
                continue;
            }
            ByteBuffer buffer = config.getData();
            if(buffer == null) {
                continue;
            }
            definitions[id] = new ItemDefinitions(id, buffer);

            ItemDefinitions def = definitions[id];
            if(def.getName().equalsIgnoreCase(nameToSearch)) {
                itemsMatching.add(id);
                System.out.println(itemsMatching.size() + " -> (ID: " + id + ")");
                if(dumpAfterPrinting) {
                    System.out.println("Dumping models...");
                    if(def.getInventoryModelId() != -1) {
                        dumpModelById(def.getInventoryModelId());
                    }
                    if(def.getPrimaryMaleModel() != -1) {
                        dumpModelById(def.getPrimaryMaleModel());
                    }
                    if(def.getSecondaryMaleModel() != -1) {
                        dumpModelById(def.getSecondaryMaleModel());
                    }
                    if(def.getTertiaryMaleModel() != -1) {
                        dumpModelById(def.getTertiaryMaleModel());
                    }
                    if(def.getPrimaryFemaleModel() != -1) {
                        dumpModelById(def.getPrimaryFemaleModel());
                    }
                    if(def.getSecondaryFemaleModel() != -1) {
                        dumpModelById(def.getSecondaryFemaleModel());
                    }
                    if(def.getTertiaryFemaleModel() != -1) {
                        dumpModelById(def.getTertiaryFemaleModel());
                    }
                }
                System.out.println("Inventory model id " + def.getInventoryModelId());
                System.out.println("Primary male model id " + def.getPrimaryMaleModel());
                System.out.println("Secondary male model id " + def.getSecondaryMaleModel());
                System.out.println("Tertiary male model id " + def.getTertiaryMaleModel());
                System.out.println("Primary female model id " + def.getPrimaryFemaleModel());
                System.out.println("Secondary female model id " + def.getSecondaryFemaleModel());
                System.out.println("Tertiary female model id " + def.getTertiaryFemaleModel());
                System.out.println();
            }
        }
    }

    private static void dumpModelById(int id) throws IOException {
        Archive archive = cache.getArchive(ArchiveType.MODELS);
        Group group = archive.findGroupByID(id);
        if (group == null) {
            System.err.println("Model doesn't exist for id: " + id);
            return;
        }
        File model = new File(directory, id + ".dat");
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(model));
        dos.write(group.findFileByID(0).getData().getBuffer());
    }
}