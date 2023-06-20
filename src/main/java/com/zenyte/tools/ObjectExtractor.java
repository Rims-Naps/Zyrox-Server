package com.zenyte.tools;

import com.zenyte.Constants;
import com.zenyte.game.util.Utils;
import mgi.Indice;
import mgi.types.config.ObjectDefinitions;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

/**
 * @author Kris | 5. march 2018 : 17:49.35
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public final class ObjectExtractor implements Extractor {

	@Override
	public void extract() {
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("info/#" + Constants.REVISION + " object-defs.txt")));
			//final int len = CacheUtil.getStore().getFilesSystem(2).findFolderByID(6).filesCount(); //.STORE.getIndexes()[2].getLastFileId(6);
			final int len = Utils.getIndiceSize(Indice.OBJECT_DEFINITIONS);
			for (int i = 0; i < len; i++) {
				final ObjectDefinitions defs = ObjectDefinitions.get(i);
				if (defs == null) {
					continue;
				}
				final StringBuilder line = new StringBuilder();
				line.append(i + ": " + defs.getName() + ", ");
				if (defs.getVarp() != -1) {
					line.append("varp: " + defs.getVarp() + ", ");
				}
				if (defs.getVarbit() != -1) {
					line.append("varbit: " + defs.getVarbit() + ", ");
				}
				if (defs.getAnimationId() != -1) {
					line.append("animation: " +defs.getAnimationId() + ", ");
				}
				if (defs.getTypes() == null) {
					line.append("types: [10]");
				} else {
					line.append("types: " + Arrays.toString(defs.getTypes()));
				}
				writer.write(line.toString());
				if (i < len) {
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
