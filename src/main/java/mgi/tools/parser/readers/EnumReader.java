package mgi.tools.parser.readers;

import lombok.val;
import lombok.var;
import mgi.tools.parser.TypeProperty;
import mgi.tools.parser.TypeReader;
import mgi.types.Definitions;
import mgi.types.config.enums.EnumDefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tommeh | 22/01/2020 | 19:01
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class EnumReader implements TypeReader {

    @Override
    public ArrayList<Definitions> read(final Map<String, Object> properties) throws NoSuchFieldException, IllegalAccessException {
        val defs = new ArrayList<Definitions>();
        if (properties.containsKey("inherit")) {
            val inherit = properties.get("inherit");
            defs.add(EnumDefinitions.get(((Long) inherit).intValue()));
        } else {
            defs.add(new EnumDefinitions());
        }
        for (val definition : defs) {
            val enumDef = (EnumDefinitions) definition;
            TypeReader.setFields(enumDef, properties);
            var property = TypeProperty.VALUES;
            if (properties.containsKey(property.getIdentifier())) {
                val map = (Map<String, Object>) properties.get(property.getIdentifier());
                var clear = false;
                if (map.containsKey("clear")) {
                    clear = (Boolean) map.get("clear");
                }
                map.remove("clear");
                val values = new HashMap<Integer, Object>(map.entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> Integer.parseInt(e.getKey()), e -> e.getValue() instanceof Long ? ((Long) e.getValue()).intValue() : e.getValue())));
                if (clear || enumDef.getValues() == null) {
                    enumDef.setValues(values);
                } else {
                    for (val entry : values.entrySet()) {
                        val value = entry.getValue();
                        enumDef.getValues().put(entry.getKey(), value instanceof Long ? ((Long) value).intValue() : value);
                    }
                }
            }
            property = TypeProperty.KEY_TYPE;
            if (properties.containsKey(property.getIdentifier())) {
                enumDef.setKeyType((String) properties.get(property.getIdentifier()));
            }
            property = TypeProperty.VALUE_TYPE;
            if (properties.containsKey(property.getIdentifier())) {
                enumDef.setValueType((String) properties.get(property.getIdentifier()));
            }
        }
        return defs;
    }

    @Override
    public String getType() {
        return "enum";
    }
}
