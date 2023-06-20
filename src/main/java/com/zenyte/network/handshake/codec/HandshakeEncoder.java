package com.zenyte.network.handshake.codec;

import java.security.SecureRandom;

import com.zenyte.network.handshake.packet.HandshakePacketOut;
import com.zenyte.network.handshake.packet.inc.HandshakeType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Tommeh | 27 jul. 2018 | 21:46:32
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */ 
public class HandshakeEncoder extends MessageToByteEncoder<HandshakePacketOut> {
	
	private static final SecureRandom RAND = new SecureRandom();
	
	@Override
	protected void encode(final ChannelHandlerContext ctx, final HandshakePacketOut packet, final ByteBuf out) throws Exception {
		if (packet.getType().equals(HandshakeType.GAME_CONNECTION)) {
			out.writeByte(packet.getResponse().getId());
	        out.writeLong(RAND.nextLong());
		} else {
			out.writeByte(packet.getResponse().getId());
		}
	}
}
