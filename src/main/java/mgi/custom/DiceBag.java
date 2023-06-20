package mgi.custom;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import lombok.val;
import lombok.var;
import mgi.custom.christmas.ChristmasMapPacker;
import mgi.tools.parser.TypeParser;
import mgi.types.config.AnimationDefinitions;
import mgi.types.config.GraphicsDefinitions;
import mgi.utilities.ByteBuffer;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

import static mgi.tools.parser.TypeParser.packModel;

/**
 * @author Kris | 15/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class DiceBag {

    private final Int2IntMap transformedModels = new Int2IntOpenHashMap();
    private final Int2IntMap transformedAnimations = new Int2IntOpenHashMap();
    private final Int2IntMap transformedSounds = new Int2IntOpenHashMap();

    public final void packAll() throws IOException {
        packSoundEffects();
        packBaseAnimations();
        packModels();
        packGraphics();
    }

    private void packBaseAnimations() throws IOException {
        val indexedAnims = new int[]{
                11900, 11915, 11916, 11917, 11918
        };

        for (int i = 0; i < indexedAnims.length; i++) {
            packAnimationBase(AnimationBase.valueOf("DICE_BASE_" + (5193 + i)), new int[]{indexedAnims[i]});
        }
    }

    private final void packSoundEffects() throws IOException {
        Iterator<File> it = FileUtils.iterateFiles(new File("assets/dice bag/sounds/"), null, false);
        val sortedMap = new Int2ObjectAVLTreeMap<File>();
        while (it.hasNext()) {
            val file = it.next();
            val originalId = Integer.parseInt(file.getName().replace(".dat", ""));
            sortedMap.put(originalId, file);
        }
        for (val entry : sortedMap.int2ObjectEntrySet()) {
            val file = entry.getValue();
            val originalId = entry.getIntKey();
            transformedSounds.put(originalId, ChristmasMapPacker.soundEffectId);
            TypeParser.packSound(ChristmasMapPacker.soundEffectId++, IOUtils.toByteArray(new FileInputStream(file)));
        }
    }

    private void packAnimationBase(@NotNull final AnimationBase base, final int[] anims) throws IOException {
        for (int anim : anims) {
            val file = new File("assets/dice bag/animations/definitions/" + anim + ".dat");
            val definitions = new AnimationDefinitions(anim, new ByteBuffer(FileUtils.readFileToByteArray(file)));
            val ids = definitions.getFrameIds();
            if (ids != null) {
                for (int i = 0; i < ids.length; i++) {
                    ids[i] = (ids[i] & 0xFFFF) | (base.getBaseId() << 16);
                }
            }
            val altIds = definitions.getExtraFrameIds();
            if (altIds != null) {
                for (int i = 0; i < altIds.length; i++) {
                    altIds[i] = (altIds[i] & 0xFFFF) | (base.getBaseId() << 16);
                }
            }

            val sounds = definitions.getSoundEffects();
            if (sounds != null) {
                for (int i = 0; i < sounds.length; i++) {
                    val effectId = sounds[i];
                    if (effectId == 0) {
                        continue;
                    }
                    val soundId = effectId >> 8;
                    var radius = effectId & 0x1F;
                    val repetitions = effectId >> 5 & 0x7;
                    sounds[i] = (Math.min(0xF, 1 + radius) | repetitions << 4) | (transformedSounds.getOrDefault(soundId, soundId) << 8);
                }
            }

            definitions.setId(ChristmasMapPacker.animationIndex++);
            transformedAnimations.put(anim, definitions.getId());
            definitions.pack();
            packFrames(ids);
            if (altIds != null) {
                packFrames(altIds);
            }
        }
    }

    private final void packFrames(final int[] ids) {
        Arrays.sort(ids);
        for (int id : ids) {
            try {
                File file = new File("assets/dice bag/animations/frames/" + id + ".dat");
                FramePacker.add(id, IOUtils.toByteArray(new FileInputStream(file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void packGraphics() throws IOException {
        Iterator<File> it = FileUtils.iterateFiles(new File("assets/dice bag/graphics/"), null, false);
        val sortedMap = new Int2ObjectAVLTreeMap<File>();
        while (it.hasNext()) {
            val file = it.next();
            val originalId = Integer.parseInt(file.getName().replace(".dat", ""));
            sortedMap.put(originalId, file);
        }
        for (val entry : sortedMap.int2ObjectEntrySet()) {
            val file = entry.getValue();
            val def = new GraphicsDefinitions(ChristmasMapPacker.graphicsId++, new ByteBuffer(FileUtils.readFileToByteArray(file)));
            def.setModelId(transformedModels.getOrDefault(def.getModelId(), def.getModelId()));
            def.setAnimationId(transformedAnimations.getOrDefault(def.getAnimationId(), def.getAnimationId()));
            def.pack();
        }
    }

    public void packModels() throws IOException {
        Iterator<File> it = FileUtils.iterateFiles(new File("assets/dice bag/models/"), null, true);
        val list = new IntAVLTreeSet();
        while (it.hasNext()) {
            val file = it.next();
            val name = file.getName().replace(".dat", "");
            val id = Integer.parseInt(name);
            list.add(id);
        }
        for (val originalId : list) {
            val file = new File("assets/dice bag/models/" + originalId + ".dat");
            transformedModels.put((int) originalId, ChristmasMapPacker.modelId);
            packModel(ChristmasMapPacker.modelId++, Files.readAllBytes(Paths.get(file.getPath())));
        }
    }

}
