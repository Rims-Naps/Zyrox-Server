package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.constants.ClientProt;
import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpObjEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Tommeh | 28 jul. 2018 | 19:30:58
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpObjDecoder implements ClientProtDecoder<OpObjEvent> {

	public static final int[] OPCODES = {
			ClientProt.OPOBJ1.getOpcode(),
			ClientProt.OPOBJ2.getOpcode(),
			ClientProt.OPOBJ3.getOpcode(),
			ClientProt.OPOBJ4.getOpcode(),
			ClientProt.OPOBJ5.getOpcode()
	};


	@Override
	public OpObjEvent decode(Player player, int opcode, RSBuffer buffer) {
		var itemId = -1;
		var x = -1;
		var y = -1;
		var run = false;
		val option = ArrayUtils.indexOf(OPCODES, opcode) + 1;
		if (opcode == OPCODES[0]) {
			y = buffer.readShortLE128();
			itemId = buffer.readShortLE();
			x = buffer.readShortLE();
			run = buffer.readByte() == 1;
		} else if (opcode == OPCODES[1]) {
			itemId = buffer.readShortLE128();
			x = buffer.readShort128();
			y = buffer.readShort();
			run = buffer.readByte() == 1;
		} else if (opcode == OPCODES[2]) {
			y = buffer.readShortLE();
			run = buffer.read128Byte() == 1;
			x = buffer.readShortLE();
			itemId = buffer.readShortLE();
		} else if (opcode == OPCODES[3]) {
			y = buffer.readShort128();
			itemId = buffer.readShortLE();
			x = buffer.readShort();
			run = buffer.read128Byte() == 1;
		} else if (opcode == OPCODES[4]) {
			run = buffer.readByte() == 1;
			itemId = buffer.readShortLE128();
			y = buffer.readShort128();
			x = buffer.readShort();
		}
		return new OpObjEvent(itemId, x, y, option, run);
	}
}
