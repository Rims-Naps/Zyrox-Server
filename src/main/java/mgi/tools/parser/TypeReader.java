package mgi.tools.parser;

import com.moandjiezana.toml.Toml;
import lombok.val;
import mgi.tools.parser.readers.*;
import mgi.types.Definitions;
import mgi.types.config.GraphicsDefinitions;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tommeh | 22/01/2020 | 18:56
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public interface TypeReader {
    
    TypeReader[] values = {
            new ItemReader(), new NPCReader(),
            new ObjectReader(), new EnumReader(),
            new StructReader(), new AnimationReader(),
            new SpriteReader(), new ComponentReader(),
            new GraphicsReader(), new VarbitReader(),
            new InventoryReader(), new VarclientReader(),
    };

    Map<String, TypeReader> readers = Arrays.asList(values).stream()
            .collect(Collectors.toMap(e -> e.getType(), e -> e));

    default ArrayList<Definitions> read(final Map<String, Object> properties) throws NoSuchFieldException, IllegalAccessException, CloneNotSupportedException, InstantiationException {
        return new ArrayList<>();
    }
    
    default ArrayList<Definitions> read(final Toml toml) throws NoSuchFieldException, IllegalAccessException, CloneNotSupportedException {
        return new ArrayList<>();
    }
    
    String getType();

    static void setFields(final Definitions definitions, final Map<String, Object> properties) throws IllegalAccessException, NoSuchFieldException {
        val clazz = definitions.getClass();

        for (val field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            val property = TypeProperty.get(field.getName());
            if (property == null) {
                continue;
            }
            if (definitions instanceof GraphicsDefinitions && property.equals(TypeProperty.REPLACEMENT_COLOURS)) {
                continue;
            }

            val identifier = property.getIdentifier();
            if (!properties.containsKey(identifier)) {
                continue;
            }
            if (property.equals(TypeProperty.DEFAULT_INT) || property.equals(TypeProperty.DEFAULT_STRING)) {
                val value = properties.get(identifier);
                if (value instanceof Long) {
                    val f = clazz.getDeclaredField(TypeProperty.DEFAULT_INT.getField());
                    f.setAccessible(true);
                    f.set(definitions, ((Long) value).intValue());
                } else {
                    val f = clazz.getDeclaredField(TypeProperty.DEFAULT_STRING.getField());
                    f.setAccessible(true);
                    f.set(definitions, value);
                }
                continue;
            }
            if (field.getType() == String.class) {
                field.set(definitions, properties.get(identifier));
            } else if (field.getType() == int.class) {
                field.set(definitions, ((Long) properties.get(identifier)).intValue());
            } else if (field.getType() == boolean.class) {
                field.set(definitions, properties.get(identifier));
            } else if (field.getType() == int[].class) {
                val list = (ArrayList<Long>) properties.get(identifier);
                field.set(definitions, list.stream().mapToInt(i -> i.intValue()).toArray());
            } else if (field.getType() == short[].class) {
                val list = (ArrayList<Long>) properties.get(identifier);
                val array = new short[list.size()];
                for (int index = 0; index < array.length; index++) {
                    array[index] = list.get(index).shortValue();
                }
                field.set(definitions, array);
            } else if (field.getType() == String[].class) {
                val list = (ArrayList<String>) properties.get(identifier);
                if ((property.equals(TypeProperty.OPTIONS_ITEM) || property.equals(TypeProperty.OPTIONS_NPC_OBJECT) || property.equals(TypeProperty.FILTERED_OPTIONS))
                        && list.isEmpty()) {
                    field.set(definitions, new String[5]);
                } else {
                    val array = new String[5];
                    for (int index = 0; index < array.length; index++) {
                        val option = list.get(index);
                        array[index] = option.isEmpty() ? null : option;
                    }
                    field.set(definitions, array);
                }
            }
        }
    }
}
