package com.zenyte.network.update.codec;

import com.zenyte.network.update.packet.UpdatePacketOut;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.val;

/**
 * @author Tommeh | 27 jul. 2018 | 20:13:48
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public class UpdateEncoder extends MessageToByteEncoder<UpdatePacketOut> {

    /**
     * Amount of bytes that can be send after sending metadata
     */
    private static final int BYTES_AFTER_HEADER = 512 - 8;

    /**
     * Amount of bytes that can be send
     */
    private static final int BYTES_AFTER_BLOCK = 512 - 1;

    @Override
    protected void encode(ChannelHandlerContext ctx, UpdatePacketOut packet, ByteBuf out) throws Exception {
        val container = packet.getContainer().resetReaderIndex();
        val settings = container.readUnsignedByte();
        val size = container.readInt();
        out.writeByte(packet.getRequest().getIndex());
        out.writeShort(packet.getRequest().getFile());
        out.writeByte(settings);
        out.writeInt(size);
        int bytes = container.readableBytes();
        if (bytes > BYTES_AFTER_HEADER) {
            bytes = BYTES_AFTER_HEADER;
        }
        ByteBuf buffer = container.readBytes(bytes);
        try {
            out.writeBytes(buffer);
        } finally {
            buffer.release();
        }
        while (container.readableBytes() > 0) {
            bytes = container.readableBytes();
            if (bytes > BYTES_AFTER_BLOCK) {
                bytes = BYTES_AFTER_BLOCK;
            }
            out.writeByte(255);
            buffer = container.readBytes(bytes);
            try {
                out.writeBytes(buffer);
            } finally {
                buffer.release();
            }
        }
    }

}
