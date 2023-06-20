package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Matt
 */
public final class SpookyBatD extends Dialogue {

	public SpookyBatD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		player("EEK! A bat!");
		npc("Squeak!");
	}
}