package com.zenyte.game.packet.out;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.ui.PaneType;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:01:53
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class IfOpenSub implements GamePacketEncoder {
	
	private final int interfaceId, paneChildId;
	private final PaneType pane;
	private final boolean passable;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", pane: " + pane.getId() + ", name: " + pane.name() + ", child: " + paneChildId + ", passable: " + passable);
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.IF_OPENSUB;
		val buffer = new RSBuffer(prot);
		buffer.writeByte128(passable ? 1 : 0);
		buffer.writeIntV1(pane.getId() << 16 | paneChildId);
		buffer.writeShortLE128(interfaceId);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}
	
}
