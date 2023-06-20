package com.zenyte.game.packet.out;

import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.masks.ChatMessage;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:38:04
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
@AllArgsConstructor
public class MessagePrivateEcho implements GamePacketEncoder {
	
    private final String toUser;
    private final ChatMessage message;

    @Override
    public void log(@NotNull final Player player) {
        log(player, "To: " + toUser + ", message: " + message.getChatText());
    }

    @Override
	public GamePacketOut encode() {
        val prot = ServerProt.MESSAGE_PRIVATE_ECHO;
        val buffer = new RSBuffer(prot);
        buffer.writeString(toUser);
        val bytes = message.getCompressedArray();
        buffer.writeSmart(bytes[0]);
        buffer.writeBytes(message.getCompressedArray(), 1, message.getOffset());
		return new GamePacketOut(prot, buffer);
	}

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

}
