package com.zenyte.network.game.codec;

import com.zenyte.Constants;
import com.zenyte.game.constants.ClientProt;
import com.zenyte.network.NetworkBootstrap;
import com.zenyte.network.game.packet.DecodeState;
import com.zenyte.network.game.packet.GamePacketIn;
import com.zenyte.network.game.packet.PacketType;
import com.zenyte.network.io.RSBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.val;

import java.util.List;

/**
 * @author Tommeh | 28 jul. 2018 | 12:41:40 | @author Kris | 23. sept 2018 : 07:29:55
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>
 */
public class GameDecoder extends ByteToMessageDecoder {

	/**
	 * Current state of the packet decoding.
	 */
	private DecodeState state = DecodeState.READ_OPCODE;

	/**
	 * Current type of the packet.
	 */
	private PacketType type;

	/**
	 * Current opcode of the packet.
	 */
	private int opcode;

	/**
	 * Current size of the packet.
	 */
	private int size;

	@Override
	protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {

		/** Start off by reading the next opcode. */
		if (state.equals(DecodeState.READ_OPCODE)) {

			/** Return the code if no bytes are available in the buffer. */
			if (!in.isReadable()) {
				return;
			}

			/** The ISAAC cipher used to alter opcodes. */
			val decryptor = ctx.channel().attr(NetworkBootstrap.SESSION).get().getISAACCipherPair().getDecodingRandom();

			/** The next opcode, an unsigned short. Altered by ISAAC cipher. */
			opcode = (in.readUnsignedByte() - decryptor.nextInt()) & 0xFF;

			/**
			 * Try to get the size for this packet. If the packet isn't defined, we drop the connection because it is impossible to recover
			 * the buffer due to lack of information on the size of this packet, which means the next opcode will be read incorrectly.
			 */
			try {
				size = ClientProt.getSize(opcode);
			} catch (final IllegalStateException e) {
				e.printStackTrace();
				ctx.channel().close();
				return;
			}

			/** The type of the packet. */
			type = size == -2 ? PacketType.VAR_SHORT : size == -1 ? PacketType.VAR_BYTE : PacketType.FIXED;

			/**
			 * The next stage of the packet decoding - if the packet is variable sized, we read its size, otherwise we skip the phase and
			 * continue off by reading the payload.
			 */
			state = type != PacketType.FIXED ? DecodeState.READ_SIZE : DecodeState.READ_PAYLOAD;
		}

		/** Continue by reading the packet's size if it is variable sized. */
		if (state.equals(DecodeState.READ_SIZE)) {

			/** Return the code and wait if there isn't enough bytes in the buffer to be able to read the size of the packet. */
			if (in.readableBytes() < (type == PacketType.VAR_BYTE ? Byte.BYTES : Short.BYTES)) {
				return;
			}

			/** Read the size of this packet. */
			size = type == PacketType.VAR_BYTE ? in.readUnsignedByte() : in.readUnsignedShort();
			if (size > Constants.MAX_CLIENT_BUFFER_SIZE) {
			    System.err.println("Maximum buffer size exceeded. Size: " + size + ", connection: " + ctx.channel().remoteAddress());
                ctx.channel().close();
                return;
            }

			/** Set the stage to read the payload of the packet. */
			state = DecodeState.READ_PAYLOAD;
		}

		/** Continue off by reading the packet's payload. */
		if (state.equals(DecodeState.READ_PAYLOAD)) {

			/**
			 * If the buffer's size isn't large enough to be able to read the full packet, we return and wait until all bytes are received.
			 */
			if (in.readableBytes() < size) {
				return;
			}

			/** Reset the state back to read the next opcode in the buffer. */
			state = DecodeState.READ_OPCODE;

			/** Create a new buffer and transfers bytes from this buffer's stream to the new buffer. */
			val buffer = new RSBuffer(in.readBytes(size));
			/** Adds a new incoming packet to the received messages list. */
			out.add(new GamePacketIn(opcode, buffer));
		}
	}

}