package com.zenyte.network.io;

import com.zenyte.Constants;
import com.zenyte.game.packet.ServerProt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import lombok.val;

@SuppressWarnings("unused")
public class RSBuffer extends UnpooledHeapByteBuf {
	
    public RSBuffer() {
		super(UnpooledByteBufAllocator.DEFAULT, 16, 40000);
	}

	public RSBuffer(final int initialCapacity) {
		super(UnpooledByteBufAllocator.DEFAULT, initialCapacity, Constants.MAX_SERVER_BUFFER_SIZE);
	}

    public RSBuffer(final int initialCapacity, final int maximumCapacity) {
        super(UnpooledByteBufAllocator.DEFAULT, initialCapacity, maximumCapacity);
    }

    public RSBuffer(final ServerProt prot) {
        super(UnpooledByteBufAllocator.DEFAULT, prot.getInitialSize(), prot.getCapacity());
    }

	public RSBuffer(final ByteBuf buf) {
		super(UnpooledByteBufAllocator.DEFAULT, buf.readableBytes(), buf.maxCapacity());
		val length = buf.writerIndex();
		for (int index = buf.readerIndex(); index < length; index++) {
			writeByte(buf.getByte(index));
		}
		buf.release();
	}

	public void writeBytes128(final RSBuffer buffer) {
	    val length = buffer.writerIndex();
		for (int i = buffer.readerIndex(); i < length; i++) {
			writeByte128(buffer.getByte(i));
		}
    }

	public void writeBytes128Reverse(final RSBuffer buffer) {
	    val offset = buffer.readerIndex();
		for (int i = buffer.writerIndex() - 1; i >= offset; i--) {
			writeByte128(buffer.getByte(i));
		}

	}

	public void writeBytes128Reverse(final byte[] payload) {
		for (int index = payload.length - 1; index >= 0; index--) {
			writeByte128(payload[index]);
		}

	}

	public void writeBytesReverse(final byte[] payload) {
		for (int index = payload.length - 1; index >= 0; index--) {
			writeByte(payload[index]);
		}

	}

	public void writeBytesReverse(final byte[] src, final int srcIndex, final int length) {
		for (int index = length - 1; index >= srcIndex; index--) {
			writeByte(src[index]);
		}

	}

	public void writeBytes128Reverse(final byte[] src, final int srcIndex, final int length) {
		for (int index = length - 1; index >= srcIndex; index--) {
			writeByte128(src[index]);
		}

	}

	public void writeBytesReverse(final RSBuffer buffer) {
		for (int index = buffer.array().length - 1; index >= 0; index--) {
			writeByte(buffer.array()[index]);
		}

	}

	public void writeBytes128(final byte[] buffer) {
        for (byte b : buffer) {
            writeByte128(b);
        }

	}

	public void writeVersionedString(final String s) {
		writeVersionedString(s, (byte) 0);

	}

	public void writeVersionedString(final String string, final byte version) {
		writeByte(version);
		writeString(string);

	}

	public void writeString(final String string) {
		for (final char c : string.toCharArray()) {
			writeByte(c);
		}
		writeByte(0);

	}

	public void writeByte128(final int i) {
		writeByte(i + 128);

	}

	public void writeByteC(final int i) {
		writeByte(-i);

	}

	public void write128Byte(final int i) {
		writeByte(128 - i);

	}

	public void writeShortLE128(final int i) {
		writeByte(i + 128);
		writeByte(i >> 8);

	}

	public void writeShort128(final int i) {
		writeByte(i >> 8);
		writeByte(i + 128);

	}

	public void writeSmart(final int i) {
    	if (i < 0 || i > Short.MAX_VALUE) {
    		throw new RuntimeException("A smart can one be within the boundaries of a signed short.");
		}
		if (i >= 0x80) {
			writeShort(i + 0x8000);
		} else {
			writeByte(i);
		}

	}

	public void writeBigSmart(final int i) {
		if (i >= Short.MAX_VALUE) {
			writeInt(i - Integer.MAX_VALUE - 1);
		} else {
			writeShort(i >= 0 ? i : 32767);
		}

	}

	public void write24BitInteger(final int i) {
		writeByte(i >> 16);
		writeByte(i >> 8);
		writeByte(i);

	}

	public void write24BitIntegerV2(final int i) {
		writeByte(i >> 16);
		writeByte(i);
		writeByte(i >> 8);

	}

	public void write24BitIntegerV3(final int i) {
		writeByte(i);
		writeByte(i >> 8);
		writeByte(i >> 16);

	}

	public void writeIntV1(final int i) {
		writeByte(i >> 8);
		writeByte(i);
		writeByte(i >> 24);
		writeByte(i >> 16);

	}

	public void writeIntV2(final int i) {
		writeByte(i >> 16);
		writeByte(i >> 24);
		writeByte(i);
		writeByte(i >> 8);

	}

	public void write5ByteInteger(final long l) {
		writeByte((int) (l >> 32));
		writeByte((int) (l >> 24));
		writeByte((int) (l >> 16));
		writeByte((int) (l >> 8));
		writeByte((int) l);

	}

	public void writeDynamic(int bytes, final long l) {
		bytes--;
		if (bytes < 0 || bytes > 7) {
			throw new IllegalArgumentException();
		}
		for (int shift = 8 * bytes; shift >= 0; shift -= 8) {
			writeByte((int) (l >> shift));
		}

	}

	public void writeUnsignedSmart(final int value) {
		if (value < 64 && value >= -64) {
			writeByte(value + 64);

		}
		if (value < 16384 && value >= -16384) {
			writeShort(value + 49152);

		} else {
			System.out.println("Error psmart out of range: " + value);

		}
	}

	public String readString() {
		final StringBuilder bldr = new StringBuilder();
		byte b;
		while (isReadable() && (b = readByte()) != 0) {
			bldr.append((char) b);
		}
		return bldr.toString();
	}

	public String readVersionedString() { // aka JAG string as you called it
		return readVersionedString((byte) 0);
	}

	public String readVersionedString(final byte versionNumber) {
		if (readByte() != versionNumber) {
			throw new IllegalStateException("Bad string version number!");
		}
		return readString();
	}

	  /**
     * Reads a type C byte.
     *
     * @return A type C byte.
     */
    public byte readByteC() {
        return (byte) (-readByte());
    }

    /**
     * Gets a type S byte.
     *
     * @return A type S byte.
     */
    public byte read128Byte() {
        return (byte) (128 - readByte());
    }

    /**
     * Reads a little-endian type A short.
     *
     * @return A little-endian type A short.
     */
    public short readShortLE128() {
        int i = (readByte() - 128 & 0xFF) | ((readByte() & 0xFF) << 8);
        if (i > 32767) {
			i -= 0x10000;
		}
        return (short) i;
    }

    /**
     * Reads a V1 integer.
     *
     * @return A V1 integer.
     */
    public int readIntV1() {
        return ((readByte() & 0xff) << 8) | ((readByte() & 0xff)) | ((readByte() & 0xff) << 24) | ((readByte() & 0xff) << 16);
    }

    /**
     * Reads a V2 integer.
     *
     * @return A V2 integer.
     */
    public int readIntV2() {
        return ((readByte() & 0xff) << 16) | ((readByte() & 0xff) << 24) | ((readByte() & 0xff)) | ((readByte() & 0xff) << 8);
    }

    /**
     * Reads a 24-bit integer.
     *
     * @return A 24-bit integer.
     */
	public int read24BitInt() {
		return (readByte() << 16) + (readByte() << 8) + (readByte());
	}

    /**
     * Reads a little-endian integer.
     *
     * @return A little-endian integer.
     */
    @Override
	public int readIntLE() {
        return (readByte() & 0xff) | ((readByte() & 0xff) << 8) | ((readByte() & 0xff) << 16) | ((readByte() & 0xff) << 24);
    }

    /**
     * Gets a 3-byte integer.
     *
     * @return The 3-byte integer.
     */
    public int getTriByte() {
        return ((readByte() << 16) & 0xFF) | ((readByte() << 8) & 0xFF) | (readByte() & 0xFF);
    }

    /**
     * Reads a type A byte.
     *
     * @return A type A byte.
     */
    public byte readByte128() {
        return (byte) (readByte() - 128);
    }

    /**
     * Reads a type A short.
     *
     * @return A type A short.
     */
    public short readShort128() {
        int i = ((readByte() & 0xFF) << 8) | (readByte() - 128 & 0xFF);
        if (i > 32767) {
			i -= 0x10000;
		}
        return (short) i;
    }

    /**
     * Reads a series of bytes in reverse.
     *
     * @param is     The target byte array.
     * @param offset The offset.
     * @param length The length.
     */
    public void getReverse(final byte[] is, final int offset, final int length) {
        for (int i = (offset + length - 1); i >= offset; i--) {
            is[i] = readByte();
        }
    }

    /**
     * Reads a series of type A bytes in reverse.
     *
     * @param is     The target byte array.
     * @param offset The offset.
     * @param length The length.
     */
    public void getReverseA(final byte[] is, final int offset, final int length) {
        for (int i = (offset + length - 1); i >= offset; i--) {
            is[i] = readByte128();
        }
    }

    /**
     * Reads a series of bytes.
     *
     * @param is     The target byte array.
     * @param offset The offset.
     * @param length The length.
     */
    public void get(final byte[] is, final int offset, final int length) {
        for (int i = 0; i < length; i++) {
            is[offset + i] = readByte();
        }
    }

    /**
     * Gets a smart.
     *
     * @return The smart.
     */
    public int getSmart() {
        final int peek = getByte(readerIndex()) & 0xFF;
        if (peek < 128) {
            return readUnsignedByte();
        } else {
            return readUnsignedShort() - 0x8000;
        }
    }

    /**
     * Gets a signed smart.
     *
     * @return The signed smart.
     */
    public int getSignedSmart() {
    	final int peek = getByte(readerIndex()) & 0xFF;
        if (peek < 128) {
            return ((readByte() & 0xFF) - 64);
        } else {
            return ((readShort() & 0xFFFF) - 49152);
        }
    }

    public void skip(final int amount) {
        this.skipBytes(amount);
    }

    public void writeBits(final BitBuffer buffer) {
        val bytes = buffer.getBytes();
        val writer = (buffer.getWriterIndex() + 7) >> 3;
        for (int i = buffer.getReaderIndex() >> 3; i < writer; i++) {
            writeByte(bytes[i]);
        }
        buffer.reset();
    }
	
}
