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
 * @author Tommeh | 31 mrt. 2018 : 22:14:49
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class IfClearItems implements GamePacketEncoder {
	
    private final int interfaceId, componentId;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId);
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.IF_CLEARITEMS;
		val buffer = new RSBuffer(prot);
		buffer.writeIntV1(interfaceId << 16 | componentId);
		return new GamePacketOut(ServerProt.IF_CLEARITEMS, buffer);
	}

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

}
