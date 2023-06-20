package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class CamMoveTo implements GamePacketEncoder {

	private final int localX, localY, cameraHeight, speed, acceleration;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "X: " + localX + ", y: " + localY + ", height: " + cameraHeight + ", speed: " + speed
                + ", acceleration: " + acceleration);
    }

	@Override
	public GamePacketOut encode() {
	    val prot = ServerProt.CAM_MOVETO;
		val buffer = new RSBuffer(prot);
		buffer.writeByte(localX);
		buffer.writeByte(localY);
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
