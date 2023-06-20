package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.MessagePrivateEvent;
import com.zenyte.game.util.StringUtilities;
import com.zenyte.game.world.entity.masks.ChatMessage;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 20:05:47
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MessagePrivateDecoder implements ClientProtDecoder<MessagePrivateEvent> {

    @Override
    public MessagePrivateEvent decode(Player player, int opcode, RSBuffer buffer) {
        val recipient = buffer.readString();
        val message = StringUtilities.readString(buffer, 32767);
        return new MessagePrivateEvent(recipient, new ChatMessage().set(message, 0, false));
    }
}
