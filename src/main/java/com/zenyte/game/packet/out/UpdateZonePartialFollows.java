package com.zenyte.game.packet.out;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 19:05:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 *      profile</a>}
 */
@AllArgsConstructor
public class UpdateZonePartialFollows implements GamePacketEncoder {

	private final int x, y;
	private final Player player;

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "X: " + x + ", y: " + y);
    }


    private static int getLocal(int abs, int chunk) {
		return abs - 8 * (chunk - 6);
	}

	@Override
	public GamePacketOut encode() {
	    val prot = ServerProt.UPDATE_ZONE_PARTIAL_FOLLOWS;
		val buffer = new RSBuffer(prot);
		val localX = getLocal(((x >> 3) << 3), player.getLastLoadedMapRegionTile().getChunkX());
		val localY = getLocal(((y >> 3) << 3), player.getLastLoadedMapRegionTile().getChunkY());
		buffer.writeByteC(localX);
		buffer.write128Byte(localY);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
