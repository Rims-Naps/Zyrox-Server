package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Matt
 */
public final class DogD extends Dialogue {

	public DogD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("Woof?");
		npc("*Your dog nudges your leg*");
		npc("Arrroooooooooo!");
	}
}
