package mgi.custom;

import com.zenyte.Game;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.cache.File;
import mgi.tools.jagcached.cache.Group;
import mgi.utilities.ByteBuffer;

/**
 * @author Kris | 28/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class FramePacker {

    private static final Int2ObjectMap<byte[]> frames = new Int2ObjectOpenHashMap<>();

    public static final void add(final int file, final byte[] frames) {
        FramePacker.frames.put(file, frames);
    }

    public static final void write() {
        val cache = Game.getCacheMgi();
        val archive = cache.getArchive(ArchiveType.SKELETONS);
        val sortedMap = new Int2ObjectAVLTreeMap<byte[]>(frames);
        for (val entry : sortedMap.int2ObjectEntrySet()) {
            val key = entry.getIntKey();
            val bytes = entry.getValue();
            val file = new File(key & 0xFFFF, new ByteBuffer(bytes));
            val group = archive.findGroupByID(key >> 16);
            if (group == null) {
                archive.addGroup(new Group(key >> 16, file));
            } else {
                group.addFile(file);
            }
        }
    }

}
