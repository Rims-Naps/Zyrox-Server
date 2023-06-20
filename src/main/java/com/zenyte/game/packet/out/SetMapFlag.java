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
 * @author Tommeh | 28 jul. 2018 | 18:50:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class SetMapFlag implements GamePacketEncoder {

	private final int x, y;

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "X: " + x + ", y: " + y);
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.SET_MAP_FLAG;
		val buffer = new RSBuffer(prot);
		buffer.writeByte(x);
		buffer.writeByte(y);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
