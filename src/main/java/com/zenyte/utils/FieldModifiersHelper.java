package com.zenyte.utils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Jire
 */
public enum FieldModifiersHelper {
    ;

    private static final VarHandle MODIFIERS_VAR_HANDLE;

    static {
        try {
            Lookup lookup = MethodHandles.lookup();
            Lookup privateLookup = MethodHandles.privateLookupIn(Field.class, lookup);
            MODIFIERS_VAR_HANDLE = privateLookup.findVarHandle(Field.class, "modifiers", int.class);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void definalize(Field field) {
        int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            MODIFIERS_VAR_HANDLE.set(field, modifiers & ~Modifier.FINAL);
        }
    }

}
