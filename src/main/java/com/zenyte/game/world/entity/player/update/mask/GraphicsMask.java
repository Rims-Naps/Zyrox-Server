package com.zenyte.game.world.entity.player.update.mask;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.update.UpdateMask;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Kris | 7. mai 2018 : 16:40:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class GraphicsMask extends UpdateMask {

	@Override
	public UpdateFlag getFlag() {
		return UpdateFlag.GRAPHICS;
	}

	@Override
	public void writePlayer(final RSBuffer buffer, final Player player, final Player processedPlayer) {
		val graphics = Utils.getOrDefault(processedPlayer.getGraphics(), Graphics.RESET);
		buffer.writeShortLE128(graphics.getId());
		buffer.writeIntV1(graphics.getDelay() | graphics.getHeight() << 16);
	}

	@Override
	public void writeNPC(final RSBuffer buffer, final Player player, final NPC npc) {
        val graphics = Utils.getOrDefault(npc.getGraphics(), Graphics.RESET);
		buffer.writeShortLE(graphics.getId());
		buffer.writeIntV1(graphics.getDelay() | graphics.getHeight() << 16);
	}

}
