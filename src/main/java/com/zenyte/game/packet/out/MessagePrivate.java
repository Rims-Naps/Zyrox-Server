package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.ChatMessage;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:36:21
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class MessagePrivate implements GamePacketEncoder {

	private final String sender;
	private final ChatMessage message;
	private final int icon;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Sender: " + sender + ", icon: " + icon + ", message: " + message.getChatText());
    }

    @Override
	public GamePacketOut encode() {
	    val prot = ServerProt.MESSAGE_PRIVATE;
		val buffer = new RSBuffer(prot);
		buffer.writeString(sender);
		for (int i = 0; i < 5; i++) {
			buffer.writeByte(Utils.random(255));
		}
		buffer.writeShort(icon);
		buffer.writeBytes(message.getCompressedArray(), 0, message.getOffset());
		return new GamePacketOut(prot, buffer);
	}

	@Override
	public LogLevel level() {
		return LogLevel.HIGH_PACKET;
	}

}
