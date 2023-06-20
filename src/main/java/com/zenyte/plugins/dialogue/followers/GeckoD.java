package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Matt
 */
public final class GeckoD extends Dialogue {

	public GeckoD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		player("There seems to be a note stuck to it...");
		player("It reads: Save 15% or more on your pet insurance. Probably a scam.");
	}
}

