package com.zenyte.game.packet.out;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.world.entity.SoundEffect;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 26. veebr 2018 : 2:06.19
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
@AllArgsConstructor
public final class SynthSound implements GamePacketEncoder {

	private final SoundEffect sound;

    @Override
    public void log(@NotNull final Player player) {
        this.log(player, "Sound: " + sound.getId() + ", delay: " + sound.getDelay());
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.SYNTH_SOUND;
		val buffer = new RSBuffer(prot);
		buffer.writeShort(sound.getId());
		buffer.writeByte(sound.getRepetitions());
		buffer.writeShort(sound.getDelay());
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}
