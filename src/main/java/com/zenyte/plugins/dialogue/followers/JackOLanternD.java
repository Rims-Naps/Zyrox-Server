package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Matt
 */
public final class JackOLanternD extends Dialogue {

	public JackOLanternD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("Listen friend.. I have a family too...");
		npc("If you don't try to light me like Felix's water pipe...");
		npc("I would like to wish you a Happy Halloween from the Pixies Team, " + player);
	}
}
