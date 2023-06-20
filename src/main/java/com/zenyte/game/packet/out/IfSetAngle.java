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
 * @author Kris | 14. apr 2018 : 14:48.38
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@AllArgsConstructor
public final class IfSetAngle implements GamePacketEncoder {
    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", rotationX: " + rotationX + ", rotationY: " + rotationY + ", zoom: " + modelZoom);
    }

    private final int interfaceId, componentId, rotationX, rotationY, modelZoom;

	@Override
	public GamePacketOut encode() {
	    val prot = ServerProt.IF_SETANGLE;
		val buffer = new RSBuffer(prot);
		buffer.writeShortLE128(rotationX);
		buffer.writeShortLE128(modelZoom);
		buffer.writeShortLE128(rotationY);
		buffer.writeIntV2(interfaceId << 16 | componentId);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
