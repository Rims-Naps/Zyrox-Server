package mgi.types.config;

import com.zenyte.Game;
import mgi.types.Definitions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.*;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.cache.File;
import mgi.utilities.ByteBuffer;

import java.util.OptionalInt;

/**
 * @author Kris | 6. apr 2018 : 21:40.11
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@ToString
public final class VarbitDefinitions implements Definitions {

	public static VarbitDefinitions[] definitions;

	private static final Int2IntMap varbit2varpmap = new Int2IntOpenHashMap();

	@Override
	public void load() {
		val cache = Game.getCacheMgi();
		val configs = cache.getArchive(ArchiveType.CONFIGS);
		val varbits = configs.findGroupByID(GroupType.VARBIT);
		definitions = new VarbitDefinitions[varbits.getHighestFileId()];
		for (int id = 0; id < varbits.getHighestFileId(); id++) {
			val file = varbits.findFileByID(id);
			if (file == null) {
				continue;
			}
			val buffer = file.getData();
			if (buffer == null) {
				continue;
			}
			definitions[id] = new VarbitDefinitions(id, buffer);
			varbit2varpmap.put(id, definitions[id].getBaseVar());
		}
	}

	@Getter
	private final int id;
	@Getter @Setter
	private int baseVar, startBit, endBit;

	public VarbitDefinitions(final int baseVar, final ByteBuffer buffer) {
		this.id = baseVar;
		decode(buffer);
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
		if (opcode == 1) {
			baseVar = buffer.readUnsignedShort();
			startBit = buffer.readUnsignedByte();
			endBit = buffer.readUnsignedByte();
		}
	}

	@Override
	public void pack() {
		val archive = Game.getCacheMgi().getArchive(ArchiveType.CONFIGS);
		val varbits = archive.findGroupByID(GroupType.VARBIT);
		varbits.addFile(new File(id, encode()));
		//Game.getLibrary().getIndex(ArchiveType.CONFIGS.getId()).getArchive(GroupType.VARBIT.getId()).addFile(id, encode().array());
	}

	public static final VarbitDefinitions get(final int id) {
		if (id < 0 || id >= definitions.length) {
			return null;
		}

		return definitions[id];
	}

	public static final OptionalInt findVarp(final int varbit) {
	    val varpId = varbit2varpmap.getOrDefault(varbit, -1);
	    return varpId == -1 ? OptionalInt.empty() : OptionalInt.of(varpId);
    }

	@Override
	public ByteBuffer encode() {
		val buffer = new ByteBuffer(6);
		buffer.writeByte(1);
		buffer.writeShort(baseVar);
		buffer.writeByte(startBit);
		buffer.writeByte(endBit);
		buffer.writeByte(0);
		return buffer;
	}
}
