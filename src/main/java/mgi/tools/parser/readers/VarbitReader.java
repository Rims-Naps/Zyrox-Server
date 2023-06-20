package mgi.tools.parser.readers;

import lombok.val;
import mgi.tools.parser.TypeReader;
import mgi.types.Definitions;
import mgi.types.config.VarbitDefinitions;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Kris | 16/08/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class VarbitReader implements TypeReader {
    @Override
    public ArrayList<Definitions> read(final Map<String, Object> properties) throws NoSuchFieldException, IllegalAccessException {
        val defs = new ArrayList<Definitions>();
        if (properties.containsKey("inherit")) {
            val inherit = properties.get("inherit");
            defs.add(VarbitDefinitions.get(((Long) inherit).intValue()));
        } else {
            defs.add(new VarbitDefinitions());
        }
        for (val definition : defs) {
            val varbit = (VarbitDefinitions) definition;
            TypeReader.setFields(varbit, properties);
        }
        return defs;
    }

    @Override
    public String getType() {
        return "varbit";
    }
}
