package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:28:56
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class LocAnim implements GamePacketEncoder {
	
	private final WorldObject object;
	private final Animation animation;

    @Override
    public void log(@NotNull final Player player) {
        log(player,
                "Id: " + object.getId() + ", type: " + object.getType() + ", rotation: " + object.getRotation() + ", x: " + object.getX() + ", y: " + object.getY() + ", z: " + object.getPlane() + ", animation id: " + animation.getId() + ", delay: " + animation.getDelay());
    }


    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.LOC_ANIM;
		val buffer = new RSBuffer(prot);
		buffer.write128Byte((object.getType() << 2) | object.getRotation());
		buffer.writeByteC((object.getX() & 7) << 4 | (object.getY() & 7));
		buffer.writeShort(animation.getId());
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
