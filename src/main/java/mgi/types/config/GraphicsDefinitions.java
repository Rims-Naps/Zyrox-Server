package mgi.types.config;

import com.zenyte.Game;
import mgi.types.Definitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.File;
import mgi.utilities.ByteBuffer;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Kris | 6. apr 2018 : 21:12.37
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 * profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 * profile</a>}
 */
@Slf4j
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
public final class GraphicsDefinitions implements Definitions {

    public static GraphicsDefinitions[] definitions;
    @Getter @Setter
    private int id;
    @Getter @Setter
    private int modelId, animationId, resizeX, resizeY, rotation, ambience, contrast;
    @Getter @Setter
    private short[] originalColours, retextureToFind, replacementColours, retextureToReplace;

    public GraphicsDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        setDefaults();
        decode(buffer);
    }

    public static final GraphicsDefinitions get(final int id) {
        if (id < 0 || id >= definitions.length) {
            return null;
        }

        return definitions[id];
    }

    public static final void printGraphicsDifferences(final Cache cache, final Cache cacheToCompareWith) {
        val currentAnimations = getAnimations(cache);
        val animations = getAnimations(cacheToCompareWith);
        var iterator = currentAnimations.int2ObjectEntrySet().iterator();
        val list = new IntArrayList();
        while (iterator.hasNext()) {
            val next = iterator.next();
            val id = next.getIntKey();
            val bytes = next.getValue();

            val otherBytes = animations.get(id);
            if (!Arrays.equals(bytes, otherBytes)) {
                list.add(id);
            }
        }

        iterator = animations.int2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            val next = iterator.next();
            val id = next.getIntKey();
            val bytes = next.getValue();

            val otherBytes = currentAnimations.get(id);
            if (otherBytes == null || !Arrays.equals(bytes, otherBytes)) {
                if (!list.contains(id)) list.add(id);
            }
        }
        Collections.sort(list);
        for (int id : list) {
            System.err.println("Graphics difference: " + id);
        }
        System.err.println("Graphics difference checking complete!");
    }

    private static final Int2ObjectOpenHashMap<byte[]> getAnimations(final Cache cache) {
        val map = new Int2ObjectOpenHashMap<byte[]>();
        try {
            val configs = cache.getArchive(ArchiveType.CONFIGS);
            val graphics = configs.findGroupByID(GroupType.SPOTANIM);
            for (int id = 0; id < graphics.getHighestFileId(); id++) {
                val file = graphics.findFileByID(id);
                if (file == null) {
                    continue;
                }
                val buffer = file.getData();
                if (buffer == null) {
                    continue;
                }
                map.put(id, buffer.getBuffer());
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
        return map;
    }

    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val configs = cache.getArchive(ArchiveType.CONFIGS);
        val graphics = configs.findGroupByID(GroupType.SPOTANIM);
        definitions = new GraphicsDefinitions[graphics.getHighestFileId()];
        for (int id = 0; id < graphics.getHighestFileId(); id++) {
            val file = graphics.findFileByID(id);
            if (file == null) {
                continue;
            }
            val buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            definitions[id] = new GraphicsDefinitions(id, buffer);
        }
    }

    private void setDefaults() {
        animationId = -1;
        resizeX = 128;
        resizeY = 128;
        rotation = 0;
        ambience = 0;
        contrast = 0;
    }

    @Override
    public void decode(final ByteBuffer buffer) {
        while (true) {
            val opcode = buffer.readUnsignedByte();
            if (opcode == 0) {
                return;
            }
            decode(buffer, opcode);
        }
    }

    @Override
    public void decode(final ByteBuffer buffer, final int opcode) {
        switch (opcode) {
            case 1:
                modelId = buffer.readUnsignedShort();
                return;
            case 2:
                animationId = buffer.readUnsignedShort();
                return;
            case 4:
                resizeX = buffer.readUnsignedShort();
                return;
            case 5:
                resizeY = buffer.readUnsignedShort();
                return;
            case 6:
                rotation = buffer.readUnsignedShort();
                return;
            case 7:
                ambience = buffer.readUnsignedByte();
                return;
            case 8:
                contrast = buffer.readUnsignedByte();
                return;
            case 40: {
                val length = buffer.readUnsignedByte();
                originalColours = new short[length];
                replacementColours = new short[length];

                for (int index = 0; index < length; ++index) {
                    originalColours[index] = (short) buffer.readUnsignedShort();
                    replacementColours[index] = (short) buffer.readUnsignedShort();
                }
                return;
            }
            case 41: {
                val length = buffer.readUnsignedByte();
                retextureToFind = new short[length];
                retextureToReplace = new short[length];

                for (int index = 0; index < length; ++index) {
                    retextureToFind[index] = (short) buffer.readUnsignedShort();
                    retextureToReplace[index] = (short) buffer.readUnsignedShort();
                }
            }
            return;
        }
    }

    @Override
    public ByteBuffer encode() {
        val buffer = new ByteBuffer(128);
        if (modelId != -1) {
            buffer.writeByte(1);
            buffer.writeShort(modelId);
        }
        if (animationId != -1) {
            buffer.writeByte(2);
            buffer.writeShort(animationId);
        }
        if (resizeX != 0) {
            buffer.writeByte(4);
            buffer.writeShort(resizeX);
        }
        if (resizeY != 0) {
            buffer.writeByte(5);
            buffer.writeShort(resizeY);
        }
        if (rotation != -1) {
            buffer.writeByte(6);
            buffer.writeShort(rotation);
        }
        if (ambience != -1) {
            buffer.writeByte(7);
            buffer.writeByte(ambience);
        }
        if (contrast != -1) {
            buffer.writeByte(8);
            buffer.writeByte(contrast);
        }
        if (originalColours != null && originalColours.length > 0) {
            buffer.writeByte(40);
            buffer.writeByte(originalColours.length);
            for (int index = 0; index < originalColours.length; index++) {
                buffer.writeShort(originalColours[index]);
                buffer.writeShort(replacementColours[index]);
            }
        }
        if (retextureToFind != null && retextureToFind.length > 0) {
            buffer.writeByte(41);
            buffer.writeByte(retextureToFind.length);
            for (int index = 0; index < retextureToFind.length; index++) {
                buffer.writeShort(retextureToFind[index]);
                buffer.writeShort(retextureToReplace[index]);
            }
        }
        buffer.writeByte(0);
        return buffer;
    }

    @Override
    public void pack() {
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.SPOTANIM).addFile(new File(id, encode()));
    }

}
