package com.zenyte.network.game;

import com.zenyte.network.NetworkBootstrap;
import com.zenyte.network.game.packet.GamePacketIn;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;

/**
 * @author Tommeh | 28 jul. 2018 | 12:55:16
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GameHandler extends SimpleChannelInboundHandler<GamePacketIn> {

    @Override
	protected void channelRead0(ChannelHandlerContext ctx, GamePacketIn msg) throws Exception {
		//ctx.channel().attr(NetworkBootstrap.SESSION).get().getGamePacketInQueue().add(msg);
        ctx.channel().attr(NetworkBootstrap.SESSION).get().decode(msg);
	}
	
	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
		if (cause instanceof IOException) {
			return;
		}
		cause.printStackTrace();
	}

}
