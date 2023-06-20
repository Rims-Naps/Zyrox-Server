package com.zenyte.network.update.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;

/**
 * @author Tommeh | 27 jul. 2018 | 20:53:07
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class XorEncoder extends MessageToByteEncoder<ByteBuf>{
	
	@Setter private int key = 0;

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
		while (in.isReadable()) {
			out.writeByte(in.readUnsignedByte() ^ key);
		}
	}

}
