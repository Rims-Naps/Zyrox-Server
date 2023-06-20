package com.zenyte.network.world;

import com.zenyte.Constants;
import com.zenyte.game.world.info.WorldType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.val;

import java.net.InetSocketAddress;

/**
 * @author Kris | 30. juuli 2018 : 02:02:19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class WorldConnector {
	
	private final Bootstrap bootstrap = new Bootstrap();

	public WorldConnector() {
		bootstrap.remoteAddress(new InetSocketAddress("127.0.0.1", 43593))
				.channel(NioSocketChannel.class).group(new NioEventLoopGroup(8)).handler(new ConnectionInitializer());
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.connect();
	}

	private static final class ConnectionInitializer extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(final SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new ConnectionHandler());
			System.err.println("Initializing connection to mother world.");
		}

	}

	private static final class ConnectionHandler extends SimpleChannelInboundHandler<ByteBuf> {

		@Override
		public void channelActive(final ChannelHandlerContext ctx) {
			val channel = ctx.channel();
			val packet = ctx.alloc().buffer();
			packet.writeByte(/*ConnectionType.WORLD_CONNECTION_AUTH.getId()*/0);
			val key = WorldType.WORLD_AUTHENTICATION_KEY;
			packet.writeByte(Constants.WORLD_PROFILE.getNumber());
			packet.writeBytes(key.getBytes());
			channel.writeAndFlush(packet);
			System.err.println("Successfully connected to mother world. Authenticating.");
		}

		@Override
		protected void channelRead0(final ChannelHandlerContext ctx, final ByteBuf msg) throws Exception {
			System.err.println("Received message: " + msg.readableBytes());
			/*val type = msg.readByte();
			if (type == 0) {
				final boolean success = msg.readByte() == 1;
				System.err.println("Received pulse back: " + success);
			}*/
		}

	}

}
