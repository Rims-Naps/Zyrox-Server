package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Cresinkel
 */
public final class PuppadileD extends Dialogue {

	public PuppadileD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		val random = Utils.random(2);
		if (random == 0) {
			player("What lovely teeth you have!");
			npc("All the better for chomping stuff.");
			player("WOW! No chill?");
			npc("Hey, it's a dog-eat-dogadile world out there!");
		} else if (random == 1) {
			player("Want to play fetch?");
			npc("Play meat!");
			player("No, fetch... you know, I throw stick and you bring it back?");
			npc("You fetch meat.");
			player("It doesn't just grow on trees you know!");
			npc("...");
		} else {
			npc("Om nom nom good meat.");
		}
	}
}
