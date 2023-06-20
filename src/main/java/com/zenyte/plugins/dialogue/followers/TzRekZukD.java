package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Cresinkel
 */
public final class TzRekZukD extends Dialogue {

	public TzRekZukD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		val random = Utils.random(2);
		if (random == 0) {
			player("What's up Zuk?");
			npc("Feeling a bit down to be honest.");
			player("Why's that?");
			npc("Well...");
			npc("Not so long ago, I was a big fearsome boss, Now I'm just another pet.");
			player("Indeed, and you're going to follow me everywhere I go.");
		} else if (random == 1) {
			player("Why have you got lava around your feet?");
			npc("Keeps me cool.");
			player("But... lava is hot?");
			npc("No no, I wasn't referring to the temperature.");
			player("Ah...");
		} else {
			player("You're a lot smaller now, I don't even need a shield.");
			npc("Mere mortal, you only survived my challenge because of that convenient pile of rock.");
			player("Well, you couldn't even break that pile of rock to get at me!");
			npc("...");
		}
	}
}
