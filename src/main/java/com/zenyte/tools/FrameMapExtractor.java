package com.zenyte.tools;

import com.zenyte.Constants;
import com.zenyte.game.util.Utils;
import mgi.Indice;
import mgi.types.config.AnimationDefinitions;
import mgi.types.config.GraphicsDefinitions;
import mgi.types.config.ObjectDefinitions;
import mgi.types.skeleton.SkeletonDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;

/**
 * @author Kris | 19. sept 2018 : 17:15:25
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public class FrameMapExtractor implements Extractor {

	@Override
	public void extract() {
		try {
			val writer = new BufferedWriter(new FileWriter(new File("info/#" + Constants.REVISION + " frame map.txt")));
			val map = new Int2ObjectAVLTreeMap<IntArrayList>();
			val skeletonDefinitions = SkeletonDefinitions.getDefinitions();
			val iterator = skeletonDefinitions.int2ObjectEntrySet().fastIterator();
			val length = Utils.getIndiceSize(Indice.SKELETON);
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
					val animations = new IntArrayList();
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
					Collections.sort(animations);
					map.put(frameMapId, animations);
					System.err.println("Progress: " + ++count + "/" + length);
				} catch (final Exception e) {
                    log.error(Strings.EMPTY, e);
				}
			}

			map.forEach((i, list) -> {
				try {
					writer.write("Frame map: " + i);
					writer.newLine();
					if (!list.isEmpty()) {
                        writer.write("Linked animations: " + list.toString());
                        writer.newLine();
                    }
					val linkedNPCs = new IntArrayList();
					for (int a = 0; a < Utils.getIndiceSize(Indice.NPC_DEFINITIONS); a++) {
						val definitions = NPCDefinitions.get(a);
						if (definitions == null)
						    continue;
						if (list.contains(definitions.getStandAnimation()) || list.contains(definitions.getWalkAnimation())) {
							if (!linkedNPCs.contains(a)) {
								linkedNPCs.add(a);
							}
						}
					}
					if (!linkedNPCs.isEmpty()) {
						val builder = new StringBuilder();
						builder.append("Linked NPCs: ");
						for (final int id : linkedNPCs) {
							val npcDefinitions = NPCDefinitions.get(id);
							if (npcDefinitions == null) {
								continue;
							}
							builder.append(npcDefinitions.getName()).append(" (").append(id).append("), ");
						}
						if (builder.length() > 2) {
							builder.delete(builder.length() - 2, builder.length());
						}
						if (builder.length() > 0) {
							writer.write(builder.toString());
							writer.newLine();
						}
					}

					val linkedGFXs = new IntArrayList();
					for (int a = 0; a < Utils.getIndiceSize(Indice.GRAPHICS_DEFINITIONS); a++) {
						val definitions = GraphicsDefinitions.get(a);
						if (definitions == null) {
							continue;
						}
						if (list.contains(definitions.getAnimationId())) {
							if (!linkedGFXs.contains(a)) {
								linkedGFXs.add(a);
							}
						}

					}
					if (!linkedGFXs.isEmpty()) {
						val builder = new StringBuilder();
						builder.append("Linked graphics: ");
						for (final int id : linkedGFXs) {
							val graphicsDefinitions = GraphicsDefinitions.get(id);
							if (graphicsDefinitions == null) {
								continue;
							}
							builder.append(id).append(", ");
						}
						if (builder.length() > 2) {
							builder.delete(builder.length() - 2, builder.length());
						}
						if (builder.length() > 0) {
							writer.write(builder.toString());
							writer.newLine();
						}
					}

					val linkedObjects = new IntArrayList();
					for (int a = 0; a < Utils.getIndiceSize(Indice.OBJECT_DEFINITIONS); a++) {
						val definitions = ObjectDefinitions.get(a);
						if (definitions == null) {
						    continue;
                        }
                        if (list.contains(definitions.getAnimationId())) {
							if (!linkedObjects.contains(a)) {
								linkedObjects.add(a);
							}
						}
					}
					if (!linkedObjects.isEmpty()) {
						val builder = new StringBuilder();
						builder.append("Linked objects: ");
						for (final int id : linkedObjects) {
							val objectDefinitions = ObjectDefinitions.get(id);
							if (objectDefinitions == null) {
							    continue;
                            }
                            builder.append(objectDefinitions.getName()).append(" (").append(id).append("), ");
						}
						if (builder.length() > 2) {
							builder.delete(builder.length() - 2, builder.length());
						}
						if (builder.length() > 0) {
							writer.write(builder.toString());
							writer.newLine();
						}
					}
					writer.newLine();
				} catch (final Exception e) {
                    log.error(Strings.EMPTY, e);
				}
			});
			System.err.println("Animation dump by frame map complete.");
			writer.flush();
			writer.close();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}

}
