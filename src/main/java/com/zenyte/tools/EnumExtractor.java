package com.zenyte.tools;

import com.zenyte.Constants;
import com.zenyte.game.util.Utils;
import mgi.Indice;
import mgi.types.config.enums.EnumDefinitions;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author Kris | 6. march 2018 : 2:47.36
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 *      profile</a>}
 */
@Slf4j
public final class EnumExtractor implements Extractor {

	@Override
	public void extract() {
		try {
			//final int len = CacheUtil.getStore().getFilesSystem(2).findFolderByID(8).filesCount();
			final int len = Utils.getIndiceSize(Indice.MAPPED_VALUES);
			for (int i = 0; i < len; i++) {
				EnumDefinitions.get(i);
			}
			final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("info/#" + Constants.REVISION + " enum-list.txt")));
			for (final EnumDefinitions t : EnumDefinitions.definitions) {
				try {
					if (t == null) {
						continue;
					}
					if (t.getKeyType() != 0) {
						writer.write("[" + t.getId() + "]\r\n");
						writer.write("inputtype=" + EnumDefinitions.TYPE_MAP.get(t.getKeyType()) + "\r\n");
						writer.write("outputtype=" + EnumDefinitions.TYPE_MAP.get(t.getValueType()) + "\r\n");
						if (t.getValueType() == 's') {
							writer.write("default=" + EnumDefinitions.getPrettyValue(t.getValueType(), t.getDefaultString()) + "\r\n");
						} else {
							writer.write("default=" + EnumDefinitions.getPrettyValue(t.getValueType(), t.getDefaultInt()) + "\r\n");
						}

						for (final Map.Entry<Integer, Object> param : t.getValues().entrySet()) {
							writer.write("val=" + EnumDefinitions.getPrettyValue(t.getKeyType(), param.getKey()) + "," + EnumDefinitions.getPrettyValue(t.getValueType(), param.getValue()) + "\r\n");
						}
						writer.write("-----");
						writer.write("\r\n");
					}
				} catch (final IOException e) {
                    log.error(Strings.EMPTY, e);
				}
			}
			writer.flush();
			writer.close();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}

}
