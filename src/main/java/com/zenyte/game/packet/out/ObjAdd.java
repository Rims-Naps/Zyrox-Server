package com.zenyte.game.packet.out;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.flooritem.FloorItem;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:40:23
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class ObjAdd implements GamePacketEncoder {
	
    private final FloorItem floorItem;

    @Override
    public void log(@NotNull final Player player) {
        val tile = floorItem.getLocation();
        this.log(player, "Item: " + floorItem.getId() + ", amount: " + floorItem.getAmount() + ", x: " + tile.getX() + ", y: " + tile.getY() + ", z: " + tile.getPlane());
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.OBJ_ADD;
		val buffer = new RSBuffer(prot);
		val targetLocalX = floorItem.getLocation().getX() - ((floorItem.getLocation().getX() >> 3) << 3);
		val targetLocalY = floorItem.getLocation().getY() - ((floorItem.getLocation().getY() >> 3) << 3);
        val offsetHash = (targetLocalX & 0x7) << 4 | (targetLocalY & 0x7);
		buffer.writeShort128(Math.min(0xFFFF, floorItem.getAmount()));
       	buffer.writeShortLE128(floorItem.getId());
       	buffer.writeByte128(offsetHash);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
