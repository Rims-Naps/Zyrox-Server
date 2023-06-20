package mgi.tools.parser.readers;

import lombok.val;
import mgi.tools.parser.TypeReader;
import mgi.types.Definitions;
import mgi.types.config.InventoryDefinitions;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Kris | 26/09/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class InventoryReader implements TypeReader {
    @Override
    public ArrayList<Definitions> read(final Map<String, Object> properties) throws NoSuchFieldException, IllegalAccessException {
        val defs = new ArrayList<Definitions>();
        if (properties.containsKey("inherit")) {
            val inherit = properties.get("inherit");
            defs.add(InventoryDefinitions.get(((Long) inherit).intValue()));
        } else {
            defs.add(new InventoryDefinitions());
        }
        for (val definition : defs) {
            val inventory = (InventoryDefinitions) definition;
            TypeReader.setFields(inventory, properties);
        }
        return defs;
    }

    @Override
    public String getType() {
        return "inventory";
    }
}
