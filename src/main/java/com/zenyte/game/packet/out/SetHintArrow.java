package com.zenyte.game.packet.out;

import com.zenyte.game.HintArrow;
import com.zenyte.game.HintArrowPosition;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.world.entity.Entity.EntityType;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:49:15
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class SetHintArrow implements GamePacketEncoder {

	private final HintArrow icon;

    @Override
    public void log(@NotNull final Player player) {
        if (icon == null) {
            log(player, "Hint arrow reset");
        } else {
            log(player, "Position: " + icon.getPosition().name() + ", " + (icon.getPosition() == HintArrowPosition.ENTITY ?
                                                                            ("Entity: " + icon.getTarget().getEntityType().name() + ", index: " + icon.getTarget().getIndex()) :
                                                                            ("x: " + icon.getX() + ", y: " + icon.getY() + ", height: " + icon.getHeight())));
        }
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.SET_HINTARROW;
		val buffer = new RSBuffer(prot);
		if (icon == null) {
		    buffer.writeBytes(new byte[6]);
			return new GamePacketOut(ServerProt.SET_HINTARROW, buffer);
		}
		if (icon.getPosition() == HintArrowPosition.ENTITY) {
			buffer.writeByte(icon.getTarget().getEntityType() == EntityType.NPC ? 1 : 10);
			buffer.writeShort(icon.getTarget().getIndex());
			buffer.writeBytes(new byte[3]);
		} else {
			buffer.writeByte(icon.getPosition().getPositionHash());
			buffer.writeShort(icon.getX());
			buffer.writeShort(icon.getY());
			buffer.writeByte(icon.getHeight());
		}
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
