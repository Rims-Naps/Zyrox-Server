package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.constants.ClientProt;
import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpHeldEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Tommeh | 28 jul. 2018 | 19:46:42
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpHeldDecoder implements ClientProtDecoder<OpHeldEvent> {

	public static final int[] OPCODES = {
			ClientProt.OPHELD1.getOpcode(),
			ClientProt.OPHELD2.getOpcode(),
			ClientProt.OPHELD3.getOpcode(),
			ClientProt.OPHELD4.getOpcode(),
			ClientProt.OPHELD5.getOpcode(),
			ClientProt.OPHELD6.getOpcode() };

	@Override
	public OpHeldEvent decode(Player player, int opcode, RSBuffer buffer) {
		var slotId = -1;
		var itemId = -1;
		val option = ArrayUtils.indexOf(OPCODES, opcode) + 1;
		if (opcode == OPCODES[0]) {
			itemId = buffer.readShortLE() & 0xFFFF;
			buffer.readIntV1();
			slotId = buffer.readShort();
		} else if (opcode == OPCODES[1]) {
			itemId = buffer.readShort128() & 0xFFFF;
			buffer.readIntV2();
			slotId = buffer.readShortLE128();
		} else if (opcode == OPCODES[2]) {
			buffer.readIntLE();
			slotId = buffer.readShortLE128();
			itemId = buffer.readShortLE128() & 0xFFFF;
		} else if (opcode == OPCODES[3]) {
			itemId = buffer.readShortLE128() & 0xFFFF;
			buffer.readInt();
			slotId = buffer.readShortLE128();
		} else if (opcode == OPCODES[4]) {
			buffer.readInt();
			slotId = buffer.readShort();
			itemId = buffer.readShort() & 0xFFFF;
		} else if (opcode == OPCODES[5]) {
			itemId = buffer.readShortLE() & 0xFFFF;
			slotId = -1;
		}
		if (itemId == 0xFFFF) {
			itemId = -1;
		}
		return new OpHeldEvent(slotId, itemId, option);
	}
}
