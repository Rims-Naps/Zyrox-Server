package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.ui.PaneType;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 15:14:48
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class IfOpenTop implements GamePacketEncoder {
	
	private final PaneType pane;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Pane: " + pane.getId() + ", name: " + pane.name());
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.IF_OPENTOP;
		val buffer = new RSBuffer(prot);
		buffer.writeShortLE128(pane.getId());
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.HIGH_PACKET;
	}
}
