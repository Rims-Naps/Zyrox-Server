package com.zenyte.game.world.entity.player.update.mask;

import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.masks.UpdateFlags;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.update.UpdateMask;
import com.zenyte.network.io.RSBuffer;
import lombok.val;

/**
 * @author Kris | 7. mai 2018 : 17:02:41
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class FaceEntityMask extends UpdateMask {

	@Override
	public UpdateFlag getFlag() {
		return UpdateFlag.FACE_ENTITY;
	}
	
	@Override
	public boolean apply(Player player, final Entity entity, final UpdateFlags flags, final boolean added) {
		return flags.get(UpdateFlag.FACE_ENTITY) || entity.getFaceEntity() != -1;
	}

	@Override
	public void writePlayer(final RSBuffer buffer, final Player player, final Player processedPlayer) {
		buffer.writeShort128(processedPlayer.getFaceEntity());
	}

	@Override
	public void writeNPC(final RSBuffer buffer, final Player player, final NPC npc) {
		buffer.writeShortLE(npc.getFaceEntity());
	}

}
