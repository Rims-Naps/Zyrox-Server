package mgi.tools.parser.readers;

import com.moandjiezana.toml.Toml;
import lombok.val;
import mgi.tools.parser.TypeReader;
import mgi.types.Definitions;
import mgi.types.config.VarclientDefinitions;

import java.util.ArrayList;
import java.util.Map;

public class VarclientReader implements TypeReader {
    @Override
    public ArrayList<Definitions> read(Map<String, Object> properties) throws NoSuchFieldException, IllegalAccessException, CloneNotSupportedException, InstantiationException {
        val defs = new ArrayList<Definitions>();
        if(properties.containsKey("inherit")) {
            val inherit = properties.get("inherit");
            defs.add(VarclientDefinitions.get(((Long) inherit).intValue()));
        } else {
            defs.add(new VarclientDefinitions(((Long)properties.get("id")).intValue()));
        }
        for(val definition : defs) {
            val varc = (VarclientDefinitions) definition;
            TypeReader.setFields(varc, properties);
        }
        return defs;
    }

    @Override
    public ArrayList<Definitions> read(Toml toml) throws NoSuchFieldException, IllegalAccessException, CloneNotSupportedException {
        return TypeReader.super.read(toml);
    }

    @Override
    public String getType() {
        return "varc";
    }
}