package mgi.types.config;

import com.zenyte.Game;
import mgi.tools.jagcached.cache.File;
import mgi.types.Definitions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.utilities.ByteBuffer;

/**
 * @author Kris | 13. march 2018 : 1:57.09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
public final class InventoryDefinitions implements Definitions {

	public static InventoryDefinitions[] definitions;

	@Override
	public void load() {
		val cache = Game.getCacheMgi();
		val configs = cache.getArchive(ArchiveType.CONFIGS);
		val invs = configs.findGroupByID(GroupType.INV);
		definitions = new InventoryDefinitions[invs.getHighestFileId()];
		for (int id = 0; id < invs.getHighestFileId(); id++) {
			val file = invs.findFileByID(id);
			if (file == null) {
				continue;
			}
			val buffer = file.getData();
			if (buffer == null) {
				continue;
			}
			definitions[id] = new InventoryDefinitions(id, buffer);
		}
	}

	@Getter
	private final int id;
	@Getter
	private int size;

	private InventoryDefinitions(final int id, final ByteBuffer buffer) {
		this.id = id;
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
		if (opcode == 2) {
			size = buffer.readUnsignedShort();
		}
	}

	@Override
	public void pack() {
		val archive = Game.getCacheMgi().getArchive(ArchiveType.CONFIGS);
		val inventories = archive.findGroupByID(GroupType.INV);
		inventories.addFile(new File(id, encode()));
	}

	public static final InventoryDefinitions get(final int id) {
		if (id < 0 || id >= definitions.length) {
			return null;
		}
		return definitions[id];
	}

	@Override
	public ByteBuffer encode() {
		val buffer = new ByteBuffer(4);
		buffer.writeByte(2);
		buffer.writeShort(size);
		buffer.writeByte(0);
		return buffer;
	}

}
