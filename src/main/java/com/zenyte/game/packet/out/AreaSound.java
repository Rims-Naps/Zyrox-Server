package com.zenyte.game.packet.out;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 26. veebr 2018 : 1:58.03
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@AllArgsConstructor
public final class AreaSound implements GamePacketEncoder {

	private final Player player;
	private final Location tile;
	private final SoundEffect sound;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Id: " + sound.getId() + ", radius: " + sound.getRadius() + ", delay: " + sound.getDelay());
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.AREA_SOUND;
		val buffer = new RSBuffer(prot);
		buffer.writeByte(tile.getLocalHash(player.getLastLoadedMapRegionTile()));
		buffer.write128Byte(sound.getDelay());
		buffer.writeByte128((sound.getRadius() << 4) | (sound.getRepetitions() & 0xF));
		buffer.writeShortLE128(sound.getId());
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
