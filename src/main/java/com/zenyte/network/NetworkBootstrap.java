package com.zenyte.network;

import com.zenyte.game.packet.Session;
import com.zenyte.network.handshake.HandshakeHandler;
import com.zenyte.network.handshake.codec.HandshakeDecoder;
import com.zenyte.network.handshake.codec.HandshakeEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Tommeh | 27 jul. 2018 | 22:35:25
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Slf4j
public class NetworkBootstrap {
	
	private static final ServerBootstrap bootstrap;
	private static final NioEventLoopGroup boss;
	private static final NioEventLoopGroup worker;
	public static final AttributeKey<Session> SESSION = AttributeKey.valueOf(Session.class.getSimpleName());
	public static final AttributeKey<IntOpenHashSet> FILE_REQUESTS =
            AttributeKey.valueOf(IntOpenHashSet.class.getSimpleName());
    public static final AttributeKey<FileRequestCounter> FILE_REQUESTS_COUNTER =
            AttributeKey.valueOf(FileRequestCounter.class.getSimpleName());

	
	static {
		bootstrap = new ServerBootstrap();
		boss = new NioEventLoopGroup(1);
		worker = new NioEventLoopGroup();
		bootstrap.group(boss, worker);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
			@Override
			protected void initChannel(NioSocketChannel ch) {
				val pipeline = ch.pipeline();
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

	public static final class FileRequestCounter {
	    private final Int2IntMap map = new Int2IntOpenHashMap(1024);

	    public int incrementCount(final int hash) {
	        val count = map.get(hash);
	        map.put(hash, count);
	        return count;
        }
    }

}
