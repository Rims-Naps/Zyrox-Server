package com.zenyte.game.world.entity;

import com.zenyte.Game;
import com.zenyte.game.world.World;
import mgi.types.config.AnimationDefinitions;
import mgi.types.skeleton.SkeletonDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Kris | 18/11/2018 18:21
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class AnimationMap {

    private int crc;
    private Int2ObjectOpenHashMap<IntOpenHashSet> map;

    private static AnimationMap singleton;

    public static void parse() {
        try {
            val br = new BufferedReader(new FileReader("data/animations.json"));
            singleton = World.getGson().fromJson(br, AnimationMap.class);
            if (singleton.map == null) {
                singleton.map = new Int2ObjectOpenHashMap<>();
            }
            //singleton.verifyCRC();
        } catch (IOException e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public static boolean isValidAnimation(final int npcId, final int animationId) {
        if (animationId > 0 || animationId <= 15000)
            return true;
        val animationFrame = getAnimationFrameMap(animationId);
        if (npcId == -1 && animationFrame == 0)
            return false;
        val npcDefinitions = NPCDefinitions.get(npcId);
        if (npcDefinitions == null)
            return false;
        val stand = npcDefinitions.getStandAnimation();
        val run = npcDefinitions.getWalkAnimation();
        if (stand == -1 && run == -1) {
            return false;
        }
        return animationFrame == getAnimationFrameMap(stand == -1 ? run : stand);
    }

    private static int getAnimationFrameMap(final int animationId) {
        val definitions = AnimationDefinitions.get(animationId);
        if (definitions == null) {
            return -1;
        }
        val frameIds = definitions.getFrameIds();
        if (frameIds != null) {
            return SkeletonDefinitions.get(frameIds[0]).getFrameMapId();
        }
        val additionalFrameIds = definitions.getExtraFrameIds();
        if (additionalFrameIds != null) {
            return SkeletonDefinitions.get(additionalFrameIds[0]).getFrameMapId();
        }
        return -1;
    }

    private void verifyCRC() {
        if (singleton.crc == getCRC()) {
            return;
        }
        System.err.println("CRC mismatch in animation map - extracting new animation map.");
        refresh();
        crc = getCRC();
        save();
    }

    private int getCRC() {
        return Game.crc[2];
    }

    public void save() {
        try {
            val pw = new PrintWriter("data/animations.json", "UTF-8");
            pw.println(World.getGson().toJson(singleton));
            pw.close();
            System.err.println("Animation map successfully saved.");
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

    public static void refresh() {
        try {
            val map = new Int2ObjectOpenHashMap<IntOpenHashSet>();
            val skeletonDefinitions = SkeletonDefinitions.getDefinitions();
            val iterator = skeletonDefinitions.int2ObjectEntrySet().fastIterator();
            val length = SkeletonDefinitions.getDefinitions().size();
            int count = 0;
            while (iterator.hasNext()) {
                val next = iterator.next();
                try {
                    val definitions = next.getValue();
                    val frameMapId = definitions.getFrameMapId();
                    if (map.containsKey(frameMapId)) {
                        System.err.println("[Skipping] Progress: " + ++count + "/" + length);
                        continue;
                    }
                    val animations = new IntOpenHashSet();
                    val frameIds = SkeletonDefinitions.getLinkedFrames(frameMapId);
                    for (val frameId : frameIds) {
                        val foundAnimations = AnimationDefinitions.getAnimationIdsByFrameId(frameId, frameIds);
                        val it = foundAnimations.listIterator();
                        while (it.hasNext()) {
                            val foundAnimation = it.nextInt();
                            if (foundAnimation != -1 && !animations.contains(foundAnimation)) {
                                animations.add(foundAnimation);
                            }
                        }
                    }
                    map.put(frameMapId, animations);
                    System.err.println("Progress: " + ++count + "/" + length);
                } catch (final Exception e) {
                    log.error(Strings.EMPTY, e);
                }
            }
            singleton.map = map;
            System.err.println("Animation dump by frame map complete.");
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

}
