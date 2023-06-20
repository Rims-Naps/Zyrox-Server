package com.zenyte.tools;

import com.zenyte.Constants;
import com.zenyte.game.util.Utils;
import mgi.Indice;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Extracts information from the world map.
 * @author Kris | 5. march 2018 : 17:12.30
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class WorldMapExtractor implements Extractor {

	@Override
	public void extract() {
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("info/#" + Constants.REVISION + " coords-list.txt")));
			final int len = Utils.getIndiceSize(Indice.WORLD_MAP);
			for (int i = 0; i < len; i++) {
				/*final WorldMapDefinitions defs = WorldMapDefinitions.get(i);
				if (defs == null)
					continue;
				//writer.write(i + " - " + defs.getName() + " | " + defs.getFileName() + ": " + defs.getLocation());
				if (i < len)
					writer.newLine();*/
			}
			writer.flush();
			writer.close();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}
	
}
