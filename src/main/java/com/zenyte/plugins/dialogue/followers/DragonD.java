package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Itzdeath
 */

public final class DragonD extends Dialogue {
	
	public DragonD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("Raawwwwwr!");
		player("*If my dragon gets any larger.. It might try to eat me..*");
	}
}


