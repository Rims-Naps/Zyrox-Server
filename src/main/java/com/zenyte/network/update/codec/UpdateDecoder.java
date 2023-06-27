package com.zenyte.network.update.codec;

import com.zenyte.network.update.packet.inc.EncryptionKeyUpdate;
import com.zenyte.network.update.packet.inc.FileRequest;
import com.zenyte.network.update.packet.inc.LoginUpdate;
import com.zenyte.network.update.packet.inc.UpdateType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Tommeh | 27 jul. 2018 | 20:13:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class UpdateDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return;
		}
		final Channel channel = ctx.channel();
		if (!channel.isOpen() || !channel.isActive()) {
			return;
		}
		final short type = in.readUnsignedByte();
		final UpdateType state = UpdateType.VALUES[type];
		switch (state) {
			case NORMAL_FILE_REQUEST:
			case PRIORITY_FILE_REQUEST:
				final short index = in.readUnsignedByte();
				//We automatically block any requests done to index 16 as this index is completely unused by the client since revision 178, and contains the largest and most dangerous
				//files in the cache.
				if (index == 16) {
					out.clear();
					ctx.close();
					return;
				}
				final int file = in.readUnsignedShort();
				final int hash = index | (file << 16);
				out.add(new FileRequest(state.equals(UpdateType.PRIORITY_FILE_REQUEST), index, file));
				break;
			case CLIENT_LOGGED_IN:
			case CLIENT_LOGGED_OUT:
				out.add(new LoginUpdate(state.equals(UpdateType.CLIENT_LOGGED_IN), in.readUnsignedMedium()));
				break;
			case ENCRYPTION_KEY_UPDATE:
				final short key = in.readUnsignedByte();
				in.readShort();
				out.add(new EncryptionKeyUpdate(key));
				break;
			case CLIENT_CONNECTED:
			case CLIENT_DECONNECTED:
				out.add(new LoginUpdate(state.equals(UpdateType.CLIENT_CONNECTED), in.readUnsignedMedium()));
				break;
			default:
				break;
		}
	}

}
