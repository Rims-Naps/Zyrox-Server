package com.zenyte.network.io;

import lombok.Getter;
import lombok.val;

/**
 * @author Kris | 30/12/2018 01:18
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BitBuffer {

    private static final int MAX_BITS = 32;
    private static final int[] BIT_MASKS = new int[MAX_BITS];

    static {
        for(int i = 0; i < BIT_MASKS.length; i++) {
            BIT_MASKS[i] = (1 << i) - 1;
        }
    }

    @Getter private int writerIndex, readerIndex;
    private final int maximumCapacity;
    @Getter private byte[] bytes;

    public BitBuffer(final int initialCapacity, final int maximumCapacity) {
        this.bytes = new byte[initialCapacity];
        this.maximumCapacity = maximumCapacity;
    }

    public void write(int numBits, final int value) {
        int bytePos = writerIndex >> 3;
        int bitOffset = 8 - (writerIndex & 7);
        val requiredLength = (writerIndex += numBits) >> 3;
        if (requiredLength >= bytes.length) {
            val temp = bytes;
            val length = Math.min(temp.length << 1, maximumCapacity);
            if (length <= requiredLength) {
                throw new IllegalArgumentException("Bitbuffer capacity exceeded: " + requiredLength + ", " + maximumCapacity);
            }
            bytes = new byte[length];
            System.arraycopy(temp, 0, bytes, 0, temp.length);
        }
        for (; numBits > bitOffset; bitOffset = 8) {
            int tmp =getByte(bytePos);
            tmp &= ~BIT_MASKS[bitOffset];
            tmp |= (value >> (numBits - bitOffset)) & BIT_MASKS[bitOffset];
            setByte(bytePos++, tmp);
            numBits -= bitOffset;
        }
        if (numBits == bitOffset) {
            int tmp =getByte(bytePos);
            tmp &= ~BIT_MASKS[bitOffset];
            tmp |= value & BIT_MASKS[bitOffset];
            setByte(bytePos, tmp);
        } else {
            int tmp =getByte(bytePos);
            tmp &= ~(BIT_MASKS[numBits] << (bitOffset - numBits));
            tmp |= (value & BIT_MASKS[numBits]) << (bitOffset - numBits);
            setByte(bytePos, tmp);
        }
    }

    public int read(int numBits) {
        int bytePos = this.readerIndex >> 3;
        int bitOffset = 8 - (this.readerIndex & 7);
        int value = 0;

        for(this.readerIndex += numBits; numBits > bitOffset; bitOffset = 8) {
            value += (getByte(bytePos++) & BIT_MASKS[bitOffset]) << numBits - bitOffset;
            numBits -= bitOffset;
        }

        if(bitOffset == numBits) {
            value += getByte(bytePos) & BIT_MASKS[bitOffset];
        } else {
            value += getByte(bytePos) >> bitOffset - numBits & BIT_MASKS[bitOffset];
        }

        return value;
    }

    public void setBits(final int startIndex, int numBits, int value) {
        int bytePos = startIndex >> 3;
        int bitOffset = 8 - (startIndex & 7);
        val requiredLength = (startIndex + numBits) >> 3;
        if (startIndex + numBits > writerIndex) {
            this.writerIndex = startIndex + numBits;
        }
        if (requiredLength >= bytes.length) {
            val temp = bytes;
            val length = Math.min(temp.length << 1, maximumCapacity);
            if (length <= requiredLength) {
                throw new IllegalArgumentException("Bitbuffer capacity exceeded: " + requiredLength + ", " + maximumCapacity);
            }
            bytes = new byte[length];
            System.arraycopy(temp, 0, bytes, 0, temp.length);
        }
        for (; numBits > bitOffset; bitOffset = 8) {
            int tmp =getByte(bytePos);
            tmp &= ~BIT_MASKS[bitOffset];
            tmp |= (value >> (numBits - bitOffset)) & BIT_MASKS[bitOffset];
            setByte(bytePos++, tmp);
            numBits -= bitOffset;
        }
        if (numBits == bitOffset) {
            int tmp =getByte(bytePos);
            tmp &= ~BIT_MASKS[bitOffset];
            tmp |= value & BIT_MASKS[bitOffset];
            setByte(bytePos, tmp);
        } else {
            int tmp =getByte(bytePos);
            tmp &= ~(BIT_MASKS[numBits] << (bitOffset - numBits));
            tmp |= (value & BIT_MASKS[numBits]) << (bitOffset - numBits);
            setByte(bytePos, tmp);
        }
    }

    public void reset() {
        readerIndex = writerIndex = 0;
    }

    private void setByte(final int position, final int value) {
        bytes[position] = (byte) value;
    }

    private byte getByte(final int position) {
        return bytes[position];
    }

}
