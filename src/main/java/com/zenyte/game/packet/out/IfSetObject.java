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
 * @author Tommeh | 28 jul. 2018 | 18:24:01
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class IfSetObject implements GamePacketEncoder {
	
    private final int interfaceId, componentId, itemId, zoom;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", item: " + itemId + ", zoom: " + zoom);
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.IF_SETOBJECT;
		val buffer = new RSBuffer(prot);
		buffer.writeInt(interfaceId << 16 | componentId);
		buffer.writeShort(itemId);
		buffer.writeIntV2(zoom);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
