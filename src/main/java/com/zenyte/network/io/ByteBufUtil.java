package com.zenyte.network.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * @author Tommeh | 3 okt. 2018 | 20:11:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ByteBufUtil {
	
	public static ByteBuf encipherRSA(ByteBuf buffer, BigInteger exponent, BigInteger modulus) {
		byte[] bytes = new byte[buffer.readShort()];
		buffer.readBytes(bytes);
		return Unpooled.wrappedBuffer(new BigInteger(bytes).modPow(exponent, modulus).toByteArray());
	}
	
	public static ByteBuf decipherXTEA(ByteBuf buffer, int[] key) {
		byte[] bytes = new byte[buffer.readableBytes()];
		buffer.readBytes(bytes);
		ByteBuf xteaBuffer = Unpooled.wrappedBuffer(bytes);
		decipherXTEA(xteaBuffer, 0, bytes.length, key);
		return xteaBuffer;
	}
	
	private static void decipherXTEA(ByteBuf buffer, int start, int end, int[] key) {
		if (key.length != 4) {
			throw new IllegalArgumentException();
		}
		int numQuads = (end - start) / 8;
		for (int i = 0; i < numQuads; i++) {
			int sum = 0x9E3779B9 * 32;
			int v0 = buffer.getInt(start + i * 8);
			int v1 = buffer.getInt(start + i * 8 + 4);
			for (int j = 0; j < 32; j++) {
				v1 -= (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(sum >>> 11) & 3]);
				sum -= 0x9E3779B9;
				v0 -= (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[sum & 3]);
			}
			buffer.setInt(start + i * 8, v0);
			buffer.setInt(start + i * 8 + 4, v1);
		}
	}

	public static ByteBuf writeString(ByteBuf buf, String string) {
		for(char c : string.toCharArray()) {
			buf.writeByte(c);
		}
		buf.writeByte(0);
		
		return buf;
	}
	
	public static ByteBuffer writeString(ByteBuffer buf, String string) {
		for(char c : string.toCharArray()) {
			buf.put((byte) c);
		}
		buf.put((byte) 0);
		
		return buf;
	}
	
	public static String readString(ByteBuf buf) {
		if (!buf.isReadable())
			throw new IllegalStateException("Buffer is not readable.");
		
		StringBuilder bldr = new StringBuilder();
		byte read;
		while(buf.isReadable() && (read = buf.readByte()) != 0) {
			bldr.append((char) read);
		}
		return bldr.toString();
	}
	
    public static String readJAGString(ByteBuf buf) {
        StringBuilder bldr = new StringBuilder();
        byte b;
        buf.readByte();
        while (buf.isReadable() && (b = buf.readByte()) != 0) {
            bldr.append((char) b);
        }
        return bldr.toString();
    }
	
	public static void writeMedium(ByteBuf buf, int value) {
		buf.writeByte(value >> 16);
		buf.writeByte(value >> 8);
		buf.writeByte(value);
	}
	
	public static void writeMedium(ByteBuffer buf, int value) {
		buf.put((byte) (value >> 16));
		buf.put((byte) (value >> 8));
		buf.put((byte) value);
	}

	public static int readMedium(ByteBuf buffer) {
		return buffer.readByte() << 16 | buffer.readByte() << 8 | buffer.readByte();
	}
}
