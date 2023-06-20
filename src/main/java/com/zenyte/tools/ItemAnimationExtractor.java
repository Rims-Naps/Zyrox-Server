package com.zenyte.tools;

import com.zenyte.Constants;
import com.zenyte.game.util.Utils;
import mgi.Indice;
import mgi.types.config.AnimationDefinitions;
import mgi.types.config.items.ItemDefinitions;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * @author Kris | 5. march 2018 : 17:22.05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class ItemAnimationExtractor implements Extractor {

	@Override
	public void extract() {
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("info/#" + Constants.REVISION + " item-animations.txt")));
			final int len = Utils.getIndiceSize(Indice.ANIMATION_DEFINITIONS);
			for (int i = 0; i < len; i++) {
				final AnimationDefinitions defs = AnimationDefinitions.get(i);
				if (defs == null)
					continue;
				if (defs.getLeftHandItem() > 0 || defs.getRightHandItem() > 0) {
					final StringBuilder line = new StringBuilder();
					line.append(i + ": ");
					if (defs.getLeftHandItem() > 0) {
						final ItemDefinitions item = ItemDefinitions.get(defs.getLeftHandItem());
						line.append("Left: " + (item == null ? "null" : item.getName()) + "(" + (defs.getLeftHandItem()) + "), ");
					} 
					if (defs.getRightHandItem() > 0) {
						final ItemDefinitions item = ItemDefinitions.get(defs.getRightHandItem());
						line.append("Right: " + (item == null ? "null" : item.getName()) + "(" + (defs.getRightHandItem()) + "), ");
					} 
					writer.write(line.toString());
					if (i < len)
						writer.newLine();
				}
			}
			writer.flush();
			writer.close();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}
	
}
