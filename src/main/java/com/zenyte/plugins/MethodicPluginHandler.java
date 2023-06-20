package com.zenyte.plugins;

import com.zenyte.processor.Listener;
import com.zenyte.processor.Listener.ListenerType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Kris | 18. juuli 2018 : 21:18:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
public class MethodicPluginHandler {

	private static final Map<ListenerType, List<RegisteredListener>> LISTENERS;

	static {
		val values = Listener.ListenerType.values();
		LISTENERS = new EnumMap<>(ListenerType.class);
		for (val value : values) {
			LISTENERS.put(value, new LinkedList<>());
		}

	}

	private static final class RegisteredListener {

		RegisteredListener(final Object instance, final Method method) {
			this.instance = instance;
			this.method = method;
		}

		Object instance;
		Method method;

	}

	public static final boolean invokePlugins(final ListenerType type, final Object... params) {
		try {
			for (val listener : LISTENERS.get(type)) {
				listener.method.invoke(listener.instance, params);
			}
			return true;
		} catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error(Strings.EMPTY, e);
		}
        return false;
	}

	public static final void register(final Class<?> c, final Executable executable) {
		try {
			if (!(executable instanceof Method)) {
				return;
			}
			val isStatic = Modifier.isStatic(executable.getModifiers());

			val listener = executable.getAnnotation(Listener.class);
			if (listener == null) {
				return;
			}
			val method = (Method) executable;
			method.setAccessible(true);

			val instance = (isStatic ? null : c.newInstance());

			LISTENERS.get(listener.type()).add(new RegisteredListener(instance, method));
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}

}
