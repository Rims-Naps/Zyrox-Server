package mgi.types.config;

import com.zenyte.Game;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.File;
import mgi.types.Definitions;
import mgi.utilities.ByteBuffer;

public class VarclientDefinitions implements Definitions {

    public static VarclientDefinitions[] definitions;
    @Getter
    @Setter
    private int id;
    private boolean persists = false;

    public VarclientDefinitions() {
        setDefaults();
    }

    public VarclientDefinitions(int id) {
        this.id = id;
        setDefaults();
    }

    public VarclientDefinitions(int id, ByteBuffer buffer) {
        this.id = id;
        setDefaults();
        decode(buffer);
    }

    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val configs = cache.getArchive(ArchiveType.CONFIGS);
        val varcs = configs.findGroupByID(GroupType.VARCLIENT);
        definitions = new VarclientDefinitions[varcs.getHighestFileId()];
        for(int id = 0; id < varcs.getHighestFileId(); id++) {
            val file = varcs.findFileByID(id);
            if(file == null) {
                continue;
            }
            val buffer = file.getData();
            if(buffer == null) {
                continue;
            }
            buffer.setPosition(0);
            definitions[id] = new VarclientDefinitions(id, buffer);
        }
    }

    public void setDefaults() {
        persists = false;
    }

    public static final VarclientDefinitions get(final int id) {
        if (id < 0 || id >= definitions.length) {
            return null;
        }
        return definitions[id];
    }

    @Override
    public void decode(ByteBuffer buffer) {
        while (true) {
            val opcode = buffer.readUnsignedByte();
            if (opcode == 0) {
                return;
            }
            decode(buffer, opcode);
        }
    }

    @Override
    public void decode(ByteBuffer buffer, int opcode) {
        switch(opcode) {
            case 2:
                persists = true;
                return;
            default:
                System.out.println("Unrecognized varc opcode: " + opcode);
        }
    }

    @Override
    public ByteBuffer encode() {
        val buffer = new ByteBuffer(1024);
        if(persists) {
            buffer.writeByte(2);
        }
        buffer.writeByte(0);
        return buffer;
    }

    @Override
    public void pack() {
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.VARCLIENT).addFile(new File(id, encode()));
    }
}