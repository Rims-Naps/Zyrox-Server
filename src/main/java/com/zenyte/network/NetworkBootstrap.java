package com.zenyte.network;

import com.zenyte.game.packet.Session;
import com.zenyte.network.handshake.HandshakeHandler;
import com.zenyte.network.handshake.codec.HandshakeDecoder;
import com.zenyte.network.handshake.codec.HandshakeEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.concurrent.TimeUnit;

/**
 * @author Tommeh | 27 jul. 2018 | 22:35:25
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Slf4j
public class NetworkBootstrap {
	
	private static final ServerBootstrap bootstrap;
	private static final EventLoopGroup boss;
	private static final EventLoopGroup worker;
	public static final int IDLE_TIMEOUT_MS = 30000;
	public static final AttributeKey<Session> SESSION = AttributeKey.valueOf(Session.class.getSimpleName());

	public static EventLoopGroup eventLoopGroup(int nThreads) {
		return Epoll.isAvailable() ? new EpollEventLoopGroup(nThreads) : KQueue.isAvailable() ? new KQueueEventLoopGroup(nThreads) : new NioEventLoopGroup(nThreads);
	}

	public static Class<? extends ServerSocketChannel> serverSocketChannel(final EventLoopGroup group) {
		return group instanceof EpollEventLoopGroup ? EpollServerSocketChannel.class : group instanceof KQueueEventLoopGroup ? KQueueServerSocketChannel.class : NioServerSocketChannel.class;
	}

	public static Class<? extends SocketChannel> socketChannel(final EventLoopGroup group) {
		return group instanceof EpollEventLoopGroup ? EpollSocketChannel.class : group instanceof KQueueEventLoopGroup ? KQueueSocketChannel.class : NioSocketChannel.class;
	}

	static {
		bootstrap = new ServerBootstrap();
		boss = eventLoopGroup(1);
		worker = eventLoopGroup(0);
		bootstrap.group(boss, worker);
		bootstrap.channel(serverSocketChannel(boss));
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(2 << 18, 2 << 20));
		bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
			@Override
			protected void initChannel(NioSocketChannel ch) {
				val pipeline = ch.pipeline();
				pipeline.addLast(IdleStateHandler.class.getSimpleName(), new IdleStateHandler(true, 0, 0, IDLE_TIMEOUT_MS, TimeUnit.MILLISECONDS));
				pipeline.addLast(HandshakeDecoder.class.getSimpleName(), new HandshakeDecoder());
				pipeline.addLast(HandshakeEncoder.class.getSimpleName(), new HandshakeEncoder());
				pipeline.addLast(HandshakeHandler.class.getSimpleName(), new HandshakeHandler());
            }
		});

	}

	public static void bind(final int port) {
		try {
			bootstrap.bind(port).syncUninterruptibly();
		} catch (final Exception e) {
            log.error(Strings.EMPTY, e);
		}
	}

}
