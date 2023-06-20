package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Cresinkel
 */
public final class VespinaD extends Dialogue {

	public VespinaD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		val random = Utils.random(1);
		if (random == 0) {
			player("Hello");
			npc("Bzzzzt!");
		} else {
			player("Bzzz bzzz bzz?");
			npc("Buzz off human.");
			player("It's you that's following me! Maybe I should invest in a large flyswat...");
			npc("Bzzzzt!");
		}
	}
}
