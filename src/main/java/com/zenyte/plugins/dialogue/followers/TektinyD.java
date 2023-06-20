package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Cresinkel
 */
public final class TektinyD extends Dialogue {

	public TektinyD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		val random = Utils.random(1);
		if (random == 0) {
			player("You look hot.");
			npc("Heat required for forge.");
			player("What are you making?");
			npc("Heat.");
			player("Well, forge ahead then.");
		} else {
			player("What are you..");
			npc("STOP!");
			player("???");
			npc("HAMMER TIME!");
		}
	}
}
