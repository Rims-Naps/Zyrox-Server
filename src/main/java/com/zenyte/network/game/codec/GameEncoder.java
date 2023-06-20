package com.zenyte.network.game.codec;

import com.zenyte.network.NetworkBootstrap;
import com.zenyte.network.game.packet.GamePacketOut;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 12:51:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class GameEncoder extends MessageToByteEncoder<GamePacketOut> {

    @Override
    protected void encode(ChannelHandlerContext ctx, GamePacketOut packet, ByteBuf out) {
        val encryptor = ctx.channel().attr(NetworkBootstrap.SESSION).get().getISAACCipherPair().getEncodingRandom();
        val opcode = packet.getPacket().getOpcode();
        val size = packet.getPacket().getSize();
        val buffer = packet.getBuffer().resetReaderIndex();
        if (opcode >= 0xFF) {
            val low = opcode & 0xFF;
            val high = (opcode >> 8) & 0xFF;
            out.writeByte((high + 128) + encryptor.nextInt());
            out.writeByte((low + encryptor.nextInt()) & 0xFF);
        } else {
            out.writeByte((opcode + encryptor.nextInt()) & 0xFF);
        }
        if (size == -1) {
            out.writeByte(buffer.readableBytes());
        } else if (size == -2) {
            out.writeShort(buffer.readableBytes());
        }
        if (packet.encryptBuffer()) {
            int length = buffer.writerIndex();
            for (int i = buffer.readerIndex(); i < length; i++) {
                out.writeByte((buffer.getByte(i) + encryptor.nextInt()) & 0xFF);
            }
        } else {
            out.writeBytes(buffer);
        }
    }

}
