package com.zenyte.game.packet.out;

import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:48:30
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class ChatFilterSettings implements GamePacketEncoder {
	
	private final Player player;

    @Override
    public void log(@NotNull final Player player) {
        log(player,
                "Trade filter: " + player.getNumericAttribute(Setting.TRADE_FILTER.toString()).intValue() + ", public filter: " + player.getNumericAttribute(Setting.PUBLIC_FILTER.toString()).intValue());
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.CHAT_FILTER_SETTINGS;
		val buffer = new RSBuffer(prot);
		buffer.writeByteC(player.getNumericAttribute(Setting.TRADE_FILTER.toString()).intValue());
        buffer.writeByteC(player.getNumericAttribute(Setting.PUBLIC_FILTER.toString()).intValue());
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.HIGH_PACKET;
	}
}