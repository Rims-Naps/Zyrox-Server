package mgi.types.config.identitykit;

import com.zenyte.Game;
import mgi.types.Definitions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import mgi.tools.jagcached.ArchiveType;
import mgi.tools.jagcached.GroupType;
import mgi.utilities.ByteBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kris | 27. veebr 2018 : 2:10.50
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 *      profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 *      profile</a>}
 */
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
public final class IdentityKitDefinitions implements Definitions {

	public static IdentityKitDefinitions[] DEFINITIONS;

	private static final List<Integer> HAIRSTYLES = new ArrayList<>(25);
	private static final List<Integer> BEARDSTYLES = new ArrayList<>(16);
	private static final List<Integer> BODYSTYLES = new ArrayList<>(15);
	private static final List<Integer> ARMSTYLES = new ArrayList<>(13);
	private static final List<Integer> LEGSSTYLES = new ArrayList<>(12);

	@Override
	public void load() {
		val cache = Game.getCacheMgi();
		val configs = cache.getArchive(ArchiveType.CONFIGS);
		val identityKits = configs.findGroupByID(GroupType.IDENTKIT);
		DEFINITIONS = new IdentityKitDefinitions[identityKits.getHighestFileId()];
		for (int id = 0; id < identityKits.getHighestFileId(); id++) {
			val file = identityKits.findFileByID(id);
			if (file == null) {
				continue;
			}
			val buffer = file.getData();
			if (buffer == null) {
				continue;
			}
			DEFINITIONS[id] = new IdentityKitDefinitions(id, buffer);
		}
	}

	@Getter
	private final int id;
	@Getter
	private int[] headModels;
	@Getter
	private int[] modelIds;
	@Getter
	private int bodyPartId;
	@Getter
	private boolean selectable;
	@Getter
	private short[] originalColours, originalTextures, replacementColours, replacementTextures;

	private IdentityKitDefinitions(final int id, final ByteBuffer buffer) {
		this.id = id;
		setDefaults();
		decode(buffer);
		if (selectable) {
			val part = bodyPartId;
			if (part == 0) {
				HAIRSTYLES.add(id);
			} else if (part == 1) {
				BEARDSTYLES.add(id);
			} else if (part == 2) {
				BODYSTYLES.add(id);
			} else if (part == 3) {
				ARMSTYLES.add(id);
			} else if (part == 5) {
				LEGSSTYLES.add(id);
			}
		}
	}

	private void setDefaults() {
		bodyPartId = -1;
		headModels = new int[] { -1, -1, -1, -1, -1 };
		selectable = true;
	}

	public static final IdentityKitDefinitions get(final int id) {
		if (id < 0 || id >= DEFINITIONS.length) {
			return null;
		}
		return DEFINITIONS[id];
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
			bodyPartId = buffer.readUnsignedByte();
			return;
		case 2:
			modelIds = new int[buffer.readUnsignedByte()];
			for (int i = 0; i < modelIds.length; i++) {
				modelIds[i] = buffer.readUnsignedShort();
			}
			return;
		case 3:
			selectable = false;
			return;
		case 40: {
			val length = buffer.readUnsignedByte();
			originalColours = new short[length];
			replacementColours = new short[length];
			for (int i = 0; i < length; i++) {
				originalColours[i] = (short) buffer.readUnsignedShort();
				replacementColours[i] = (short) buffer.readUnsignedShort();
			}
			return;
		}
		case 41: {
			val length = buffer.readUnsignedByte();
			originalTextures = new short[length];
			replacementTextures = new short[length];
			for (int i = 0; i < length; i++) {
				originalTextures[i] = (short) buffer.readUnsignedShort();
				replacementTextures[i] = (short) buffer.readUnsignedShort();
			}
			return;
		}
		case 60:
		case 61:
		case 62:
		case 63:
		case 64:
			headModels[opcode - 60] = buffer.readUnsignedShort();
			return;
		}
	}

	public static final int getHairstyle(final int index) {
		if (index < 0 || index >= HAIRSTYLES.size()) {
			return 0;
		}
		return HAIRSTYLES.get(index);
	}

	public static final int getBeardstyle(final int index) {
		if (index < 0 || index >= BEARDSTYLES.size()) {
			return 0;
		}
		return BEARDSTYLES.get(index);
	}

	public static final int getBodystyle(final int index) {
		if (index < 0 || index >= BODYSTYLES.size()) {
			return 0;
		}
		return BODYSTYLES.get(index);
	}

	public static final int getLegsstyle(final int index) {
		if (index < 0 || index >= LEGSSTYLES.size()) {
			return 0;
		}
		return LEGSSTYLES.get(index);
	}

	public static final int getArmstyle(final int index) {
		if (index < 0 || index >= ARMSTYLES.size()) {
			return 0;
		}
		return ARMSTYLES.get(index);
	}
}
