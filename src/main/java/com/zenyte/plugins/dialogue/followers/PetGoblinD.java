package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * Matt
 */
public final class PetGoblinD extends Dialogue {

	public PetGoblinD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		player("You look like someone who would lose us money..");
		npc("Yeah you can say that..");
	}

}
