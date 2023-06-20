package com.zenyte.game.packet.in.decoder;

import com.zenyte.game.constants.ClientProt;
import com.zenyte.game.packet.ClientProtDecoder;
import com.zenyte.game.packet.in.event.If3ButtonEvent;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.network.io.RSBuffer;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Tommeh | 28 jul. 2018 | 16:04:14
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class If3ButtonActionDecoder implements ClientProtDecoder<If3ButtonEvent> {

	public static final int[] OPCODES = { ClientProt.IF3_ACTION1.getOpcode(),
			ClientProt.IF3_ACTION2.getOpcode(), ClientProt.IF3_ACTION3.getOpcode(),
			ClientProt.IF3_ACTION4.getOpcode(), ClientProt.IF3_ACTION5.getOpcode(),
			ClientProt.IF3_ACTION6.getOpcode(), ClientProt.IF3_ACTION7.getOpcode(),
			ClientProt.IF3_ACTION8.getOpcode(), ClientProt.IF3_ACTION9.getOpcode(),
			ClientProt.IF3_ACTION10.getOpcode() };

	@Override
	public If3ButtonEvent decode(Player player, int opcode, RSBuffer buffer) {
		val compressed = buffer.readInt();
		int slotId = buffer.readShort() & 0xFFFF;
		int itemId = buffer.readShort() & 0xFFFF;
		val interfaceId = (compressed >> 16);
		val componentId = (compressed & 0xFFFF);
		val option = ArrayUtils.indexOf(OPCODES, opcode) + 1;
		return new If3ButtonEvent(interfaceId, componentId, slotId, itemId, option);
	}
}
