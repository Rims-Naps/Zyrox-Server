package com.zenyte.plugins.equipment.equip;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 25. jaan 2018 : 4:36.14
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public class EquipPluginLoader {

	public static final Int2ObjectOpenHashMap<EquipPlugin>  PLUGINS = new Int2ObjectOpenHashMap<EquipPlugin>();
	
	public static final void add(final Class<?> c) {
		try {
			if (c.isAnonymousClass()) {
				return;
			}
			if (c.isInterface()) {
				return;
			}
			final Object o = c.newInstance();
			if (!(o instanceof EquipPlugin)) {
				return;
			}
			final EquipPlugin action = (EquipPlugin) o;
			for (final int item : action.getItems()) {
				PLUGINS.put(item, action);
			}
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}
	
}
