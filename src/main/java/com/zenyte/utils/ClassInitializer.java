package com.zenyte.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Kris | 29. juuli 2018 : 15:32:30
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
@Slf4j
public class ClassInitializer {

	/**
	 * Initializes the given class and all its static blocks.
	 * @param c the class to be initialized.
	 */
	public static final void initialize(final Class<?> c) {
		try {
			Class.forName(c.getName());
		} catch (final ClassNotFoundException e) {
            log.error(Strings.EMPTY, e);
		}
	}

}
