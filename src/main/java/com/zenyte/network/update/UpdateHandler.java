package com.zenyte.network.update;

import com.zenyte.Game;
import com.zenyte.network.NetworkBootstrap;
import com.zenyte.network.update.packet.UpdatePacketIn;
import com.zenyte.network.update.packet.UpdatePacketOut;
import com.zenyte.network.update.packet.inc.EncryptionKeyUpdate;
import com.zenyte.network.update.packet.inc.FileRequest;
import com.zenyte.network.update.packet.inc.LoginUpdate;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Tommeh | 27 jul. 2018 | 20:55:07
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@Slf4j
public class UpdateHandler extends SimpleChannelInboundHandler<UpdatePacketIn> {

    private static final long POLL_RATE = 100;
    private static final int POLL_LIMIT = 100;

    private static final Int2ObjectMap<ByteBuf> cachedFiles =
            Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>(0xFFFF));

    private final Queue<FileRequest> highPriorityRequests = new ConcurrentLinkedQueue<>();
    private final Queue<FileRequest> lowPriorityRequests = new ConcurrentLinkedQueue<>();

    private ScheduledFuture<?> pollTask;

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        pollTask = ctx.channel().eventLoop().scheduleAtFixedRate(() -> pollRequests(ctx), POLL_RATE, POLL_RATE, TimeUnit.MILLISECONDS);
        ctx.channel().attr(NetworkBootstrap.FILE_REQUESTS).set(new IntOpenHashSet(Byte.SIZE << 8));
        ctx.channel().attr(NetworkBootstrap.FILE_REQUESTS_COUNTER).set(new NetworkBootstrap.FileRequestCounter());
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        if (pollTask != null) {
            pollTask.cancel(false);
            pollTask = null;
        }
    }

    private void pollRequests(final ChannelHandlerContext ctx) {
        int count = 0;
        while (!highPriorityRequests.isEmpty() && count < POLL_LIMIT) {
            handleFileRequest(ctx, highPriorityRequests.poll());
            count++;
        }

        while (!lowPriorityRequests.isEmpty() && count < POLL_LIMIT) {
            handleFileRequest(ctx, lowPriorityRequests.poll());
            count++;
        }

        if (count > 0) {
            ctx.flush();
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final UpdatePacketIn msg) throws Exception {
        if (msg instanceof FileRequest) {
            val request = (FileRequest) msg;

            if (request.isPriority()) {
                highPriorityRequests.offer(request);
            } else {
                lowPriorityRequests.offer(request);
            }
        } else if (msg instanceof LoginUpdate) {
            // unsupported
        } else if (msg instanceof EncryptionKeyUpdate) {
            // unsupported
        }
    }

    private void handleFileRequest(final ChannelHandlerContext ctx, final FileRequest request) {
        val index = request.getIndex();
        val fileId = request.getFile();
        val hash = index | (fileId << 16);
        var file = cachedFiles.get(hash);
        val cache = Game.getCacheMgi();
        if (file == null) {
            ByteBuf container = null;
            if (index == 255 && fileId == 255) {
                container = Unpooled.wrappedBuffer(Game.getChecksumBuffer());
            } else {
                container = Unpooled.wrappedBuffer(Game.getCacheMgi().getIndex(index).get(fileId).getBuffer());

                if (index != 255 && (container.readableBytes() > 1)) {
                    container = container.slice(0, container.readableBytes() - 2);
                }
            }
            cachedFiles.put(hash, file = container);
        }
        ctx.write(new UpdatePacketOut(request, file));
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        if (cause instanceof IOException) {
            return;
        }
        cause.printStackTrace();
    }
}
