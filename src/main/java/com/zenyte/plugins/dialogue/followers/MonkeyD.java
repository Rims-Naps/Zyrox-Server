package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;

/**
 * @author Matt
 */
public final class MonkeyD extends Dialogue {

	public MonkeyD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("Ooh aah ee AAAH!");
		player("It appears I cannot understand monkey speak anymore...*");
	}
}
