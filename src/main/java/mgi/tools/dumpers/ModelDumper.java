package mgi.tools.dumpers;

import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.Cache;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Tommeh | 01/02/2020 | 13:59
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ModelDumper {

    public static void main(String[] args) throws IOException {
        File directory = new File("./dumps/201/models/");

        if (!directory.exists()) {
            directory.mkdirs();
        }
        val cache = Cache.openCache("./data/cache200V2/");
        val archive = cache.getArchive(ArchiveType.MODELS);

        for(int i = 0; i < archive.getHighestGroupId(); i++) {
            val group = archive.findGroupByID(i);
            if (group == null) {
                System.err.println("Model doesn't exist for id: " + i);
            } else {
                val dos = new DataOutputStream(new FileOutputStream(new File(directory, i + ".dat")));
                dos.write(group.findFileByID(0).getData().getBuffer());
            }
        }

    }
}
