package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.constants.ClientProt;
import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.OpNpcEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Tommeh | 28 jul. 2018 | 19:56:33
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpNpcDecoder implements ClientProtDecoder<OpNpcEvent> {

	public static final int[] OPCODES = {
			ClientProt.OPNPC1.getOpcode(),
			ClientProt.OPNPC2.getOpcode(),
			ClientProt.OPNPC3.getOpcode(),
			ClientProt.OPNPC4.getOpcode(),
			ClientProt.OPNPC5.getOpcode(),
			/*ClientProt.OPNPC6.getOpcode()*/
	};

	@Override
	public OpNpcEvent decode(Player player, int opcode, RSBuffer buffer) {
    	var run = false;
    	var index = -1;
		val option = ArrayUtils.indexOf(OPCODES, opcode) + 1;
		if (opcode == OPCODES[0]) {
			run = buffer.readByteC() == 1;
			index = buffer.readShortLE();
		} else if (opcode == OPCODES[1]) {
			index = buffer.readShort128();
			run = buffer.readByte() == 1;
		}  else if (opcode == OPCODES[2]) {
			index = buffer.readShortLE128();
			run = buffer.readByte() == 1;
		} else if (opcode == OPCODES[3]) {
			index = buffer.readShort();
			run = buffer.readByte128() == 1;
		} else if (opcode == OPCODES[4]) {
			run = buffer.readByte() == 1;
			index = buffer.readShort128();
		}
		return new OpNpcEvent(index, option, run);
	}
}
