package mgi.types.config;

import com.zenyte.Game;
import mgi.types.Definitions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.utilities.ByteBuffer;

/**
 * @author Kris | 6. apr 2018 : 19:59.19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
public final class ParamDefinitions implements Definitions {

	public static ParamDefinitions[] definitions;

	@Override
	public void load() {
		val cache = Game.getCacheMgi();
		val configs = cache.getArchive(ArchiveType.CONFIGS);
		val params = configs.findGroupByID(GroupType.PARAMS);
		definitions = new ParamDefinitions[params.getHighestFileId()];
		for (int id = 0; id < params.getHighestFileId(); id++) {
			val file = params.findFileByID(id);
			if (file == null) {
				continue;
			}
			val buffer = file.getData();
			if (buffer == null) {
				continue;
			}
			definitions[id] = new ParamDefinitions(id, buffer);
		}
	}

	@Getter
	private final int id;
	@Getter
	private char stackType;
	@Getter
	private int defaultInt;
	@Getter
	private String defaultString;
	@Getter
	private boolean autoDisable = true;

	private ParamDefinitions(final int id, final ByteBuffer buffer) {
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
		switch (opcode) {
		case 1:
			stackType = buffer.readJagexChar();
			return;
		case 2:
			defaultInt = buffer.readInt();
			return;
		case 4:
			autoDisable = false;
			return;
		case 5:
			defaultString = buffer.readString();
			return;
		}
	}

	public static final ParamDefinitions get(final int id) {
		if (id < 0 || id >= definitions.length) {
			return null;
		}

		return definitions[id];
	}
}
