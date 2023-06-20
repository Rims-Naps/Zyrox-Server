package mgi.types.config;

import com.zenyte.Game;
import com.zenyte.game.util.Utils;
import mgi.Indice;
import mgi.types.Definitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.Cache;
import mgi.tools.jagcached.cache.File;
import mgi.utilities.ByteBuffer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@Setter
@ToString
@Slf4j
public final class AnimationDefinitions implements Definitions, Cloneable {

    public static AnimationDefinitions[] definitions;
    /**
     * The id of the animation.
     */
    @Getter
    private int id;
    @Getter
    private int precedenceAnimating;
    /**
     * An array of frame ids. The value is a bitpacked number, with bits past 16 being the skeleton id.
     */
    @Getter
    private int[] frameIds;
    @Getter
    private int[] mergedBoneGroups;
    /**
     * Animation priority level.
     */
    @Getter
    private int priority;
    @Getter
    private int frameStep;
    /**
     * The length of each frame, with one value being equal to one actual frame, capping at 20 milliseconds (1 second / 50 FPS)
     */
    @Getter
    private int[] frameLengths;
    @Getter
    private boolean stretches;
    @Getter
    private int[] extraFrameIds;
    /**
     * The id of the item held in the left hand. If the id is 0, the helf left hand item is not displayed by the client.
     */
    @Getter
    private int leftHandItem;
    @Getter
    private int forcedPriority;
    /**
     * The id of the item held in the right hand. If the id is 0, the held right hand item is not displayed by the
     * client.
     */
    @Getter
    private int rightHandItem;
    /**
     * The maximum number of times the animation can replay itself.
     */
    @Getter
    private int iterations;
    @Getter
    private int replyMode;
    /**
     * An array of sound effects per each frame. The values are already shifted by 8 bits to get the id of the actual sound effect, as the
     * rest of the information is useless to us.
     */
    @Getter
    private int[] soundEffects;

    public AnimationDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        setDefaults();
        decode(buffer);
    }

    public AnimationDefinitions clone() throws CloneNotSupportedException {
        return (AnimationDefinitions) super.clone();
    }

    /**
     * Gets a list of all the animations that share the skeleton of the animation in arguments.
     *
     * @param animationId the animation to compare
     * @return a list of animations.
     */
    public static final List<Integer> getSkeletonAnimations(final int animationId) throws Throwable {
        final AnimationDefinitions d = AnimationDefinitions.get(animationId);
        if (d == null) {
            throw new Throwable("Animation is null.");
        }
        if (d.frameIds == null) {
            throw new Throwable("Animation images are null - unable to compare.");
        }
        final int frameId = d.frameIds[0] >> 16;
        final List<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < Utils.getIndiceSize(Indice.ANIMATION_DEFINITIONS); i++) {
            final AnimationDefinitions defs = AnimationDefinitions.get(i);
            if (defs == null) {
                continue;
            }
            if (defs.frameIds == null) {
                continue;
            }
            if (defs.frameIds[0] >> 16 == frameId) {
                ids.add(i);
            }
        }
        return ids;
    }

    public static final AnimationDefinitions get(final int id) {
        if (id < 0 || id >= definitions.length) {
            return null;
        }

        return definitions[id];
    }

    public static final IntArrayList getAnimationIdsByFrameId(final int frameId, final IntOpenHashSet linkedAnimations) {
        val list = new IntArrayList();
        for (int i = 0; i < Utils.getIndiceSize(Indice.ANIMATION_DEFINITIONS); i++) {
            if (linkedAnimations != null && linkedAnimations.contains(i)) {
                continue;
            }
            val definitions = AnimationDefinitions.get(i);
            if (definitions == null) {
                continue;
            }
            if (definitions.getFrameIds() != null) {
                if (ArrayUtils.contains(definitions.getFrameIds(), frameId)) {
                    if (!list.contains(i)) {
                        list.add(i);
                    }
                }
            }
            if (definitions.getExtraFrameIds() != null) {
                if (ArrayUtils.contains(definitions.getExtraFrameIds(), frameId)) {
                    if (!list.contains(i)) {
                        list.add(i);
                    }
                }
            }
        }
        return list;
    }

    public static final int getSkeletonId(final int animationId) {
        val definitions = get(animationId);
        if (definitions == null) {
            return -1;
        }
        val frames = definitions.frameIds;
        if (frames == null || frames.length == 0) {
            return -1;
        }
        return frames[0] >> 16;
    }

    public static final void printAnimationDifferences(final Cache cache, final Cache cacheToCompareWith) {

        val currentAnimations = getAnimations(cache);
        val animations = getAnimations(cacheToCompareWith);
        var iterator = currentAnimations.int2ObjectEntrySet().iterator();
        val list = new IntArrayList();

        while (iterator.hasNext()) {
            val next = iterator.next();
            val id = next.getIntKey();
            val bytes = next.getValue();

            val otherBytes = animations.get(id);
            if (otherBytes == null || !Arrays.equals(bytes, otherBytes)) {
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
                if (!list.contains(id))
                    list.add(id);
            }
        }
        Collections.sort(list);
        for (int id : list) {
            System.err.println("Animation difference: " + id);
        }
        System.err.println("Animation difference checking complete!");
    }

    private static final Int2ObjectOpenHashMap<byte[]> getAnimations(final Cache cache) {
        val map = new Int2ObjectOpenHashMap<byte[]>();
        try {
            val configs = cache.getArchive(ArchiveType.CONFIGS);
            val animations = configs.findGroupByID(GroupType.SEQUENCE);
            for (int id = 0; id < animations.getHighestFileId(); id++) {
                val file = animations.findFileByID(id);
                if (file == null) {
                    continue;
                }
                val buffer = file.getData();
                if (buffer == null) {
                    continue;
                }
                map.put(id, buffer.toArray(0, buffer.getBuffer().length));
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
        return map;
    }

    @Override
    public void load() {
        try {
            val cache = Game.getCacheMgi();
            val configs = cache.getArchive(ArchiveType.CONFIGS);
            val animations = configs.findGroupByID(GroupType.SEQUENCE);
            definitions = new AnimationDefinitions[animations.getHighestFileId()];
            for (int id = 0; id < animations.getHighestFileId(); id++) {
                val file = animations.findFileByID(id);
                if (file == null) {
                    continue;
                }
                val buffer = file.getData();
                if (buffer == null) {
                    continue;
                }
                definitions[id] = new AnimationDefinitions(id, buffer);
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    private void setDefaults() {
        frameStep = -1;
        stretches = false;
        forcedPriority = 5;
        leftHandItem = -1;
        rightHandItem = -1;
        iterations = 99;
        precedenceAnimating = -1;
        priority = -1;
        replyMode = 2;
    }

    public final int getDuration() {
        int duration = 0;
        if (frameLengths == null) {
            return 0;
        }
        for (final int i : frameLengths) {
            if (i > 30) {
                continue;
            }
            duration += i * 20;
        }
        return duration;
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

    public void decode(final ByteBuffer buffer, final int opcode) {
        switch (opcode) {
            case 1: {
                val count = buffer.readUnsignedShort();
                frameLengths = new int[count];

                for (int index = 0; index < count; ++index) {
                    frameLengths[index] = buffer.readUnsignedShort();
                }

                frameIds = new int[count];

                for (int index = 0; index < count; ++index) {
                    frameIds[index] = buffer.readUnsignedShort();
                }

                for (int index = 0; index < count; ++index) {
                    frameIds[index] += (buffer.readUnsignedShort()) << 16;
                }
                return;
            }
            case 2:
                frameStep = buffer.readUnsignedShort();
                return;
            case 3: {
                val count = buffer.readUnsignedByte();
                mergedBoneGroups = new int[1 + count];

                for (int index = 0; index < count; ++index) {
                    mergedBoneGroups[index] = buffer.readUnsignedByte();
                }

                mergedBoneGroups[count] = 9999999;
                return;
            }
            case 4:
                stretches = true;
                return;
            case 5:
                forcedPriority = buffer.readUnsignedByte();
                return;
            case 6:
                leftHandItem = buffer.readUnsignedShort();
                if (leftHandItem > 0) {
                    leftHandItem -= 512;
                }
                return;
            case 7:
                rightHandItem = buffer.readUnsignedShort();
                if (rightHandItem > 0) {
                    rightHandItem -= 512;
                }
                return;
            case 8:
                iterations = buffer.readUnsignedByte();
                return;
            case 9:
                precedenceAnimating = buffer.readUnsignedByte();
                return;
            case 10:
                priority = buffer.readUnsignedByte();
                return;
            case 11:
                replyMode = buffer.readUnsignedByte();
                return;
            case 12: {
                val count = buffer.readUnsignedByte();
                extraFrameIds = new int[count];

                for (int index = 0; index < count; ++index) {
                    extraFrameIds[index] = buffer.readUnsignedShort();
                }

                for (int index = 0; index < count; ++index) {
                    extraFrameIds[index] += (buffer.readUnsignedShort()) << 16;
                }
                return;
            }
            case 13: {
                val count = buffer.readUnsignedByte();
                soundEffects = new int[count];

                for (int index = 0; index < count; ++index) {
                    soundEffects[index] = buffer.readMedium();
                }
            }
            return;
        }
    }

    @Override
    public ByteBuffer encode() {
        val buffer = new ByteBuffer(1024 * 10 * 10);

        if (frameIds != null) {
            buffer.writeByte(1);
            buffer.writeShort(frameLengths.length);
            for (int frameLength : frameLengths) {
                buffer.writeShort(frameLength);
            }
            for (val frameId : frameIds) {
                buffer.writeShort(frameId & 0xFFFF);
            }
            for (val frameId : frameIds) {
                buffer.writeShort(frameId >> 16);
            }
        }

        if (frameStep != -1) {
            buffer.writeByte(2);
            buffer.writeShort(frameStep);
        }

        if (mergedBoneGroups != null) {
            buffer.writeByte(3);
            buffer.writeByte(mergedBoneGroups.length - 1);
            for (int i = 0, len = mergedBoneGroups.length - 1; i < len; i++) {
                buffer.writeByte(mergedBoneGroups[i]);
            }
        }

        if (stretches) {
            buffer.writeByte(4);
        }

        if (forcedPriority != 5) {
            buffer.writeByte(5);
            buffer.writeByte(forcedPriority);
        }

        if (leftHandItem != -1) {
            buffer.writeByte(6);
            buffer.writeShort(leftHandItem == 0 ? 0 : leftHandItem + 512);
        }

        if (rightHandItem != -1) {
            buffer.writeByte(7);
            buffer.writeShort(rightHandItem == 0 ? 0 : rightHandItem + 512);
        }

        if (iterations != 99) {
            buffer.writeByte(8);
            buffer.writeByte(iterations);
        }

        if (precedenceAnimating != -1) {
            buffer.writeByte(9);
            buffer.writeByte(precedenceAnimating);
        }

        if (priority != -1) {
            buffer.writeByte(10);
            buffer.writeByte(priority);
        }

        if (replyMode != 2) {
            buffer.writeByte(11);
            buffer.writeByte(replyMode);
        }

        if (extraFrameIds != null) {
            buffer.writeByte(12);
            buffer.writeByte(extraFrameIds.length);
            for (val frameId : extraFrameIds) {
                buffer.writeShort(frameId & 0xFFFF);
            }
            for (val frameId : extraFrameIds) {
                buffer.writeShort(frameId >> 16);
            }
        }

        if (soundEffects != null) {
            buffer.writeByte(13);
            buffer.writeByte(soundEffects.length);
            for (val soundEffect : soundEffects) {
                buffer.writeMedium(soundEffect);
            }
        }

        buffer.writeByte(0);
        return buffer;
    }

    @Override
    public void pack() {
        val archive = Game.getCacheMgi().getArchive(ArchiveType.CONFIGS);
        val animations = archive.findGroupByID(GroupType.SEQUENCE);
        animations.addFile(new File(id, encode()));
    }

    public final IntArrayList getUniqueFrames() {
        val list = new IntArrayList();
        if (frameIds != null) {
            for (final int frame : frameIds) {
                if (!list.contains(frame)) {
                    list.add(frame);
                }
            }
        }
        if (extraFrameIds != null) {
            for (final int frame : extraFrameIds) {
                if (!list.contains(frame)) {
                    list.add(frame);
                }
            }
        }
        return list;
    }

}
