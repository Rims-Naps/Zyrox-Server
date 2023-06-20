package com.zenyte.game.ui;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.Modifier;

/**
 * @author Kris | 19/10/2018 01:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
public class NewInterfaceHandler {

    public static final Int2ObjectOpenHashMap<Interface> INTERFACES = new Int2ObjectOpenHashMap<>();

    public static final void add(final Class<? extends Interface> clazz) {
        try {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                return;
            }
            val instance = clazz.newInstance();
            instance.attach();
            instance.build();
            INTERFACES.put(instance.getInterface().getId(), instance);
        } catch (Exception e) {
            log.error(Strings.EMPTY, e);
        }
    }

}
