package mgi.tools.parser.readers;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;

import mgi.tools.parser.TypeProperty;
import mgi.tools.parser.TypeReader;
import mgi.types.Definitions;
import mgi.types.config.StructDefinitions;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tommeh | 23/01/2020 | 21:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class StructReader implements TypeReader {

    @Override
    public ArrayList<Definitions> read(final Map<String, Object> properties) throws NoSuchFieldException, IllegalAccessException {
        val defs = new ArrayList<Definitions>();
        if (properties.containsKey("inherit")) {
            val inherit = properties.get("inherit");
            defs.add(StructDefinitions.get(((Long) inherit).intValue()));
        } else {
            defs.add(new StructDefinitions());
        }
        for (val definition : defs) {
            val struct = (StructDefinitions) definition;
            TypeReader.setFields(struct, properties);
            var property = TypeProperty.PARAMETERS;
            if (properties.containsKey(property.getIdentifier())) {
                val map = (Map<String, Object>) properties.get(property.getIdentifier());
                var clear = false;
                if (map.containsKey("clear")) {
                    clear = (Boolean) map.get("clear");
                }
                map.remove("clear");
                val parameters = new Int2ObjectOpenHashMap<Object>(map.entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> Integer.parseInt(e.getKey()), Map.Entry::getValue)));
                if (clear || struct.getParameters() == null) {
                    struct.setParameters(parameters);
                } else {
                    for (val entry : parameters.int2ObjectEntrySet()) {
                        struct.getParameters().put(entry.getIntKey(), entry.getValue());
                    }
                }
            }
        }
        return defs;
    }

    @Override
    public String getType() {
        return "struct";
    }
}
