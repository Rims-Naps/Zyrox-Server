package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Matt
 */
public final class PetLavaDragonD extends Dialogue {

	public PetLavaDragonD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("Are you the person the dragonkin have been telling legends about?");
		player("What do you mean?");
		npc("You have hoarded so much gold that the dragons envy you! But I think that they are starting to want what you have...");
		npc("I'd watch your back if I were you human, you never know when they might come for it! Don't worry about me though, I have no such interest in those matters...");
		player("Thanks for the heads up I guess.");

	}
}
