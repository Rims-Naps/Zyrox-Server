package mgi.tools.dumpers;

import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.Cache;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Tommeh | 11/02/2020 | 11:12
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ScriptDumper {

    public static void main(String[] args) throws IOException {
        File directory = new File("./dumps/201/cs2/");

        if (!directory.exists()) {
            directory.mkdirs();
        }
        val cache = Cache.openCache("./data/cache200V2/");
        val archive = cache.getArchive(ArchiveType.CLIENTSCRIPTS);
        for(int i = 0; i < archive.getHighestGroupId(); i++) {
            val group = archive.findGroupByID(i);
            if (group == null) {
                System.err.println("Script doesn't exist!");
                return;
            }
            val dos = new DataOutputStream(new FileOutputStream(new File(directory, i + ".cs2")));
            dos.write(group.findFileByID(0).getData().getBuffer());
            if(group.fileCount() > 1) {
                System.out.println("ID: " + i + " had more than 1 file count: " + group.fileCount());
            }
        }

    }
}
