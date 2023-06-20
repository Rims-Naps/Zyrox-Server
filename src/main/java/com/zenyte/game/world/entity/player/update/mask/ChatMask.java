package com.zenyte.game.world.entity.player.update.mask;

import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.player.Device;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.update.UpdateMask;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Kris | 7. mai 2018 : 17:15:09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ChatMask extends UpdateMask {

	@Override
	public UpdateFlag getFlag() {
		return UpdateFlag.CHAT;
	}

	@Override
	public void writePlayer(final RSBuffer buffer, final Player player, final Player processedPlayer) {
		val message = processedPlayer.getChatMessage();
		buffer.writeShort128(message.getEffects());
		buffer.writeShort(processedPlayer.getPrimaryIcon() | (processedPlayer.getSecondaryIcon() << 5) | (processedPlayer.getTertiaryIcon() << 10));
		buffer.writeByteC((byte) (message.isAutotyper() ? 1 : 0));
		val offset = message.getOffset();
		buffer.writeByte(offset);
		buffer.writeBytes(message.getCompressedArray(), 0, offset);
	}

}
