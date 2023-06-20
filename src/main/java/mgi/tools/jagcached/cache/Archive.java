package mgi.tools.jagcached.cache;

import lombok.val;
import mgi.tools.jagcached.GroupType;
import mgi.tools.jagcached.Helper;
import mgi.utilities.ByteBuffer;
import mgi.utilities.Whirlpool;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class Archive {

	/**
	 * Underlying cache.
	 */
	private Cache cache;

	/**
	 * Contains ID of the archive.
	 */
	private int id;

	/**
	 * Whether to use automatic version incrementer.
	 */
	private boolean useAutomaticVersionIncrementer = true;

	/**
	 * Contains all groups.
	 */
	private Group[] groups;

	/**
	 * Whether whirlpool hashes are used.
	 */
	private boolean useWhirlpool;

	/**
	 * Whether names are used.
	 */
	private boolean useNames;

	/**
	 * Version of this archive.
	 */
	private int version;

	/**
	 * CRC32 of this archive.
	 */
	private int crc;

	private int highestGroupId;

	/**
	 * 64byte whirlpool digest
	 * of this archive.
	 */
	private byte[] digest;

	/**
	 * Contains packed archive file
	 * which was provided with load() call.
	 */
	private ByteBuffer packed;

	/**
	 * Whether any changes were made to
	 * archive properties such as version or properties.
	 */
	private boolean changed;

	private final Object $lock = new Object();

	public Archive(int id, Cache cache) {
		this.id = id;
		this.cache = cache;
	}

	/**
	 * Tries to load this archive from cache.
	 */
	public void load() {
		if (isLoaded())
			throw new RuntimeException("Already loaded!");
		ByteBuffer buffer = cache.getMasterIndex().get(id);
		if (buffer == null) {
			return;
			//throw new RuntimeException("Missing archive file.");
		}

		ByteBuffer unpacked = Helper.decodeFITContainer(buffer);

		unpacked.setPosition(0);

		int protocol = unpacked.readUnsignedByte();
		if (protocol >= 6)
			version = unpacked.readInt();

		int properties = unpacked.readUnsignedByte();
		useNames     = (properties & 0x1) != 0;
		useWhirlpool = (properties & 0x2) != 0;

		groups = new Group[protocol >= 7 ? unpacked.readSmartInt() : unpacked.readUnsignedShort()];

		int[]    groupIDS        = new int [groups.length];
		int[]    groupNames      = new int [groups.length];
		int[]    groupCRCS       = new int [groups.length];
		byte[][] groupDigests    = new byte[groups.length][64];
		int[]    groupVersions   = new int [groups.length];
		int[][]  groupFilesIDS   = new int [groups.length][];
		int[][]  groupFilesNames = new int [groups.length][];

		int highest = 0;
		for (int offset = 0, i = 0; i < groups.length; i++) {
			groupIDS[i] = offset += (protocol >= 7 ? unpacked.readSmartInt() : unpacked.readUnsignedShort());
			if (groupIDS[i] <= highest) {
				continue;
			}
			highest = groupIDS[i];
		}
		this.highestGroupId = highest == 0 ? 0 : (highest + 1);

		for (int i = 0; i < groups.length; i++)
			groupNames[i] = useNames ? unpacked.readInt() : -1;

		for (int i = 0; i < groups.length; i++)
			groupCRCS[i] = unpacked.readInt();

		if (useWhirlpool)
			for (int i = 0; i < groups.length; i++)
				unpacked.readBytes(groupDigests[i], 0, 64);

		for (int i = 0; i < groups.length; i++)
			groupVersions[i] = unpacked.readInt();

		for (int i = 0; i < groups.length; i++) {
			int filesCount = protocol >= 7 ? unpacked.readSmartInt() : unpacked.readUnsignedShort();
			groupFilesIDS[i] = new int[filesCount];
			groupFilesNames[i] = new int[filesCount];
		}

		for (int i = 0; i < groups.length; i++)
			for (int offset = 0, a = 0; a < groupFilesIDS[i].length; a++)
				groupFilesIDS[i][a] = offset += (protocol >= 7 ? unpacked.readSmartInt() : unpacked.readUnsignedShort());

		for (int i = 0; i < groups.length; i++)
			for (int a = 0; a < groupFilesIDS[i].length; a++)
				groupFilesNames[i][a] = useNames ? unpacked.readInt() : -1;

		for (int i = 0; i < groups.length; i++)
			groups[i] = new Group(groupIDS[i],groupNames[i],groupVersions[i],groupCRCS[i],groupDigests[i],groupFilesIDS[i],groupFilesNames[i]);

		updateHashes(buffer);

		packed = buffer;
	}

	/**
	 * Deletes all groups on this archive.
	 * Resets version to 0.
	 */
	public void reset() {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		changed = true;
		groups = new Group[0];
		version = 0;
	}

	/**
	 * Finishes any changes to this archive.
	 */
	public void finish() {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		if (!needsRepack())
			return;

		for (int i = 0; i < groups.length; i++) {
			if (groups[i].isIndexChanged()) {
				if (useAutomaticVersionIncrementer)
					groups[i].setVersion(groups[i].getVersion() + 1);
				ByteBuffer buffer = groups[i].finish();
				if (!cache.getIndex(id).put(groups[i].getID(), buffer, buffer.getBuffer().length))
					throw new RuntimeException("Couldn't update group:" + groups[i]);
			}
		}

		if (useAutomaticVersionIncrementer)
			version++;

		// sort the group by their id
		Arrays.sort(groups, Comparator.comparingInt(Group::getID));
		for (val group : groups) {
			val files = Arrays.asList(group.getFiles());
			if (files != null) {
				files.removeIf(Objects::isNull);
				files.sort(Comparator.comparingInt(File::getID));
			}
			group.setFiles(files.toArray(new File[files.size()]));
		}

		packed = pack();
		updateHashes(packed);

		if (!cache.getMasterIndex().put(id, packed, packed.getBuffer().length))
			throw new RuntimeException("Couldn't update packed archive.");

		changed = false;
		for (int i = 0; i < groups.length; i++)
			groups[i].markIndexAsNotChanged();


	}

	/**
	 * Packs this archive.
	 */
	private ByteBuffer pack() {

		int protocol = decideProtocol();
		ByteBuffer pack = new ByteBuffer(calculatePackedAllocSize(protocol));

		pack.writeByte(protocol);
		if (protocol >= 6)
			pack.writeInt(version);

		pack.writeByte((useNames ? 0x1 : 0x0) | (useWhirlpool ? 0x2 : 0x0));

		if (protocol >= 7)
			pack.writeSmart32(groups.length);
		else
			pack.writeShort(groups.length);

		for (int delta = 0, i = 0; i < groups.length; i++) {
			if (protocol >= 7)
				pack.writeSmart32(groups[i].getID() - delta);
			else
				pack.writeShort(groups[i].getID() - delta);
			delta = groups[i].getID();
		}

		if (useNames)
			for (int i = 0; i < groups.length; i++)
				pack.writeInt(groups[i].getName());

		for (int i = 0; i < groups.length; i++)
			pack.writeInt(groups[i].getCrc());

		if (useWhirlpool)
			for (int i = 0; i < groups.length; i++)
				pack.writeBytes(groups[i].getDigest(), 0, 64);

		for (int i = 0; i < groups.length; i++)
			pack.writeInt(groups[i].getVersion());

		for (int i = 0; i < groups.length; i++)
			if (protocol >= 7)
				pack.writeSmart32(groups[i].fileCount());
			else
				pack.writeShort(groups[i].fileCount());

		for (int i = 0; i < groups.length; i++) {
			for (int delta = 0, a = 0; a < groups[i].fileCount(); a++) {
				if (protocol >= 7)
					pack.writeSmart32(groups[i].getFiles()[a].getID() - delta);
				else
					pack.writeShort(groups[i].getFiles()[a].getID() - delta);
				delta = groups[i].getFiles()[a].getID();
			}
		}

		if (useNames)
			for (int i = 0; i < groups.length; i++)
				for (int a = 0; a < groups[i].fileCount(); a++)
					pack.writeInt(groups[i].getFiles()[a].getName());

		return Helper.encodeFITContainer(pack, version);
	}

	/**
	 * Calculates new packed archive size.
	 */
	private int calculatePackedAllocSize(int protocol) {
		int size = 2;
		if (protocol >= 6)
			size += 4;

		size += protocol >= 7 ? (groups.length > 0x7FFF ? 4 : 2) : 2;
		for (int delta = 0, i = 0; i < groups.length; i++) {
			if (protocol >= 7 && (groups[i].getID() - delta) > 0x7FFF)
				size += 4;
			else
				size += 2;
			delta = groups[i].getID();
		}

		if (useNames)
			size += groups.length * 4; // names
		size += groups.length * 4; // crcs
		if (useWhirlpool)
			size += groups.length * 64; // whirlpool
		size += groups.length * 4; // versions

		for (int i = 0; i < groups.length; i++)
			size += protocol >= 7 ? (groups[i].fileCount() > 0x7FFF ? 4 : 2) : 2;

		for (int i = 0; i < groups.length; i++) {
			for (int delta = 0, a = 0; a < groups[i].fileCount(); a++) {
				if (protocol >= 7 && (groups[i].getFiles()[a].getID() - delta) > 0x7FFF)
					size += 4;
				else
					size += 2;
				delta = groups[i].getFiles()[a].getID();
			}
		}

		for (int i = 0; i < groups.length; i++)
			for (int a = 0; a < groups[i].fileCount(); a++)
				size += 4;
		return size;
	}

	/**
	 * Decides protocol ID to be used when packing
	 * this archive.
	 */
	private int decideProtocol() {
		if (groups.length > 0xFFFF)
			return 7;
		for (int delta = 0, i = 0; i < groups.length; i++) {
			if ((groups[i].getID() - delta) > 0xFFFF)
				return 7;
			delta = groups[i].getID();
		}

		for (int i = 0; i < groups.length; i++) {
			if (groups[i].fileCount() > 0xFFFF)
				return 7;
			for (int delta = 0, a = 0; a < groups[i].fileCount(); a++) {
				if ((groups[i].getFiles()[a].getID() - delta) > 0xFFFF)
					return 7;
				delta = groups[i].getFiles()[a].getID();
			}
		}

		return version != 0 ? 6 : 5;
	}

	/**
	 * Finds group by id.
	 * Returns null if none found.
	 */
	public Group findGroupByID(int id) {
		return findGroupByID(id,null,true);
	}

	/**
	 * Finds group by id.
	 * Returns null if none found.
	 */
	public Group findGroupByID(final GroupType group) {
		return findGroupByID(group.getId());
	}

	/**
	 * Finds group by id.
	 * Returns null if none found.
	 */
	public Group findGroupByID(int id, int[] xtea, boolean unpack) {
		return findGroupByID(id, xtea, unpack, false);
	}

	/**
	 * Finds group by id.
	 * Returns null if none found.
	 */
	public Group findGroupByID(int id, int[] xtea, boolean unpack, final boolean throwException) {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		for (int i = 0; i < groups.length; i++)
			if (groups[i].getID() == id) {
				Group group = groups[i];
				if (group.isLoaded())
					return group;
				ByteBuffer data = cache.getIndex(this.id).get(id);
				if (data == null) {
					if (throwException) {
						throw new RuntimeException("Missing group:" + id);
					}
					return null;
				}
				group.load(data, xtea, unpack);
				return group;
			}
		return null;
	}

	/**
	 * Finds group by name.
	 * Returns null if none found.
	 */
	public Group findGroupByName(String name) {
		return findGroupByName(name,null);
	}

	/**
	 * Finds group by name.
	 * Returns null if none found.
	 */
	public Group findGroupByName(int name) {
		return findGroupByName(name,null);
	}

	public Group findGroupByName(String name, int[] xtea) {
		return findGroupByName(name, xtea, true);
	}

	/**
	 * Finds group by name.
	 * Returns null if none found.
	 */
	public Group findGroupByName(String name, int[] xtea, boolean unpack) {
		return findGroupByName(Helper.strToI(name), xtea, unpack);
	}

	public Group findGroupByName(int name, int[] xtea) {
		return findGroupByName(name, xtea, true);
	}

	/**
	 * Finds group by name.
	 * Returns null if none found.
	 */
	public Group findGroupByName(int name, int[] xtea, boolean unpack) {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		if (name == -1)
			return null;
		for (int i = 0; i < groups.length; i++)
			if (groups[i].getName() == name) {
				synchronized ($lock) {
					Group group = groups[i];
					if (group.isLoaded())
						return group;
					ByteBuffer data = cache.getIndex(this.id).get(group.getID());
					if (data == null)
						throw new RuntimeException("Missing group:" + group.getID());

					group.load(data, xtea, unpack);
					return group;
				}
			}
		return null;
	}

	/**
	 * Adds new group to this archive.
	 * If there's already a group with same id then
	 * it gets overwritten.
	 */
	public void addGroup(Group group) {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		if (!group.isLoaded())
			throw new RuntimeException("group is not loaded.");
		if (findGroupIndex(group) != -1)
			return;
		if (group.getID() == -1)
			group.setID(getFreeGroupID());
		int index = -1;
		for (int i = 0; i < groups.length; i++)
			if (groups[i].getID() == group.getID()) {
				index = i;
				break;
			}
		if (index == -1) {
			Group[] newGroups = new Group[groups.length + 1];
			System.arraycopy(groups, 0, newGroups, 0, groups.length);
			index = groups.length;
			groups = newGroups;
		}
		groups[index] = group;
		group.markIndexAsChanged(); // cause it needs to be packed to store.
		changed = true;
	}

	/**
	 * Deletes given group.
	 */
	public void deleteGroup(Group group) {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		int index = findGroupIndex(group);
		if (index == -1)
			return;
		Group[] newGroups = new Group[groups.length - 1];
		for (int write = 0, i = 0; i < groups.length; i++)
			if (groups[i] != group)
				newGroups[write++] = groups[i];
		groups = newGroups;
		changed = true;
	}

	/**
	 * Deletes all group in this fs.
	 */
	public void deleteAllGroups() {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		groups = new Group[0];
		changed = true;
	}

	/**
	 * Loads group if it's not yet loaded.
	 */
	public void load(Group group) {
		load(group,null);
	}

	/**
	 * Loads group if it's not yet loaded.
	 */
	public void load(Group group, int[] xtea) {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		int index = findGroupIndex(group);
		if (index == -1)
			return;
		synchronized ($lock) {
			if (group.isLoaded())
				return;
			ByteBuffer data = cache.getIndex(id).get(group.getID());
			if (data == null)
				throw new RuntimeException("Missing group:" + group.getID());
			group.load(data, xtea, true);
		}
	}

	/**
	 * Finishes any pending caches on this archive
	 * and then unloads some buffered files.
	 */
	public void unloadCachedFiles() {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		finish();
		for (int i = 0; i < groups.length; i++)
			if (groups[i].isLoaded())
				groups[i].unload();
	}

	/**
	 * Gets free group ID.
	 */
	public int getFreeGroupID() {
		if (groups.length == 0)
			return 0;
		int highest = -1;
		for (int i = 0; i < groups.length; i++)
			if (groups[i].getID() > highest)
				highest = groups[i].getID();
		return highest + 1;
	}

	/**
	 * Finds group index.
	 */
	private int findGroupIndex(Group group) {
		for (int i = 0; i < groups.length; i++)
			if (groups[i] == group)
				return i;
		return -1;
	}

	public int findGroupIdByName(final String name) {
		return Helper.strToI(name);
	}

	/**
	 * Updates crc32 and whirlpool hash to ones from
	 * given buffer.
	 */
	private void updateHashes(ByteBuffer packed) {
		crc = Helper.crc32(packed,0,packed.getBuffer().length);
		digest = Whirlpool.whirlpool(packed.getBuffer(), 0, packed.getBuffer().length);
	}

	/**
	 * Whether this archive is loaded.
	 */
	public boolean isLoaded() {
		return packed != null;
	}

	/**
	 * Whether archive file needs repack.
	 */
	private boolean needsRepack() {
		if (changed)
			return true;
		for (int i = 0; i < groups.length; i++)
			if (groups[i].isIndexChanged())
				return true;
		return false;
	}

	/**
	 * Gets ID of this archive.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Whether this archive uses names.
	 */
	public boolean usesNames() {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		return useNames;
	}

	/**
	 * Whether this archive uses whirlpool.
	 */
	public boolean usesWhirlpool() {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		return useWhirlpool;
	}

	/**
	 * Sets Whether this archive uses names.
	 */
	public void setUsesNames(boolean uses) {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		if (this.useNames != uses) {
			changed = true;
			this.useNames = uses;
		}
	}

	/**
	 * Sets Whether this archive uses whirlpool.
	 */
	public void setUsesWhirlpool(boolean uses) {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		if (this.useWhirlpool != uses) {
			changed = true;
			this.useWhirlpool = uses;
		}
	}

	/**
	 * Gets version of this archive.
	 */
	public int getVersion() {
		//if (!isLoaded())
		//	throw new RuntimeException("Using nonloaded archive.");
		return version;
	}

	public ByteBuffer getPacked() {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		return packed;
	}

	/**
	 * Sets version of this archive.
	 */
	public void setVersion(int version) {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		if (this.version != version) {
			changed = true;
			this.version = version;
		}
	}

	/**
	 * Gets crc32 of packed version of this archive.
	 */
	public int getCRC32() {
		//if (!isLoaded())
		//	throw new RuntimeException("Using nonloaded archive.");
		return crc;
	}

	/**
	 * Gets whirlpool digest of packed version of this archive.
	 */
	public byte[] getDigest() {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		return digest;
	}

	/**
	 * Whether versions are automatically incremented
	 * each time finish() is called.
	 */
	public boolean usingAutomaticVersionsIncrementer() {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		return useAutomaticVersionIncrementer;
	}

	/**
	 * Sets Whether to use automatic versions incrementer.
	 */
	public void setUseAutomaticVersionsIncrementer(boolean use) {
		if (!isLoaded())
			throw new RuntimeException("Using nonloaded archive.");
		this.useAutomaticVersionIncrementer = use;
	}

	/**
	 * Gets all group.
	 * Returned group array can't be modified
	 * in any way.
	 */
	public Group[] getGroups() {
		return groups;
	}

	/**
	 * Gets count of groups this archive has.
	 */
	public int groupCount() {
		return groups.length;
	}

	public int getHighestGroupId() {
		return highestGroupId;
	}

}
