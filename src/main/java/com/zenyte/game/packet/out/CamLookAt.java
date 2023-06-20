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
 * @author Kris | 4. dets 2017 : 13:45.09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@AllArgsConstructor
public final class CamLookAt implements GamePacketEncoder {

	private final int viewLocalX, viewLocalY, cameraHeight, speed, acceleration;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "X: " + viewLocalX + ", y: " + viewLocalY + ", height: " + cameraHeight + ", speed: " + speed
         + ", acceleration: " + acceleration);
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.CAM_LOOKAT;
		val buffer = new RSBuffer(prot);
		buffer.writeByte(viewLocalX);
		buffer.writeByte(viewLocalY);
		buffer.writeShort(cameraHeight);
		buffer.writeByte(speed);
		buffer.writeByte(acceleration);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.HIGH_PACKET;
	}

}
