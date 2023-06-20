package com.zenyte.network;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Tommeh | 27 jul. 2018 | 21:01:01
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@FunctionalInterface
public interface ProtocolNetworkHandler<P extends PacketIn> {

	void handle(ChannelHandlerContext ctx, P packet);
}