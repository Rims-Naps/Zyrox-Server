package com.zenyte.network.update.codec;

import com.zenyte.Constants;
import com.zenyte.network.NetworkBootstrap;
import com.zenyte.network.update.packet.inc.EncryptionKeyUpdate;
import com.zenyte.network.update.packet.inc.FileRequest;
import com.zenyte.network.update.packet.inc.LoginUpdate;
import com.zenyte.network.update.packet.inc.UpdateType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.val;

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
		val channel = ctx.channel();
		if (!channel.isOpen() || !channel.isActive()) {
		    return;
        }
		val type = in.readUnsignedByte();
		val state = UpdateType.VALUES[type];
		switch (state) {
		case NORMAL_FILE_REQUEST:
		case PRIORITY_FILE_REQUEST:
			val index = in.readUnsignedByte();
			//We automatically block any requests done to index 16 as this index is completely unused by the client since revision 178, and contains the largest and most dangerous
            //files in the cache.
			if (index == 16) {
			    out.clear();
			    ctx.channel().close();
			    return;
            }
			val file = in.readUnsignedShort();
			val hash = index | (file << 16);
			if (Constants.FILTERING_DUPLICATE_JS5_REQUESTS) {
                val set = ctx.channel().attr(NetworkBootstrap.FILE_REQUESTS).get();
                if (!set.add(hash)) {
                    if (Constants.WORLD_PROFILE.isDevelopment()) {
                        System.err.println("Requesting duplicate file: " + index + ", " + file);
                    }
                    val counter = ctx.channel().attr(NetworkBootstrap.FILE_REQUESTS_COUNTER).get();
                    if (counter.incrementCount(hash) >= Constants.MAX_DUPLICATE_FILE_REQUESTS) {
                        System.err.println("Closing channel due to duplicate file requests.");
                        out.clear();
                        ctx.channel().close();
                        return;
                    }
                }
            }
			out.add(new FileRequest(state.equals(UpdateType.PRIORITY_FILE_REQUEST), index, file));
			break;
		case CLIENT_LOGGED_IN:
		case CLIENT_LOGGED_OUT:
			out.add(new LoginUpdate(state.equals(UpdateType.CLIENT_LOGGED_IN), in.readUnsignedMedium()));
			break;	
		case ENCRYPTION_KEY_UPDATE:
			val key = in.readUnsignedByte();
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
