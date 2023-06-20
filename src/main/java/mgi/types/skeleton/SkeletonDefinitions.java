package mgi.types.skeleton;

import com.zenyte.Game;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.types.Definitions;
import mgi.utilities.ByteBuffer;

/**
 * @author Kris | 19. sept 2018 : 16:57:22
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
public class SkeletonDefinitions implements Definitions {

    @Getter
    private static final Int2ObjectOpenHashMap<SkeletonDefinitions> definitions = new Int2ObjectOpenHashMap<SkeletonDefinitions>(1000);

    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val skeletons = cache.getArchive(ArchiveType.SKELETONS);
        for (int i = 0; i < skeletons.getHighestGroupId(); i++) {
            val skeletonGroup = skeletons.findGroupByID(i);
            if (skeletonGroup == null) {
                continue;
            }
            for (int id = 0; id < skeletonGroup.getHighestFileId(); id++) {
                val file = skeletonGroup.findFileByID(id);
                if (file == null) {
                    continue;
                }
                val buffer = file.getData();
                if (buffer == null) {
                    continue;
                }
                definitions.put(i << 16 | id, new SkeletonDefinitions(i << 16 | id, buffer));
            }
        }
    }

    public static final IntOpenHashSet getLinkedFrames(final int frameMapId) {
        val set = new IntOpenHashSet();

        val iterator = SkeletonDefinitions.getDefinitions().int2ObjectEntrySet().fastIterator();
        while (iterator.hasNext()) {
            val next = iterator.next();
            val definitions = next.getValue();
            if (definitions.frameMapId == frameMapId) {
                set.add(definitions.frameId);
            }
        }

        return set;
    }

    private SkeletonDefinitions(final int frameId, final ByteBuffer buffer) {
        this.frameId = frameId;
        decode(buffer);
    }

    @Getter
    private final int frameId;
    @Getter
    private int frameMapId;

    public static final SkeletonDefinitions get(final int frameId) {
        return definitions.get(frameId);
    }

    @Override
    public void decode(final ByteBuffer buffer) {
        if (buffer.remaining() >= 2) {
            frameMapId = buffer.readUnsignedShort();
        }
    }
}
