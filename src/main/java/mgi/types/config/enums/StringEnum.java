package mgi.types.config.enums;

import com.google.errorprone.annotations.Immutable;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import lombok.var;
import mgi.utilities.ByteBuffer;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * @author Kris | 20/11/2018 11:59
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 *
 * <p>A String representation of an enum held in the cache. We use type-specific enums because the language
 * restrictions of generics cause a lot of unnecessary garbage in the case of primitive type.</p>
 */
@Immutable
@SuppressWarnings({"unused", "WeakerAccess"})
public final class StringEnum extends AbstractEnum<String> {

    private transient final Int2ObjectMap<String> stringMap;
    private transient final Object2IntMap<String> reverseStringMap;
    @Accessors(fluent = true)
    @Getter(value = AccessLevel.PACKAGE)
    private transient final boolean containsOverlappingValues;

    public StringEnum(final int id, final char keyType, final char valType, final String defaultValue, final Map<Integer, ?> map) {
        super(id, keyType, valType, defaultValue, map);
        if (size == 0) {
            stringMap = null;
            reverseStringMap = null;
            containsOverlappingValues = false;
            return;
        }
        Int2ObjectMap<String> stringMap = new Int2ObjectOpenHashMap<>(size);
        Object2IntMap<String> reverseStringMap = new Object2IntOpenHashMap<>(size);
        this.containsOverlappingValues = populate(map, stringMap, reverseStringMap);
        this.stringMap = Int2ObjectMaps.unmodifiable(stringMap);
        this.reverseStringMap = containsOverlappingValues ? null : Object2IntMaps.unmodifiable(reverseStringMap);
    }

    /**
     * Allocates a new buffer of size {@link IntEnum#encodedSize()}, encodes it with the enum's data.
     * @return a new bytebuffer containing the enum in encoded form.
     */
    @Override
    public ByteBuffer encode() {
        val buffer = new ByteBuffer(encodedSize());

        buffer.writeByte(1);
        buffer.writeByte(keyType);

        buffer.writeByte(2);
        buffer.writeByte(valType);

        if (!defaultValue.isEmpty()) {
            buffer.writeByte(3);
            buffer.writeString(defaultValue);
        }
        if (stringMap != null) {
            buffer.writeByte(5);
            buffer.writeShort(stringMap.size());
            for (val entry : stringMap.int2ObjectEntrySet()) {
                buffer.writeInt(entry.getIntKey());
                buffer.writeString(entry.getValue());
            }
        }

        buffer.writeByte(0);
        return buffer;
    }

    @Override
    int encodedSize() {
        var size = 5;
        if (!defaultValue.isEmpty()) {
            size += 2 + defaultValue.length();
        }
        if (stringMap != null) {
            size += 3;
            for (val entry : stringMap.int2ObjectEntrySet()) {
                size += 5 + entry.getValue().length();
            }
        }
        return size;
    }

    @Override
    boolean populate(final Map<Integer, ?> map, final Map<Integer, String> populatedMap,
                     final Map<String, Integer> reversePopulatedMap) {
        int key;
        String value;
        boolean overlappingValues = false;
        for (val entry : map.entrySet()) {
            key = entry.getKey();
            value = (String) entry.getValue();
            if (!overlappingValues && reversePopulatedMap.containsKey(value)) {
                overlappingValues = true;
            }
            populatedMap.put(key, value);
            if (!overlappingValues) {
                reversePopulatedMap.put(value, key);
            }
        }
        return overlappingValues;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    /**
     * @deprecated Use {@link StringEnum#getValues()} instead to prevent autoboxing & unboxing.
     * @return a map containing the enum values.
     * @throws IllegalArgumentException if the map is empty.
     */
    @Override
    @Deprecated
    public Map<Integer, String> map() {
        verifyPresence();
        return stringMap;
    }

    /**
     * {@inheritDoc}
     * @deprecated use {@link StringEnum#getReverseValues()} instead to prevent autoboxing & unboxing.
     */
    @Override
    @Deprecated
    public Map<String, Integer> reverseMap() {
        verifyPresence();
        return reverseStringMap;
    }

    /**
     * @return the values of the enum in a primitive int2object map.
     * @throws RuntimeException if the enum is empty.
     */
    public Int2ObjectMap<String> getValues() {
        verifyPresence();
        return stringMap;
    }

    /**
     * @return the values of the enum in a primitive int2object map in reverse.
     * @throws RuntimeException if the enum is empty.
     * @throws IllegalArgumentException if the enum contains overlapping values.
     */
    public Object2IntMap<String> getReverseValues() {
        verifyPresence();
        if (reverseStringMap == null) {
            throw new IllegalArgumentException("Enum contains overlapping values: " + id);
        }
        return reverseStringMap;
    }

    /**
     * @param key the key of the map pair.
     * @return an Optional containing the value of the map pair, or empty representation if absent.
     * @throws RuntimeException if the enum is empty.
     */
    public Optional<String> getValue(final int key) {
        if (!getValues().containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(stringMap.get(key));
    }

    /**
     * @param value the value of the map pair.
     * @return an OptionalInt containing the key of the map pair, or empty representation if absent.
     * @throws RuntimeException if the enum is empty.
     * @throws IllegalArgumentException if the enum contains overlapping values.
     */
    public OptionalInt getKey(final String value) {
        if (!getReverseValues().containsKey(value)) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(reverseStringMap.getInt(value));
    }

    /**
     * Use of the method is not advised if at all possible. Method has to loop over all of the fields of the enum to
     * find the correct pair.
     * @param value the value of the map pair.
     * @return an OptionalInt containing the key of the map pair, or empty representation if absent.
     * @throws RuntimeException if the enum is empty.
     * @throws IllegalArgumentException if the enum contains overlapping values.
     */
    public OptionalInt getKeyIgnoreCase(final String value) {
        for (val entry : getValues().int2ObjectEntrySet()) {
            if (entry.getValue().equalsIgnoreCase(value)) {
                return OptionalInt.of(entry.getIntKey());
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Iterates over all of the map pairings, accepting the consumer on every pair.
     * @param consumer the entry consumer.
     * @throws RuntimeException if the enum is empty.
     */
    public void forEach(final Consumer<Int2ObjectMap.Entry> consumer) {
        for (val entry : getValues().int2ObjectEntrySet()) {
            consumer.accept(entry);
        }
    }

}
