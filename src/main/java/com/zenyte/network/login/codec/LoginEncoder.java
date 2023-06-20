package com.zenyte.network.login.codec;

import com.zenyte.game.world.entity.player.Device;
import com.zenyte.network.ClientResponse;
import com.zenyte.network.NetworkBootstrap;
import com.zenyte.network.login.packet.LoginPacketOut;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.val;

/**
 * @author Tommeh | 27 jul. 2018 | 19:04:53
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>
 */
public class LoginEncoder extends MessageToByteEncoder<LoginPacketOut> {

	@Override
	protected void encode(final ChannelHandlerContext ctx, final LoginPacketOut packet, final ByteBuf out) throws Exception {
		val response = packet.getResponse();
		val device = packet.getDevice();
		out.writeByte(response.getId());
		if (!response.equals(ClientResponse.LOGIN_OK)) {
			return;
		}
        val player = ctx.channel().attr(NetworkBootstrap.SESSION).get().getPlayer();
        if (player == null) {
            throw new RuntimeException("Player is null.");
        }
		out.writeByte(device.equals(Device.DESKTOP) ? 13 : 14);
		val authenticator = player.getAuthenticator().isEnabled();
		val trusted = player.getAuthenticator().isTrusted();
		val randomUID = player.getAuthenticator().getRandomUID();
		if (!authenticator || !trusted) {
			out.writeByte(0);
			out.writeInt(0);
		} else {
		    //If authenticator is enabled, we send a random UID to the client that is then stored on the player's local computer; The next login, the client will send that same
            // number to us; if the number matches and hasn't expired, we allow login without having to enter the code.
            val encryptor = ctx.channel().attr(NetworkBootstrap.SESSION).get().getISAACCipherPair().getEncodingRandom();
            out.writeByte(1);
            out.writeByte(((randomUID >> 24 & 0xFF) + encryptor.nextInt()) & 0xFF);
            out.writeByte(((randomUID >> 16 & 0xFF) + encryptor.nextInt()) & 0xFF);
            out.writeByte(((randomUID >> 8 & 0xFF) + encryptor.nextInt()) & 0xFF);
            out.writeByte(((randomUID & 0xFF) + encryptor.nextInt()) & 0xFF);
		}
		out.writeByte(player.getPrivilege().ordinal());
		out.writeByte(player.getPrivilege().ordinal() > 0 ? 1 : 0);//Is p-mod.
		out.writeShort(player.getIndex());
		out.writeByte(1);//Friends/ignores container size. 0 = 200, 1 = 400. OSRS allows 400 for members, 200 for F2P.
	}

}
