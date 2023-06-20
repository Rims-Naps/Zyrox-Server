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
 * @author Tommeh | 28 jul. 2018 | 19:02:06
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class UpdateRunEnergy implements GamePacketEncoder {
	
	private final int energy;

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "Energy: " + energy);
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.UPDATE_RUNENERGY;
		val buffer = new RSBuffer(prot);
		buffer.writeByte(Math.min(255, energy));
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
