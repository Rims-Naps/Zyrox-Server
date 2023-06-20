package mgi.types.config;

import com.zenyte.Game;
import mgi.types.Definitions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.File;
import mgi.utilities.ByteBuffer;

import java.util.Optional;

/**
 * @author Kris | 14/01/2019 00:39
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@Slf4j
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
public class StructDefinitions implements Definitions, Cloneable {

    private static StructDefinitions[] definitions;

    public static final StructDefinitions get(final int id) {
        if (id < 0 || id >= definitions.length)
            throw new IllegalArgumentException();
        return definitions[id];
    }

    private StructDefinitions(final int id, final ByteBuffer buffer) {
        this.id = id;
        decode(buffer);
    }

    @Getter private final int id;
    @Getter
    @Setter
    private Int2ObjectOpenHashMap<Object> parameters;

    @Override
    public void load() {
        val cache = Game.getCacheMgi();
        val configs = cache.getArchive(ArchiveType.CONFIGS);
        val structs = configs.findGroupByID(GroupType.STRUCT);
        definitions = new StructDefinitions[structs.getHighestFileId()];
        for (int id = 0; id < structs.getHighestFileId(); id++) {
            val file = structs.findFileByID(id);
            if (file == null) {
                continue;
            }
            val buffer = file.getData();
            if (buffer == null) {
                continue;
            }
            if (buffer.remaining() < 1) {
                continue;
            }

            definitions[id] = new StructDefinitions(id, buffer);
        }
    }

    public StructDefinitions clone() throws CloneNotSupportedException {
        return (StructDefinitions) super.clone();
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

    public final Optional<?> getValue(final int id) {
        return Optional.ofNullable(parameters.get(id));
    }

    @Override
    public void decode(final ByteBuffer buffer, int opcode) {
        if (opcode == 249) {
            parameters = buffer.readParameters();
            return;
        }
        throw new IllegalStateException("Opcode: " + opcode);
    }

    @Override
    public ByteBuffer encode() {
        val buffer = new ByteBuffer(1132);
        if (parameters!= null && !parameters.isEmpty()) {
            buffer.writeByte(249);
            buffer.writeParameters(parameters);
        }
        buffer.writeByte(0);
        return buffer;
    }

    @Override
    public void pack() {
        Game.getCacheMgi().getArchive(ArchiveType.CONFIGS).findGroupByID(GroupType.STRUCT).addFile(new File(id, encode()));
    }
}
