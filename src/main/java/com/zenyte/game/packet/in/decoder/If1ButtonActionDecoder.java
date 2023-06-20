package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.constants.ClientProt;
import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.If1ButtonEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Kris | 25/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class If1ButtonActionDecoder implements ClientProtDecoder<If1ButtonEvent> {

    public static final int[] OPCODES = {
            ClientProt.IF1_ACTION1.getOpcode(),
            ClientProt.IF1_ACTION2.getOpcode(),
            ClientProt.IF1_ACTION3.getOpcode(),
            ClientProt.IF1_ACTION4.getOpcode(),
            ClientProt.IF1_ACTION5.getOpcode()
    };

    @Override
    public If1ButtonEvent decode(Player player, int opcode, RSBuffer buffer) {
        int itemId, slotId, compressed;
        if (opcode == ClientProt.IF1_ACTION1.getOpcode()) {
            itemId = buffer.readShortLE128() & 0xFFFF;
            compressed = buffer.readIntV2();
            slotId = buffer.readShortLE() & 0xFFFF;
        } else if (opcode == ClientProt.IF1_ACTION2.getOpcode()) {
            compressed = buffer.readIntV2();
            slotId = buffer.readShortLE() & 0xFFFF;
            itemId = buffer.readShort128() & 0xFFFF;
        } else if (opcode == ClientProt.IF1_ACTION3.getOpcode()) {
            compressed = buffer.readIntLE();
            slotId = buffer.readShort128() & 0xFFFF;
            itemId = buffer.readShort() & 0xFFFF;
        } else if (opcode == ClientProt.IF1_ACTION4.getOpcode()) {
            compressed = buffer.readInt();
            slotId = buffer.readShort128() & 0xFFFF;
            itemId = buffer.readShort() & 0xFFFF;
        } else if (opcode == ClientProt.IF1_ACTION5.getOpcode()) {
            itemId = buffer.readShort() & 0xFFFF;
            compressed = buffer.readIntV1();
            slotId = buffer.readShort() & 0xFFFF;
        } else {
            throw new RuntimeException();
        }
        val interfaceId = (compressed >> 16);
        val componentId = (compressed & 0xFFFF);
        val option = ArrayUtils.indexOf(OPCODES, opcode) + 1;
        return new If1ButtonEvent(interfaceId, componentId, slotId, itemId, option);
    }
}

