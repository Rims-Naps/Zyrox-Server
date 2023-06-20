package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpLocExamineEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 25-1-2019 | 21:46
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpLocExamineDecoder implements ClientProtDecoder<OpLocExamineEvent> {

    @Override
    public OpLocExamineEvent decode(Player player, int opcode, RSBuffer buffer) {
        val id =  buffer.readShort128() & 0xFFFF;
        return new OpLocExamineEvent(id);
    }
}
