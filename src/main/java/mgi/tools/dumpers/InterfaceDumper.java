package mgi.tools.dumpers;

import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.Cache;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InterfaceDumper {
    public static void main(String[] args) throws IOException {
        File directory = new File("./dumps/201/interface/");

        if (!directory.exists()) {
            directory.mkdirs();
        }
        val cache = Cache.openCache("./data/cache200V2/");
        val archive = cache.getArchive(ArchiveType.INTERFACES);
        val id = archive.getHighestGroupId();
        for(int i = 0; i < id; i++) {
            val group = archive.findGroupByID(i);
            if (group == null) {
                System.err.println("Interface doesn't exist for id: " + i);
                return;
            }
            File directory2 = new File("./dumps/201/interface/" + i + "/");
            if (!directory2.exists()) {
                directory2.mkdirs();
            }
            for(int j = 0; j < archive.findGroupByID(i).fileCount(); j++) {
                val dos = new DataOutputStream(new FileOutputStream(new File(directory2, j + "")));
                dos.write(group.findFileByID(j).getData().getBuffer());
            }
        }
    }
}
