package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerLogger;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kris | 24/10/2018 00:17
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
@AllArgsConstructor
public class UpdateZonePartialEnclosed implements GamePacketEncoder {

    private final int x, y;
    private final Player player;
    private final List<GamePacketEncoder> packets = new ArrayList<>();

    private static int getLocal(int abs, int chunk) {
        return abs - 8 * (chunk - 6);
    }

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "X: " + x + ", y: " + y);
    }


    @Override
    public GamePacketOut encode() {
        val prot = ServerProt.UPDATE_ZONE_PARTIAL_ENCLOSED;
        val buffer = new RSBuffer(prot);
        val localX = getLocal(((x >> 3) << 3), player.getLastLoadedMapRegionTile().getChunkX());
        val localY = getLocal(((y >> 3) << 3), player.getLastLoadedMapRegionTile().getChunkY());
        buffer.write128Byte(localY);
        buffer.writeByteC(localX);
        for (int i = packets.size() - 1; i >= 0; i--) {
            val packet = packets.get(i);
            val arrayIndex = ArrayUtils.indexOf(ZONE_FOLLOW_TYPES, packet.getClass());
            if (arrayIndex == -1) {
                continue;
            }
            buffer.writeByte(arrayIndex);
            buffer.writeBytes(packet.encode().getBuffer());
            if (packet.level().getPriority() >= PlayerLogger.WRITE_LEVEL.getPriority()) {
                packet.log(player);
            }
        }
        return new GamePacketOut(prot, buffer);
    }

    private static final Class<?>[] ZONE_FOLLOW_TYPES = new Class<?>[] {
            ObjUpdate.class, MapProjAnim.class, AttachedPlayerObject.class, SpotAnimSpecific.class,
            LocAnim.class, AreaSound.class, LocDel.class, ObjDel.class, ObjAdd.class, LocAdd.class
    };

    public void append(final GamePacketEncoder packet) {
        packets.add(packet);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }

}
