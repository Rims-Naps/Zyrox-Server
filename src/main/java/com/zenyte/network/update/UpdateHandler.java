package com.zenyte.network.update;

import com.zenyte.Game;
import com.zenyte.network.NetworkBootstrap;
import com.zenyte.network.update.packet.UpdatePacketIn;
import com.zenyte.network.update.packet.inc.EncryptionKeyUpdate;
import com.zenyte.network.update.packet.inc.FileRequest;
import com.zenyte.network.update.packet.inc.LoginUpdate;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jctools.queues.MpmcArrayQueue;
import org.jctools.queues.MpscArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Tommeh | 27 jul. 2018 | 20:55:07
 * @author Jire - fixed infinite memory growth exploit, and massively improved performance/responsiveness.
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class UpdateHandler extends SimpleChannelInboundHandler<UpdatePacketIn> {
    private static final Logger log = LoggerFactory.getLogger(UpdateHandler.class);
    /**
     * Amount of bytes that can be send after sending metadata
     */
    private static final int BYTES_AFTER_HEADER = 512 - 8;
    /**
     * Amount of bytes that can be send
     */
    private static final int BYTES_AFTER_BLOCK = 512 - 1;
    private static final int REQUEST_QUEUE_CAPACITY = 200;
    private static final int HIGH_PRIORITY_POLL_RATE = 20;
    private static final int LOW_PRIORITY_POLL_RATE = HIGH_PRIORITY_POLL_RATE * 10;
    private static final Int2ObjectMap<ByteBuf> cachedFiles = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>(65535));
    private static final ScheduledExecutorService lowPriorityService = NetworkBootstrap.eventLoopGroup(1);
    private static final ScheduledExecutorService highPriorityService = NetworkBootstrap.eventLoopGroup(0);
    private final Queue<FileRequest> lowPriorityRequests = new MpscArrayQueue<>(REQUEST_QUEUE_CAPACITY);
    private final Queue<FileRequest> highPriorityRequests = new MpmcArrayQueue<>(REQUEST_QUEUE_CAPACITY);
    private Future<?> lowPriorityPollTask;
    private Future<?> highPriorityPollTask;

    private static void pollRequests(final ChannelHandlerContext ctx, final Queue<FileRequest> queue, final int pollLimit) {
        final Channel channel = ctx.channel();
        int count = 0;
        for (int i = 0; i < pollLimit && channel.isWritable(); i++) {
            final FileRequest request = queue.poll();
            if (request == null) break;
            handleFileRequest(ctx, request);
            count++;
        }
        if (count > 0) {
            ctx.flush();
        }
    }

    private static void handleFileRequest(final ChannelHandlerContext ctx, final FileRequest request) {
        final int index = request.getIndex();
        final int fileId = request.getFile();
        final int hash = index | (fileId << 16);
        ByteBuf file = cachedFiles.get(hash);
        if (file == null) {
            ByteBuf container = null;
            try {
                if (index == 255 && fileId == 255) {
                    container = Unpooled.wrappedBuffer(Game.getChecksumBuffer());
                } else {
                    final byte[] buffer = Game.getCacheMgi().getIndex(index).get(fileId).getBuffer();
                    final int capacity = buffer.length;
                    container = Unpooled.directBuffer(capacity, capacity);
                    container.writeBytes(buffer);
                    if (index != 255 && (container.readableBytes() > 1)) {
                        container = container.slice(0, container.readableBytes() - 2);
                    }
                }
                file = encodeBuffer(container, index, fileId);
            } finally {
                if (container != null) {
                    container.release();
                }
            }
            cachedFiles.put(hash, file);
        }
        ctx.write(file.retainedDuplicate(), ctx.voidPromise());
    }

    private static ByteBuf encodeBuffer(final ByteBuf container, final int index, final int file) {
        final ByteBuf out = Unpooled.directBuffer();
        final short settings = container.readUnsignedByte();
        final int size = container.readInt();
        out.writeByte(index);
        out.writeShort(file);
        out.writeByte(settings);
        out.writeInt(size);
        int bytes = container.readableBytes();
        if (bytes > BYTES_AFTER_HEADER) {
            bytes = BYTES_AFTER_HEADER;
        }
        ByteBuf buffer = container.readSlice(bytes);
        out.writeBytes(buffer);
        while ((bytes = container.readableBytes()) > 0) {
            if (bytes > BYTES_AFTER_BLOCK) {
                bytes = BYTES_AFTER_BLOCK;
            }
            out.writeByte(255);
            buffer = container.readSlice(bytes);
            out.writeBytes(buffer);
        }
        return out;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        lowPriorityPollTask = lowPriorityService.scheduleAtFixedRate(() -> pollRequests(ctx, lowPriorityRequests, REQUEST_QUEUE_CAPACITY), LOW_PRIORITY_POLL_RATE, LOW_PRIORITY_POLL_RATE, TimeUnit.MILLISECONDS);
        highPriorityPollTask = highPriorityService.scheduleAtFixedRate(() -> pollRequests(ctx, highPriorityRequests, REQUEST_QUEUE_CAPACITY), HIGH_PRIORITY_POLL_RATE, HIGH_PRIORITY_POLL_RATE, TimeUnit.MILLISECONDS);
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) {
        Future<?> lowPriorityPollTask = this.lowPriorityPollTask;
        if (lowPriorityPollTask != null) {
            lowPriorityPollTask.cancel(false);
            lowPriorityPollTask = null;
        }
        Future<?> highPriorityPollTask = this.highPriorityPollTask;
        if (highPriorityPollTask != null) {
            highPriorityPollTask.cancel(false);
            highPriorityPollTask = null;
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final UpdatePacketIn msg) {
        if (msg instanceof FileRequest) {
            final FileRequest request = (FileRequest) msg;
            if (request.isPriority()) {
                highPriorityRequests.offer(request);
            } else {
                lowPriorityRequests.offer(request);
            }
        } else if (msg instanceof LoginUpdate) {
        } else
            // unsupported
            if (msg instanceof EncryptionKeyUpdate) {
            }
        // unsupported
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        if (cause instanceof IOException) {
            return;
        }
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            ctx.close();
        }
    }

}
