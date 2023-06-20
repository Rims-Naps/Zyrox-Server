package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.EventAppletFocusEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 28 jul. 2018 | 19:27:33
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class EventAppletFocusDecoder implements ClientProtDecoder<EventAppletFocusEvent> {

    @Override
    public EventAppletFocusEvent decode(Player player, int opcode, RSBuffer buffer) {
        val active = buffer.readByte() == 1;
        return new EventAppletFocusEvent(active);
    }
}
