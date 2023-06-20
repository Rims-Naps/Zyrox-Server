package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Cresinkel
 */
public final class VasaMinirioD extends Dialogue {

	public VasaMinirioD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("The Dark Altar! The power it has given. Xeric, you cannot comprehend!");
		player("Excuse me?");
		npc(" I will take Kourend for myself, for I am no longer the priest. I am the god!");
		player("What have I done...");
	}
}
