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
 * @author Tommeh | 28 jul. 2018 | 16:09:02
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class IfMoveSub implements GamePacketEncoder {
	
    private final int fromPaneId, fromPaneChildId, toPaneId, toPaneChildId;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Pane: " + fromPaneId + " -> " + toPaneId + ", child: " + fromPaneChildId + " -> " + toPaneChildId);
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.IF_MOVESUB;
		val buffer = new RSBuffer(prot);
		buffer.writeInt(toPaneId << 16 | toPaneChildId);
		buffer.writeInt(fromPaneId << 16 | fromPaneChildId);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}
}
