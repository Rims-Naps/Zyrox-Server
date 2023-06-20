package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:29:57
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class LocDel implements GamePacketEncoder {
	
    private final WorldObject object;

    @Override
    public void log(@NotNull final Player player) {
        log(player,
                "Id: " + object.getId() + ", type: " + object.getType() + ", rotation: " + object.getRotation() + ", x: " + object.getX() + ", y: " + object.getY() + ", z: " + object.getPlane());
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.LOC_DEL;
		val buffer = new RSBuffer(prot);
		val targetLocalX = object.getX() - ((object.getX() >> 3) << 3);
		val targetLocalY = object.getY() - ((object.getY() >> 3) << 3);
        val offsetHash = (targetLocalX & 0x7) << 4 | (targetLocalY & 0x7);
        buffer.write128Byte((object.getType() << 2) + (object.getRotation() & 3));
        buffer.write128Byte(offsetHash);
		return new GamePacketOut(prot, buffer);
	}

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }

}
