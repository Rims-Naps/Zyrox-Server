package com.zenyte.plugins.handlers;

import com.zenyte.game.util.Colour;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 19. juuli 2018 : 22:24:28
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public class InterfaceSwitchHandler {

	public static final Int2ObjectOpenHashMap<InterfaceSwitchPlugin> INTERFACES = new Int2ObjectOpenHashMap<InterfaceSwitchPlugin>();
	
	public static final void add(final Class<? extends InterfaceSwitchPlugin> c) {
		try {
			if (c.isAnonymousClass() || c.isMemberClass()) {
				return;
			}
			final InterfaceSwitchPlugin plugin = c.newInstance();
			for (final int key : plugin.getInterfaceIds()) {
				if (INTERFACES.containsKey(key)) {
					log.error(Colour.RED + "FATAL: Overriding an interface handler. ID: " + key + ", Class: " + plugin.getClass().getSimpleName());
				}
				INTERFACES.put(key, plugin);
			}
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}
	
}
