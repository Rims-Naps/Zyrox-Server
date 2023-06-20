package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpNpcExamineEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Tommeh | 25-1-2019 | 19:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpNpcExamineDecoder implements ClientProtDecoder<OpNpcExamineEvent> {

    @Override
    public OpNpcExamineEvent decode(Player player, final int opcode, final RSBuffer buffer) {
        val npcId = buffer.readShort128();
        return new OpNpcExamineEvent(npcId);
    }
}
