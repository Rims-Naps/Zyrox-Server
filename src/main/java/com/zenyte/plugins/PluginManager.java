package com.zenyte.plugins;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

/**
 * @author Kris | 21/03/2019 16:16
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class PluginManager {

    /**
     * A map of executables, wherein the key is the event class, and the value is a set of subscribed methods.
     */
    private static final Map<Class<?>, Set<Method>> executableMap = new Object2ObjectOpenHashMap<>();

    /**
     * Regosters an executable method.
     *
     * @param superClass the class in which the subscribed event is.
     * @param executable the executable method subscribed.
     */
    public static final void register(@NotNull final Class<?> superClass, @NotNull final Method executable) {
        //Do not register the event if the method isn't declared by the class publishing it. Static methods are still inherited through extensions, so we filter all those.
        if (!ArrayUtils.contains(superClass.getDeclaredMethods(), executable)) {
            return;
        }
        Preconditions.checkArgument(Modifier.isStatic(executable.getModifiers()), "Registered events must be static: " + (superClass.getSimpleName() + "::" + executable.getName()));
        Preconditions.checkArgument(executable.isAnnotationPresent(Subscribe.class), "Registered event must have Subscribe annotation preset: " + (superClass.getSimpleName() + "::" + executable.getName()));
        Preconditions.checkArgument(executable.getParameterCount() == 1, "Registered events must only contain one parameter: " + (superClass.getSimpleName() + "::" + executable.getName()));
        val event = executable.getParameters()[0];
        val type = event.getType();
        //log.info("Registering: [" + type.getSimpleName() + "] " + superClass.getSimpleName() + "::" + executable.getName());
        Preconditions.checkArgument(Event.class.isAssignableFrom(type), "Subscribed method's parameter must implement the Event interface.");
        executableMap.computeIfAbsent(type, r -> new ObjectLinkedOpenHashSet<>()).add(executable);
    }

    /**
     * Publishes an event to the listener.
     *
     * @param event the event published.
     */
    public static final void post(@NotNull final Event event) {
        val clazz = event.getClass();
        val executables = executableMap.get(clazz);
        if (executables == null) {
            return;
        }
        for (val executable : executables) {
            try {
                executable.invoke(null, event);
            } catch (Exception e) {
                log.error(Strings.EMPTY, e);
            }
        }
    }

}
