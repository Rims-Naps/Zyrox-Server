package mgi.tools.jagcached.cache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Manages the reading and writing for a particular index in the cache.
 */
public class Index {

	private static final int IDX_BLOCK_LEN = 6;
	private static final int HEADER_LEN = 8;
    private static final int EXPANDED_HEADER_LEN = 10;
	private static final int BLOCK_LEN = 512;
    private static final int EXPANDED_BLOCK_LEN = 510;
	private static final int TOTAL_BLOCK_LEN = HEADER_LEN + BLOCK_LEN;
	private static ByteBuffer tempBuffer = ByteBuffer.allocateDirect(TOTAL_BLOCK_LEN);

	private int id;
	private FileChannel indexChannel;
	private FileChannel dataChannel;
	private int maxSize;
	private static final Object lock = new Object();

	/**
	 * Creates a new Index object.
	 * @param id The index of this index.
	 * @param dataChannel The channel of the data file for this index.
	 * @param indexChannel The channel of the index file for this index.
	 * @param maxSize The maximum size of a file in this index.
	 */
	public Index(int id, FileChannel dataChannel,
				 FileChannel indexChannel, int maxSize) {
		this.id = id;
		this.dataChannel = dataChannel;
		this.indexChannel = indexChannel;
		this.maxSize = maxSize;
	}

	/**
	 * Gets the number of groups stored in this index.
	 * @return This index's group count.
	 */
	public int groupCount() {
		try {
			return (int) (indexChannel.size() / IDX_BLOCK_LEN);
		} catch (IOException ex) {
			return 0;
		}
	}

	/**
	 * Reads a group from the index.
	 * @param group The group to read.
	 * @return The group's data, or null if the group was invalid.
	 */
	public mgi.utilities.ByteBuffer get(int group) {
		synchronized (lock) {
			try {
				if (group * IDX_BLOCK_LEN + IDX_BLOCK_LEN > indexChannel.size()) {
					return null;
				}

				tempBuffer.position(0).limit(IDX_BLOCK_LEN);
				indexChannel.read(tempBuffer, group * IDX_BLOCK_LEN);
				tempBuffer.flip();
				int size = getMediumInt(tempBuffer);
				int block = getMediumInt(tempBuffer);

				if (size < 0 || size > maxSize) {
					return null;
				}
				if (block <= 0 || block > dataChannel.size() / TOTAL_BLOCK_LEN) {
					return null;
				}

				mgi.utilities.ByteBuffer groupBuffer = new mgi.utilities.ByteBuffer(size);
				int remaining = size;
				int chunk = 0;
				int blockLen = group <= 0xffff ? BLOCK_LEN : EXPANDED_BLOCK_LEN;
				int headerLen = group <= 0xffff ? HEADER_LEN : EXPANDED_HEADER_LEN;
				while (remaining > 0) {
					if (block == 0) {
						return null;
					}

					int blockSize = remaining > blockLen ? blockLen : remaining;
					tempBuffer.position(0).limit(blockSize + headerLen);
					dataChannel.read(tempBuffer, block * TOTAL_BLOCK_LEN);
					tempBuffer.flip();

					int currentGroup, currentChunk, nextBlock, currentIndexId;

					if (group <= 65535) {
						currentGroup = tempBuffer.getShort() & 0xffff;
						currentChunk = tempBuffer.getShort() & 0xffff;
						nextBlock = getMediumInt(tempBuffer);
						currentIndexId = tempBuffer.get() & 0xff;
					} else {
						currentGroup = tempBuffer.getInt();
						currentChunk = tempBuffer.getShort() & 0xffff;
						nextBlock = getMediumInt(tempBuffer);
						currentIndexId = tempBuffer.get() & 0xff;
					}

					if (group != currentGroup || chunk != currentChunk || id != currentIndexId) {
						return null;
					}
					if (nextBlock < 0 || nextBlock > dataChannel.size() / TOTAL_BLOCK_LEN) {
						return null;
					}

					int rem = tempBuffer.remaining();
					for (int i = 0; i < rem; i++)
						groupBuffer.writeByte(tempBuffer.get());

					remaining -= blockSize;
					block = nextBlock;
					chunk++;
				}

				groupBuffer.setPosition(0);
				return groupBuffer;
			} catch (IOException ex) {
				return null;
			}
		}
	}

	/**
	 * Writes a group to the index.
	 * @param group The group to write.
	 * @param data The group's data.
	 * @param size The size of the group.
	 * @return true if the group was written, false otherwise.
	 */
	public boolean put(int group, mgi.utilities.ByteBuffer data, int size) {
		if (size < 0 || size > maxSize) {
			throw new IllegalArgumentException("Group too big: " + group + " size: " + size);
		}

		boolean success = put(group, ByteBuffer.wrap(data.getBuffer()), size, true);
		if (!success) {
			success = put(group, ByteBuffer.wrap(data.getBuffer()), size, false);
		}

		return success;
	}

	private boolean put(int group, ByteBuffer data, int size, boolean exists) {
		synchronized (lock) {
			try {
				int block;
				if (exists) {
					if (group * IDX_BLOCK_LEN + IDX_BLOCK_LEN > indexChannel.size()) {
						return false;
					}

					tempBuffer.position(0).limit(IDX_BLOCK_LEN);
					indexChannel.read(tempBuffer, group * IDX_BLOCK_LEN);
					tempBuffer.flip().position(3);
					block = getMediumInt(tempBuffer);

					if (block <= 0 || block > dataChannel.size() / TOTAL_BLOCK_LEN) {
						return false;
					}
				} else {
					block = (int) (dataChannel.size() + TOTAL_BLOCK_LEN - 1) / TOTAL_BLOCK_LEN;
					if (block == 0) {
						block = 1;
					}
				}

				tempBuffer.position(0);
				putMediumInt(tempBuffer, size);
				putMediumInt(tempBuffer, block);
				tempBuffer.flip();
				indexChannel.write(tempBuffer, group * IDX_BLOCK_LEN);

				int remaining = size;
				int chunk = 0;
				int blockLen = group <= 0xffff ? BLOCK_LEN : EXPANDED_BLOCK_LEN;
				int headerLen = group <= 0xffff ? HEADER_LEN : EXPANDED_HEADER_LEN;
				while (remaining > 0) {
					int nextBlock = 0;
					if (exists) {
						tempBuffer.position(0).limit(headerLen);
						dataChannel.read(tempBuffer, block * TOTAL_BLOCK_LEN);
						tempBuffer.flip();

						int currentGroup, currentChunk, currentIndexId;
						if (group <= 0xffff) {
							currentGroup = tempBuffer.getShort() & 0xffff;
							currentChunk = tempBuffer.getShort() & 0xffff;
							nextBlock = getMediumInt(tempBuffer);
							currentIndexId = tempBuffer.get() & 0xff;
						} else {
							currentGroup = tempBuffer.getInt();
							currentChunk = tempBuffer.getShort() & 0xffff;
							nextBlock = getMediumInt(tempBuffer);
							currentIndexId = tempBuffer.get() & 0xff;
						}

						if (group != currentGroup || chunk != currentChunk || id != currentIndexId) {
							return false;
						}
						if (nextBlock < 0 || nextBlock > dataChannel.size() / TOTAL_BLOCK_LEN) {
							return false;
						}
					}

					if (nextBlock == 0) {
						exists = false;
						nextBlock = (int) ((dataChannel.size() + TOTAL_BLOCK_LEN - 1) / TOTAL_BLOCK_LEN);
						if (nextBlock == 0) {
							nextBlock = 1;
						}
						if (nextBlock == block) {
							nextBlock++;
						}
					}

					if (remaining <= blockLen) {
						nextBlock = 0;
					}
					tempBuffer.position(0).limit(TOTAL_BLOCK_LEN);
					if (group <= 0xffff) {
						tempBuffer.putShort((short) group);
						tempBuffer.putShort((short) chunk);
						putMediumInt(tempBuffer, nextBlock);
						tempBuffer.put((byte) id);
					} else {
						tempBuffer.putInt(group);
						tempBuffer.putShort((short) chunk);
						putMediumInt(tempBuffer, nextBlock);
						tempBuffer.put((byte) id);
					}

					int blockSize = remaining > blockLen ? blockLen : remaining;
					data.limit(data.position() + blockSize);
					tempBuffer.put(data);
					tempBuffer.flip();

					dataChannel.write(tempBuffer, block * TOTAL_BLOCK_LEN);
					remaining -= blockSize;
					block = nextBlock;
					chunk++;
				}

				return true;
			} catch (IOException ex) {
				return false;
			}
		}
	}

	private static int getMediumInt(ByteBuffer buffer) {
		return ((buffer.get() & 0xff) << 16) | ((buffer.get() & 0xff) << 8) |
				(buffer.get() & 0xff);
	}

	private static void putMediumInt(ByteBuffer buffer, int val) {
		buffer.put((byte) (val >> 16));
		buffer.put((byte) (val >> 8));
		buffer.put((byte) val);
	}
	
	public void setMaxSize(int size) {
		this.maxSize = size;
	}
	
	/**
	 * Close's this store.
	 */
	public void close() {
		synchronized (lock) {
			try {
				this.dataChannel.close();
			} catch (IOException e) {
			}
			try {
				this.indexChannel.close();
			} catch (IOException e) {
			}
		}
	}

}
