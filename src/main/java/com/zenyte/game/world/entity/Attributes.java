package com.zenyte.game.world.entity;

import java.util.HashMap;
import java.util.Map;

public class Attributes {

    private Map<Object, Object> attributes = new HashMap<Object, Object>();

    /**
     * Gets an attribute from the attribute map based upon the key specified.
     *
     * @param key The key to retrieve the value.
     * @param <T> The type of the value.
     * @return The value.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Object key) {
        return (T) attributes.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object key, T fail) {
        T value = (T) attributes.get(key);
        if (value == null) {
            return fail;
        }
        return value;
    }

    /**
     * Gets a integer from the attribute map.
     *
     * @param key The key of the attribute to retrieve the value back later.
     * @return The value.
     */
    public int getInt(Object key) {
        Number n = get(key);
        if (n == null) {
            return -1;
        }
        return n.intValue();
    }

    /**
     * Gets a long from the attribute map.
     *
     * @param key The key of the attribute to retrieve the value back later.
     * @return The value.
     */
    public long getLong(Object key) {
        Number n = get(key);
        if (n == null) {
            return 0;
        }
        return n.longValue();
    }

    /**
     * Gets a boolean from the attribute map.
     *
     * @param key The key of the attribute to retrieve the value back later.
     * @return The value.
     */
    public boolean is(Object key) {
        Boolean b = get(key);
        if (b == null) {
            return false;
        }
        return b;
    }

    /**
     * Sets an attribute.
     *
     * @param key   The key of the attribute to retrieve the value back later.
     * @param value The value to set.
     */
    public void set(Object key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Subtracts an int attribute
     *
     * @param att
     * @param amount
     */
    public void subtractInt(String att, int amount) {
        Number n = get(att);
        if (n == null) {
            return;
        }
        int current = n.intValue();
        int new_count = current - amount;
        if (new_count < 0) {
            new_count = 0;
        }
        set(att, new_count);
    }

    public void addInt(String att, int amount) {
        Number n = get(att);
        if (n == null) {
            return;
        }
        int current = n.intValue();
        int new_count = current + amount;
        set(att, new_count);
    }

    /**
     * Checks if a {@link Boolean} key is active in the map.
     *
     * @param key
     * @return
     */
    public boolean active(Object key) {
        Boolean bool = get(key);
        if (bool == null) {
            return false;
        } else if (bool) {
            return true;
        }
        return false;
    }

    /**
     * If an attribute is set.
     *
     * @param key The attribute key.
     */
    public boolean isSet(Object key) {
        return attributes.containsKey(key);
    }

    /**
     * Un-sets an attribute.
     *
     * @param key The key of the attribute to un-set.
     */
    public void remove(Object key) {
        attributes.remove(key);
    }
}
