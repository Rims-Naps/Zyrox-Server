package com.zenyte.game.packet;

import com.zenyte.Constants;
import com.zenyte.Game;
import com.zenyte.game.constants.ClientProt;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerLogger;
import com.zenyte.network.game.packet.GamePacketIn;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.security.ISAACCipherPair;
import com.zenyte.network.login.packet.LoginPacketIn;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Tommeh | 28 jul. 2018 | 11:24:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>
 */
@Slf4j
public class Session {

    private static final ClientProtDecoder[] decoders = Game.getDecoders();
    @Getter
    private final Channel channel;
    @Getter
    private final LoginPacketIn request;
    @Getter private final Queue<GamePacketOut> gamePacketOutQueue = new LinkedList<>();
    @Getter private final Queue<GamePacketOut> gamePacketOutPrioritizedQueue = new LinkedList<>();

    private final Queue<ClientProtEvent> events = new ConcurrentLinkedQueue<>();

    private final int[] processedPackets = new int[0xFF];
    private int size;
    @Getter
    @Setter
    private Player player;
    @Getter private BufferTracker bufferTracker;
    private boolean closed;

    public Session(final Channel channel, final LoginPacketIn request) {
        this.channel = channel;
        this.request = request;
        this.bufferTracker = new BufferTracker();
    }

    public boolean write(final GamePacketOut packet) {
        if (!bufferTracker.canWrite(packet))
            return false;
        channel.write(packet, channel.voidPromise());
        bufferTracker.appendBytes(packet);
        return true;
    }

    public void flush() {
        channel.flush();
        bufferTracker.reset();
    }

    public void decode(final GamePacketIn packet) {
        if (closed || player.isNulled()) {
            return;
        }
        player.setLastReceivedPacket(System.currentTimeMillis());
        if (++size >= Constants.CUMULATIVE_PACKETS_LIMIT) {
            channel.close();
            closed = true;
            events.clear();
            throw new IllegalStateException("Maximum packet limit exceedeed: " + player.getUsername());
        }
        val opcode = packet.getOpcode();
        val prot = ClientProt.get(opcode);
        try {
            if (opcode < 0 || opcode >= 256) {
                return;
            }
            val decoder = decoders[opcode];
            if (decoder == null) {
                System.err.println("Unhandled opcode: " + opcode);
                return;
            }
            if (++processedPackets[opcode] > prot.getLimit()) {
                return;
            }
            val event = decoder.decode(player, opcode, packet.getBuffer());
            if (event != null) {
                events.add(event);
            }
        } catch (final Exception e) {
            log.error(Strings.EMPTY, e);
        } finally {
            packet.getBuffer().release();
        }
    }

    public void processEvents() {
        if (!events.isEmpty()) {
            for (val event : events) {
                try {
                    event.handle(player);
                    if (event.level().getPriority() >= PlayerLogger.WRITE_LEVEL.getPriority()) {
                        event.log(player);
                    }
                } catch (Exception e) {
                    log.error(Strings.EMPTY, e);
                }
            }
            events.clear();
            for (int i = 0; i < 0xFF; i++) {
                processedPackets[i] = 0;
            }
            size = 0;
        }
    }


    public ISAACCipherPair getISAACCipherPair() {
        return request.getIsaacCipherPair();
    }
}
