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
 * @author Tommeh | 28 jul. 2018 | 18:41:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class UpdateZoneFullFollows implements GamePacketEncoder {

	private final int chunkX, chunkY;

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "X: " + chunkX + ", y: " + chunkY);
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.UPDATE_ZONE_FULL_FOLLOWS;
		val buffer = new RSBuffer(prot);
		buffer.writeByteC(chunkY);
		buffer.writeByte(chunkX);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
