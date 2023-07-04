package com.zenyte.network.handshake;

import com.zenyte.Constants;
import com.zenyte.api.client.query.ApiIPCheck;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import com.zenyte.network.ClientResponse;
import com.zenyte.network.NetworkConstants;
import com.zenyte.network.handshake.codec.HandshakeDecoder;
import com.zenyte.network.handshake.codec.HandshakeEncoder;
import com.zenyte.network.handshake.packet.HandshakePacketIn;
import com.zenyte.network.handshake.packet.HandshakePacketOut;
import com.zenyte.network.handshake.packet.inc.HandshakeRequest;
import com.zenyte.network.handshake.packet.inc.HandshakeType;
import com.zenyte.network.login.LoginHandler;
import com.zenyte.network.login.codec.LoginDecoder;
import com.zenyte.network.login.codec.LoginEncoder;
import com.zenyte.network.update.UpdateHandler;
import com.zenyte.network.update.codec.UpdateDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.val;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tommeh | 27 jul. 2018 | 22:14:50
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
public class HandshakeHandler extends SimpleChannelInboundHandler<HandshakePacketIn> {

    private static final Object2ObjectOpenHashMap<String, List<Channel>> handshakeRequests =
            new Object2ObjectOpenHashMap<>();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HandshakePacketIn msg) throws Exception {
	    if (!verifyConnection(ctx)) {
	        return;
        }
		if (msg instanceof HandshakeType) {
			val type = (HandshakeType) msg;
			ctx.writeAndFlush(new HandshakePacketOut(type, ClientResponse.SUCCESSFUL), ctx.voidPromise());
			ctx.pipeline().replace(HandshakeDecoder.class.getSimpleName(), LoginDecoder.class.getSimpleName(), new LoginDecoder());
			ctx.pipeline().replace(HandshakeEncoder.class.getSimpleName(), LoginEncoder.class.getSimpleName(), new LoginEncoder());
			ctx.pipeline().replace(HandshakeHandler.class.getSimpleName(), LoginHandler.class.getSimpleName(), new LoginHandler());
		} else if (msg instanceof HandshakeRequest) {
            val request = (HandshakeRequest) msg;
            if (request.getRevision() == NetworkConstants.REVISION) {
                ctx.writeAndFlush(new HandshakePacketOut(request.getType(), ClientResponse.SUCCESSFUL), ctx.voidPromise());
                ctx.pipeline().replace(HandshakeDecoder.class.getSimpleName(), UpdateDecoder.class.getSimpleName(), new UpdateDecoder());
                ctx.pipeline().remove(HandshakeEncoder.class.getSimpleName());
                ctx.pipeline().replace(HandshakeHandler.class.getSimpleName(), UpdateHandler.class.getSimpleName(), new UpdateHandler());
            } else {
                ctx.writeAndFlush(new HandshakePacketOut(request.getType(), ClientResponse.SERVER_UPDATED)).addListener(ChannelFutureListener.CLOSE);
            }
        }
	}

	private static final synchronized boolean verifyConnection(final ChannelHandlerContext ctx) {
        if (true) return true;
		if (Constants.WORLD_PROFILE.isDevelopment())
	        return true;
        val remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            val socketAddress = (InetSocketAddress) remoteAddress;
            val hostAddress = socketAddress.getAddress().getHostAddress();
            if (Constants.ANTIKNOX) {
                if (ApiIPCheck.invalidIPs.contains(hostAddress)) {
                    ctx.channel().close();
                    return false;
                }
            }
            if (PunishmentManager.isPunishmentActive(null, hostAddress, null, PunishmentType.IP_BAN).isPresent()) {
                ctx.channel().close();
                return false;
            }
            val list = handshakeRequests.computeIfAbsent(hostAddress, k -> new ArrayList<>());
            list.removeIf(channel -> !channel.isOpen());
            if (list.size() >= Constants.MAXIMUM_NUMBER_OF_HANDSHAKE_CONNECTIONS) {
                System.err.println("Too many connections from ip: " + hostAddress);
                ctx.channel().close();
                return false;
            }
            list.add(ctx.channel());
        }
        return true;
    }

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
		if (cause instanceof IOException) {
			return;
		}
		cause.printStackTrace();
	}

}
