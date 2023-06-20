package com.zenyte.plugins.dialogue.followers;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import lombok.val;

/**
 * @author Cresinkel
 */
public final class VanguardD extends Dialogue {

	public VanguardD(final Player player, final NPC npc) {
		super(player, npc);
	}

	@Override
	public void buildDialogue() {
		npc("Learn ye of the Judgement of the Vanguard. The form we have taken.");
		player("But you're so little and cute!");
		npc("Learn ye this form was not given from the grace of our Lord Xeric. He does not forgive those who choose poorly.");
	}
}
