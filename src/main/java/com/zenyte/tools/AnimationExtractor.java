package com.zenyte.tools;

import com.zenyte.Constants;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.race.Race;
import mgi.Indice;
import mgi.types.config.AnimationDefinitions;
import mgi.types.config.items.ItemDefinitions;
import mgi.types.config.npcs.NPCDefinitions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Kris | 3. apr 2018 : 3:51.28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class AnimationExtractor implements Extractor {

	@Override
	public void extract() {
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("info/#" + Constants.REVISION + " animations.txt")));
			final int len = Utils.getIndiceSize(Indice.ANIMATION_DEFINITIONS);
			
			for (final Race race : Race.values()) {
				writer.write("Race: " + Utils.formatString(race.toString()));
				writer.newLine();
				writer.write("Suitable NPCs: ");
				for (int i = 0; i < Utils.getIndiceSize(Indice.NPC_DEFINITIONS); i++) {
					final NPCDefinitions defs = NPCDefinitions.get(i);
					if (defs == null) {
						continue;
					}
					final AnimationDefinitions anim = AnimationDefinitions.get(defs.getStandAnimation());
					if (anim == null) {
						continue;
					}
					if (anim.getFrameIds() == null) {
						continue;
					}
					if (anim.getFrameIds().length == 0) {
						continue;
					}
					final int skeleton = anim.getFrameIds()[0] >> 16;
					if (ArrayUtils.contains(race.getSkeletonIds(), skeleton)) {
						writer.write(defs.getName() + "(" + i + "), ");
					}
				}
				writer.newLine();
				writer.write("----------");
				writer.newLine();
			}
			
			final String br = System.getProperty("line.separator");
			for (int i = 0; i < len; i++) {
				final StringBuilder builder = new StringBuilder();
				final AnimationDefinitions defs = AnimationDefinitions.get(i);
				if (defs == null) {
					continue;
				}
				builder.append("Animation: " + defs.getId() + br);
				final int skeletonId = (defs.getFrameIds()[0] >> 16);
				final Race race = Race.MAP.get(skeletonId);
				
				if (race != null) {
					builder.append("Race: " + Utils.formatString(race.toString()) + br);
				}
					
				builder.append("Duration: " + defs.getDuration() + "ms" + br);
				if (defs.getIterations() > 1 && defs.getIterations() < 99) {
					builder.append("Loops: " + defs.getIterations() + br);
					builder.append("Full duration: " + (defs.getIterations() * defs.getDuration()) + "ms" + br);
				}
				final List<Integer> soundEffects = new ArrayList<Integer>();
				if (defs.getSoundEffects() != null) {
					for (final int x : defs.getSoundEffects()) {
						if ((x >> 8) != 0) {
							soundEffects.add((x >> 8));
						}
					}
				}
				if (!soundEffects.isEmpty()) {
					builder.append("Sound effects: " + Arrays.toString(soundEffects.toArray(new Integer[soundEffects.size()])) + br);
				}
				if (defs.getLeftHandItem() > 0) {
					builder.append("Left item: " + ItemDefinitions.get(defs.getLeftHandItem()).getName() + ", " + defs.getLeftHandItem() + br);
				}
				if (defs.getRightHandItem() > 0) {
					try {
					builder.append("Right item: " + ItemDefinitions.get(defs.getRightHandItem()).getName() + ", " + defs.getRightHandItem() + br);
					} catch (final Exception a) {
						a.printStackTrace();
						System.err.println(defs.getRightHandItem() + ", " + defs.getId());
					}
				}
				if (race == null) {
					final Map<Integer, String> npcs = new HashMap<Integer, String>();
					for (int x = 0; x < Utils.getIndiceSize(Indice.NPC_DEFINITIONS); x++) {
						final NPCDefinitions npcDefs = NPCDefinitions.get(x);
						if (npcDefs == null) {
							continue;
						}
						if (npcDefs.getStandAnimation() > 0) {
							final AnimationDefinitions animDefs = AnimationDefinitions.get(npcDefs.getStandAnimation());
							if (animDefs == null) {
								continue;
							}
							final int id = animDefs.getFrameIds()[0] >> 16;
							if (id == skeletonId) {
								npcs.put(x, npcDefs.getName());
							}
						}
					}
					if (!npcs.isEmpty()) {

						final StringBuilder b = new StringBuilder();

						final Iterator<Entry<Integer, String>> it = npcs.entrySet().iterator();
						int count = 0;
						while (it.hasNext()) {
							final Entry<Integer, String> x = it.next();
							if (x.getValue().equals("null")) {
								continue;
							}
							b.append(x.getValue() + "(" + x.getKey() + "), ");
							if (it.hasNext() && ++count % 11 == 10) {
								b.append(br);
							}
						}
						if (b.length() > 0) {
							b.delete(b.length() - 2, b.length());
							builder.append("Suitable monsters: " + b.toString() + br);
						}
					}
				}
				
				writer.write(builder.toString());
				writer.write("----------" + br);
				
			}
			writer.flush();
			writer.close();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}

}
