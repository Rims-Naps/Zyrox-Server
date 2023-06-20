package mgi.types.config.enums;

import com.google.common.collect.ImmutableMap;
import com.zenyte.Game;
import com.zenyte.game.util.BitUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.File;
import mgi.types.Definitions;
import mgi.types.config.ObjectDefinitions;
import mgi.types.config.items.ItemDefinitions;
import mgi.utilities.ByteBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Kris | 3. march 2018 : 16:32.44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@Slf4j
@ToString
@NoArgsConstructor(force = true)
public final class EnumDefinitions implements Definitions, Cloneable {

    public static final ImmutableMap<Character, String> TYPE_MAP = ImmutableMap.<Character, String>builder().put('A', "seq").put('i', "int")
            .put('1', "boolean").put('s', "string").put('v', "inv").put('z', "char").put('O', "namedobj").put('M', "midi").put('K', "idkit")
            .put('o', "obj").put('n', "npc").put('c', "coordgrid").put('S', "stat").put('m', "model").put('d', "graphic").put('J', "struct")
            .put('f', "fontmetrics").put('I', "component").put('k', "chatchar").put('g', "enum").put('l', "location").build();

    public static final ImmutableMap<String, Character> REVERSE_TYPE_MAP =
            ImmutableMap.<String, Character>builder().putAll(TYPE_MAP.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey))).build();

    private static Int2ObjectOpenHashMap<Enum> map;

    public static EnumDefinitions[] definitions;

    public static IntEnum getIntEnum(final int id) {
        val e = map.get(id);
        if (!(e instanceof IntEnum)) {
            throw new RuntimeException("Enum isn't an instanceof IntEnum!");
        }
        return (IntEnum) e;
    }

    public static StringEnum getStringEnum(final int id) {
        val e = map.get(id);
        if (!(e instanceof StringEnum)) {
            throw new RuntimeException("Enum isn't an instanceof StringEnum!");
        }
        return (StringEnum) e;
    }

    public static Enum<?> getEnum(final int id) {
        return map.get(id);
    }

    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val configs = cache.getArchive(ArchiveType.CONFIGS);
        val enums = configs.findGroupByID(GroupType.ENUM);
        definitions = new EnumDefinitions[enums.getHighestFileId()];
        map = new Int2ObjectOpenHashMap<>(enums.getHighestFileId());
        for (int id = 0; id < enums.getHighestFileId(); id++) {
            val file = enums.findFileByID(id);
            if (file == null) {
                continue;
            }
            val buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            definitions[id] = new EnumDefinitions(id, buffer);
        }
    }

    @Getter
    @Setter
    private int id;
    private int size;
    @Getter
    private char keyType;
    @Getter
    private char valueType;
    @Getter
    @Setter
    private String defaultString;
    @Getter
    @Setter
    private int defaultInt;
    @Getter
    @Setter
    private HashMap<Integer, Object> values;
    private transient int largestIntValue = -1;
    private transient int largestIntKey = -1;

    private EnumDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        defaultString = "null";
        decode(buffer);
        if (values == null) {
            return;
        }
        Enum anEnum;
        if (valueType == 's') {
            anEnum = new StringEnum(id, keyType, valueType, defaultString, values);
        } else {
            anEnum = new IntEnum(id, keyType, valueType, defaultInt, values);
        }
        map.put(id, anEnum);
    }

    public EnumDefinitions clone() throws CloneNotSupportedException {
        return (EnumDefinitions) super.clone();
    }

    public void setKeyType(final String keyType) {
        val type = REVERSE_TYPE_MAP.get(keyType);
        if (type == null) {
            throw new RuntimeException("Unable to find a matching type for " + keyType + ".");
        }
        this.keyType = type;
    }

    public void setValueType(final String valueType) {
        val type = REVERSE_TYPE_MAP.get(valueType);
        if (type == null) {
            throw new RuntimeException("Unable to find a matching type for " + valueType + ".");
        }
        this.valueType = type;
    }

    public int getLargestIntValue() {
        if (largestIntValue != -1) {
            return largestIntValue;
        }
        int largestValue = 0;
        val iterator = values.entrySet().iterator();
        while (iterator.hasNext()) {
            val next = iterator.next();
            if (next.getValue() instanceof Integer) {
                val intValue = (int) next.getValue();
                if (intValue > largestValue) {
                    largestValue = intValue;
                }
            }
        }
        largestIntValue = largestValue;
        return largestIntValue;
    }

    public int getLargestIntKey() {
        if (largestIntKey != -1) {
            return largestIntKey;
        }
        int largestKey = 0;
        val iterator = values.entrySet().iterator();
        while (iterator.hasNext()) {
            val next = iterator.next();
            if (next.getKey() instanceof Integer) {
                val intKey = next.getKey();
                if (intKey > largestKey) {
                    largestKey = intKey;
                }
            }
        }
        largestIntKey = largestKey;
        return largestIntKey;
    }

    @Override
    public void decode(final ByteBuffer buffer) {
        while (true) {
            val opcode = buffer.readUnsignedByte();
            if (opcode == 0) {
                return;
            }
            decode(buffer, opcode);
        }
    }

    @Override
    public void decode(final ByteBuffer buffer, final int opcode) {
        switch (opcode) {
            case 1:
                keyType = (char) buffer.readUnsignedByte();
                return;
            case 2:
                valueType = (char) buffer.readUnsignedByte();
                return;
            case 3:
                defaultString = buffer.readString();
                return;
            case 4:
                defaultInt = buffer.readInt();
                return;
            case 5:
                size = buffer.readUnsignedShort();
                values = new HashMap<>(BitUtils.nextPowerOfTwo(size));
                for (int index = 0; index < size; ++index) {
                    val key = buffer.readInt();
                    val value = buffer.readString();
                    values.put(key, value);
                }
                return;
            case 6:
                size = buffer.readUnsignedShort();
                values = new HashMap<>(BitUtils.nextPowerOfTwo(size));
                for (int index = 0; index < size; ++index) {
                    val key = buffer.readInt();
                    val value = buffer.readInt();
                    values.put(key, value);
                }
                return;
        }
    }

    public static final EnumDefinitions get(final int id) {
        if (id < 0 || id >= definitions.length) {
            return null;
        }
        return definitions[id];
    }

    public static final Optional<EnumDefinitions> getOptional(final int id) {
        if (id < 0 || id >= definitions.length) {
            return Optional.empty();
        }
        return Optional.of(definitions[id]);
    }

    public int getKeyForValue(final Object value) {
        for (val key : values.keySet()) {
            if (values.get(key).equals(value)) {
                return key;
            }
        }
        return -1;
    }

    public int getKeyForStringValue(final String value) {
        for (val key : values.keySet()) {
            if (values.get(key).toString().toLowerCase().equals(value.toLowerCase())) {
                return key;
            }
        }
        return -1;
    }

    public int getSize() {
        if (values == null) {
            return 0;
        }
        return values.size();
    }

    public int getIntValue(final int key) {
        if (values == null) {
            return defaultInt;
        }
        val value = values.get(key);
        if (value == null || !(value instanceof Integer)) {
            return defaultInt;
        }
        return (Integer) value;
    }

    public int getIntValueOrDefault(final int key, final int defaultValue) {
        if (values == null) {
            return defaultValue;
        }
        val value = values.get(key);
        if (value == null || !(value instanceof Integer)) {
            return defaultValue;
        }
        return (Integer) value;
    }

    public String getStringValue(final int key) {
        if (values == null) {
            return defaultString;
        }
        val value = values.get(key);
        if (value == null || !(value instanceof String)) {
            return defaultString;
        }
        return (String) value;
    }

    public Optional<String> getString(final int key) {
        if (values == null) {
            return Optional.ofNullable(defaultString);
        }
        val value = values.get(key);
        if (value == null || !(value instanceof String)) {
            return Optional.ofNullable(defaultString);
        }
        return Optional.of((String) value);
    }

    public static final String getPrettyValue(final char type, final Object value) {
        if (type == 's') {
            return (String) value;
        }
        final int intVal = (int) value;
        if (intVal == -1) {
            return "null";
        }
        switch (type) {
            case 'I':
                return (intVal >> 16) + ":" + (intVal & 0xffff);
            case 'c':
                final int x = intVal >> 14 & 16383;
                final int y = intVal & 16383;
                final int z = intVal >> 28;
                return x + "_" + y + "_" + z;
            case 'o':
            case 'O':
                return ItemDefinitions.get(intVal).getName().toLowerCase().replaceAll(" ", "_") + "_" + intVal;
            case 'l':
                return ObjectDefinitions.get(intVal).getName().toLowerCase().replaceAll(" ", "_") + "_" + intVal;
            default:
                return Integer.toString(intVal);
        }
    }

    @Override
    public ByteBuffer encode() {
        val buffer = new ByteBuffer(4096 * 8);

        buffer.writeByte(1);
        buffer.writeByte(keyType);

        buffer.writeByte(2);
        buffer.writeByte(valueType);

        if (valueType == 's') {
            buffer.writeByte(3);
            buffer.writeString(defaultString);

            if (values != null && !values.isEmpty()) {
                buffer.writeByte(5);
                buffer.writeShort(values.size());
                for (val entry : values.entrySet()) {
                    buffer.writeInt(entry.getKey());
                    buffer.writeString(entry.getValue().toString());
                }
            }
        } else {
            if (defaultInt != 0) {
                buffer.writeByte(4);
                buffer.writeInt(defaultInt);
            }
            if (values != null && !values.isEmpty()) {
                buffer.writeByte(6);
                buffer.writeShort(values.size());
                for (val entry : values.entrySet()) {
                    buffer.writeInt(entry.getKey());
                    buffer.writeInt(Double.valueOf(entry.getValue().toString()).intValue());
                }
            }
        }

        buffer.writeByte(0);
        return buffer;
    }

    @Override
    public void pack() {
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.ENUM).addFile(new File(id, encode()));
    }
}
