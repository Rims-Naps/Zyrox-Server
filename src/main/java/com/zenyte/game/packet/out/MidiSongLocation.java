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
 * @author Kris | 14. apr 2018 : 15:03.45
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@AllArgsConstructor
public final class MidiSongLocation implements GamePacketEncoder {
	
	private final int trackId;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Song: " + trackId);
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.MIDI_SONG_LOCATION;
		val buffer = new RSBuffer(prot);
		buffer.writeMedium(0);
		buffer.writeShort128(trackId);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
