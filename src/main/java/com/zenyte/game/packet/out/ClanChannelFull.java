package com.zenyte.game.packet.out;

import com.zenyte.game.content.clans.ClanChannelBuilder;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:09:09
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class ClanChannelFull implements GamePacketEncoder {

	private final ClanChannelBuilder builder;

    @Override
    public void log(@NotNull final Player player) {
        val channel = builder.getChannel();
        this.log(player, "Channel owner: " + channel.getOwner() + ", prefix: " + channel.getPrefix() + ", members: " + channel.getMembers().size());
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.CLANCHANNEL_FULL;
		val buffer = new RSBuffer(prot);
		val prebuiltBuffer = builder.getBuffer().resetReaderIndex();
		buffer.writeBytes(prebuiltBuffer);
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.LOW_PACKET;
	}

}